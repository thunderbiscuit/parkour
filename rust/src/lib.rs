use bark::{Config, KeychainKind, SqliteClient, Wallet as RustArkWallet};
use bitcoin::Network;
use std::path::Path;
use std::str::FromStr;
use bip39::Mnemonic;
use std::sync::{Arc, Mutex, MutexGuard};
use tokio::runtime::Runtime;

uniffi::setup_scaffolding!("ark");

const DB_FILE: &str = "parkour_db.sqlite3";

#[derive(uniffi::Object)]
pub struct ArkWallet {
    inner_mutex: Arc<Mutex<RustArkWallet>>,
    runtime: Arc<Runtime>,
}

#[uniffi::export]
impl ArkWallet {
    #[uniffi::constructor()]
    pub fn new(
        file_path: &str,
        recovery_phrase: &str
    ) -> Result<ArkWallet, ArkWalletError> {
        let recovery_phrase = Mnemonic::from_str(recovery_phrase)?;
        println!("{}", recovery_phrase);

        let path = Path::new(file_path);
        let db_path = path.join(DB_FILE);
        println!("Attempting to open database at: {:?}", db_path);
        
        // Create the directory if it doesn't exist
        if let Some(parent) = db_path.parent() {
            std::fs::create_dir_all(parent)?;
        }
        
        let db = SqliteClient::open(db_path.clone())?;
        let config = Config {
            asp_address: "https://ark.signet.2nd.dev".to_owned(),
            esplora_address: Some("https://esplora.signet.2nd.dev".to_owned()),
            bitcoind_address: None,
            bitcoind_cookiefile: None,
            bitcoind_user: None,
            bitcoind_pass: None,
            vtxo_refresh_expiry_threshold: 288,
            fallback_fee_rate: None,
        };

        let birthday = Some(250_000);

        // Create wallet and runtime using a dedicated thread
        let (wallet, runtime) = std::thread::spawn(move || {
            let rt = tokio::runtime::Runtime::new().map_err(|e| ArkWalletError::Error(e.to_string()))?;
            let wallet = rt.block_on(async {
                // Check if database exists and has wallet data, if so use open(), otherwise create()
                if db_path.exists() {
                    match RustArkWallet::open(&recovery_phrase, db).await {
                        Ok(wallet) => {
                            println!("Existing Ark wallet loaded successfully!");
                            Ok::<RustArkWallet, ArkWalletError>(wallet)
                        }
                        Err(_) => {
                            // If open fails, try to create (database might exist but be empty)
                            let db = SqliteClient::open(db_path)?;
                            println!("Could not load existing wallet, creating new one...");
                            let wallet = RustArkWallet::create(&recovery_phrase, Network::Signet, config, db, birthday).await?;
                            println!("Wallet created successfully!");
                            Ok::<RustArkWallet, ArkWalletError>(wallet)
                        }
                    }
                } else {
                    println!("Database doesn't exist, creating new wallet...");
                    let wallet = RustArkWallet::create(&recovery_phrase, Network::Signet, config, db, birthday).await?;
                    println!("Wallet created successfully!");
                    Ok::<RustArkWallet, ArkWalletError>(wallet)
                }
            })?;
            
            Ok::<(RustArkWallet, Runtime), ArkWalletError>((wallet, rt))
        }).join().map_err(|e| ArkWalletError::Error(format!("Thread join error: {:?}", e)))??;
        
        Ok(ArkWallet { 
            inner_mutex: Arc::new(Mutex::new(wallet)),
            runtime: Arc::new(runtime),
        })
    }

    pub fn onchain_balance(&self) -> u64 {
        self.get_wallet().onchain.balance().to_sat()
    }

    pub fn sync_onchain(&self) -> Result<(), ArkWalletError> {
        println!("Starting wallet maintenance");

        // Use spawn_blocking to avoid runtime conflicts
        let result = std::thread::spawn({
            let runtime = self.runtime.clone();
            let inner_mutex = self.inner_mutex.clone();
            move || {
                runtime.block_on(async {
                    let mut wallet = inner_mutex.lock().map_err(|e| ArkWalletError::Error(format!("Mutex lock error: {}", e)))?;
                    wallet.onchain.sync().await
                })
            }
        }).join().map_err(|e| ArkWalletError::Error(format!("Thread join error: {:?}", e)))?;

        result?;
        println!("Wallet maintenance completed successfully");
        Ok(())
    }

    pub fn onchain_address(&self) -> Result<String, ArkWalletError> {
        Ok(self.get_wallet().onchain.address()?.to_string())
    }

    pub fn offchain_balance(&self) -> Result<u64, ArkWalletError> {
        Ok(self.get_wallet().offchain_balance()?.to_sat())
    }

    pub fn maintenance(&self) -> Result<(), ArkWalletError> {
        println!("Starting wallet maintenance");
        
        // Use spawn_blocking to avoid runtime conflicts
        let result = std::thread::spawn({
            let runtime = self.runtime.clone();
            let inner_mutex = self.inner_mutex.clone();
            move || {
                runtime.block_on(async {
                    let mut wallet = inner_mutex.lock().map_err(|e| ArkWalletError::Error(format!("Mutex lock error: {}", e)))?;
                    wallet.maintenance().await
                })
            }
        }).join().map_err(|e| ArkWalletError::Error(format!("Thread join error: {:?}", e)))?;
        
        result?;
        println!("Wallet maintenance completed successfully");
        Ok(())
    }

    pub fn vtxo_pubkey(&self) -> Result<String, ArkWalletError> {
        Ok(self.get_wallet()
            .derive_store_next_keypair(KeychainKind::External)?
            .public_key()
            .to_string())
    }
}

impl ArkWallet {
    pub(crate) fn get_wallet(&self) -> MutexGuard<RustArkWallet> {
        self.inner_mutex.lock().unwrap_or_else(|poisoned| {
            println!("Wallet mutex was poisoned, recovering...");
            poisoned.into_inner()
        })
    }
}

#[derive(Debug, uniffi::Error)]
pub enum ArkWalletError {
    Error(String),
}

impl std::fmt::Display for ArkWalletError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            ArkWalletError::Error(msg) => write!(f, "{}", msg),
        }
    }
}

impl std::error::Error for ArkWalletError {}

impl From<uniffi::deps::anyhow::Error> for ArkWalletError {
    fn from(err: uniffi::deps::anyhow::Error) -> Self {
        ArkWalletError::Error(err.to_string())
    }
}

impl From<bip39::Error> for ArkWalletError {
    fn from(err: bip39::Error) -> Self {
        ArkWalletError::Error(err.to_string())
    }
}

impl From<std::io::Error> for ArkWalletError {
    fn from(err: std::io::Error) -> Self {
        ArkWalletError::Error(err.to_string())
    }
}

impl From<tokio::task::JoinError> for ArkWalletError {
    fn from(err: tokio::task::JoinError) -> Self {
        ArkWalletError::Error(format!("Task join error: {}", err))
    }
}
