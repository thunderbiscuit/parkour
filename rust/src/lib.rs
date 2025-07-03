use bark::{Config, KeychainKind, SqliteClient, Wallet as RustArkWallet};
use bitcoin::Network;
use std::path::Path;
use log::{error, info, debug};
use std::thread;
use std::env;
use std::str::FromStr;
use bip39::Mnemonic;
use std::sync::{Mutex, MutexGuard};

uniffi::setup_scaffolding!("ark");

const DB_FILE: &str = "parkour_db.sqlite3";

#[derive(uniffi::Object)]
pub struct ArkWallet {
    inner_mutex: Mutex<RustArkWallet>,
}

#[uniffi::export]
impl ArkWallet {
    #[uniffi::constructor()]
    pub fn new(
        file_path: &str,
        recovery_phrase: &str
    ) -> ArkWallet {
        android_logger::init_once(
            android_logger::Config::default()
                .with_max_level(log::LevelFilter::Trace)
                .with_tag("ArkWallet")
        );
        
        // Enable verbose logging for key crates
        env::set_var("RUST_LOG", "bark=trace,tonic=trace,hyper=trace,tower=trace");
        
        info!("Starting ArkWallet creation with enhanced logging");
        
        let file_path = file_path.to_string();
        let recovery_phrase = Mnemonic::from_str(recovery_phrase).unwrap();
        println!("{}", recovery_phrase);
        // let recovery_phrase = bip39::Mnemonic::generate(12).unwrap();
        // println!("{}", recovery_phrase);

        // Run wallet creation in a background thread to avoid Android's main thread network restrictions
        let handle = thread::spawn(move || {
            let path = Path::new(&file_path);
            let db = SqliteClient::open(path.join(DB_FILE)).unwrap();
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
            info!("Configuration: ASP={}, Network=Signet", config.asp_address);
            debug!("Full config: {:?}", config);
            
            let birthday = Some(250_000);
            info!("Creating Tokio runtime...");

            let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");
            info!("Tokio runtime created successfully");
            
            info!("Starting wallet creation with ASP handshake...");
            rt.block_on(async {
                debug!("Inside async block, about to call RustArkWallet::create");
                match RustArkWallet::create(&recovery_phrase, Network::Signet, config, db, birthday).await {
                    Ok(wallet) => {
                        info!("Wallet created successfully!");
                        Ok(wallet)
                    }
                    Err(e) => {
                        error!("Wallet creation failed with error: {:?}", e);
                        error!("Error source chain:");
                        let mut source = e.source();
                        let mut level = 1;
                        while let Some(err) = source {
                            error!("  Level {}: {:?}", level, err);
                            source = err.source();
                            level += 1;
                        }
                        Err(e)
                    }
                }
            })
        });
        
        let wallet = handle.join()
            .expect("Background thread panicked")
            .expect("Could not create Ark wallet");

        ArkWallet { inner_mutex: Mutex::new(wallet) }
    }

    #[uniffi::constructor()]
    pub fn load(
        file_path: &str,
        recovery_phrase: &str
    ) -> ArkWallet {
        android_logger::init_once(
            android_logger::Config::default()
                .with_max_level(log::LevelFilter::Trace)
                .with_tag("ArkWallet")
        );

        // Enable verbose logging for key crates
        env::set_var("RUST_LOG", "bark=trace,tonic=trace,hyper=trace,tower=trace");

        info!("Starting ArkWallet creation with enhanced logging");

        let file_path = file_path.to_string();
        let recovery_phrase = Mnemonic::from_str(recovery_phrase).unwrap();
        println!("{}", recovery_phrase);

        // Run wallet creation in a background thread to avoid Android's main thread network restrictions
        let handle = thread::spawn(move || {
            let path = Path::new(&file_path);
            let db = SqliteClient::open(path.join(DB_FILE)).unwrap();

            let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");
            info!("Tokio runtime created successfully");

            info!("Starting wallet creation with ASP handshake...");
            rt.block_on(async {
                debug!("Inside async block, about to call RustArkWallet::create");
                match RustArkWallet::open(&recovery_phrase, db).await {
                    Ok(wallet) => {
                        info!("Wallet created successfully!");
                        Ok(wallet)
                    }
                    Err(e) => {
                        error!("Wallet creation failed with error: {:?}", e);
                        error!("Error source chain:");
                        let mut source = e.source();
                        let mut level = 1;
                        while let Some(err) = source {
                            error!("  Level {}: {:?}", level, err);
                            source = err.source();
                            level += 1;
                        }
                        Err(e)
                    }
                }
            })
        });

        let wallet = handle.join()
            .expect("Background thread panicked")
            .expect("Could not create Ark wallet");

        ArkWallet { inner_mutex: Mutex::new(wallet) }
    }

    pub fn onchain_balance(&self) -> u64 {
        self.get_wallet().onchain.balance().to_sat()
    }
    
    pub fn offchain_balance(&self) -> u64 {
        self.get_wallet().offchain_balance().unwrap().to_sat()
    }
    
    pub fn sync_ark(&self) {
        info!("Starting wallet sync");
        
        let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");
        info!("Tokio runtime created for sync");
        
        rt.block_on(async {
            match self.get_wallet().onchain.sync().await {
                Ok(_) => {
                    info!("Wallet onchain sync completed successfully");
                    println!("Wallet onchain sync completed successfully");
                }
                Err(e) => {
                    error!("Wallet sync failed with error: {:?}", e);
                    println!("Wallet onchain sync failed with error: {:?}", e);
                }
            }
            match self.get_wallet().sync_ark().await {
                Ok(_) => {
                    info!("Wallet sync completed successfully");
                    println!("Wallet sync completed successfully");
                }
                Err(e) => {
                    error!("Wallet sync failed with error: {:?}", e);
                    println!("Wallet sync failed with error: {:?}", e);
                }
            }
            match self.get_wallet().maintenance().await {
                Ok(_) => {
                    info!("Wallet maintenance completed successfully");
                    println!("Wallet maintenance completed successfully");
                }
                Err(e) => {
                    error!("Wallet maintenance failed with error: {:?}", e);
                    println!("Wallet maintenance failed with error: {:?}", e);
                }
            }
        })
    }

    pub fn maintenance(&self) {
        info!("Starting wallet sync");
        println!("Starting wallet sync");

        let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");
        println!("Tokio runtime created for sync");

        rt.block_on(async {
            match self.get_wallet().maintenance().await {
                Ok(_) => {
                    info!("Wallet maintenance completed successfully");
                    println!("Wallet maintenance completed successfully");
                }
                Err(e) => {
                    error!("Wallet maintenance failed with error: {:?}", e);
                    println!("Wallet maintenance failed with error: {:?}", e);
                }
            }
        })
    }

    pub fn vtxo_pubkey(&self) -> String {
        self.get_wallet()
            .derive_store_next_keypair(KeychainKind::External)
            .unwrap()
            .public_key()
            .to_string()
    }
}

impl ArkWallet {
    pub(crate) fn get_wallet(&self) -> MutexGuard<RustArkWallet> {
        self.inner_mutex.lock().expect("wallet")
    }
}
