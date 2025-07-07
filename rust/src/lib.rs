use bark::{Config, KeychainKind, SqliteClient, Wallet as RustArkWallet};
use bitcoin::Network;
use std::path::Path;
use std::thread;
use std::str::FromStr;
use bip39::Mnemonic;
use std::sync::{Mutex, MutexGuard};

const DB_FILE: &str = "parkour_db.sqlite3";

pub struct ArkWallet {
    inner_mutex: Mutex<RustArkWallet>,
}

impl ArkWallet {
    pub fn new(
        file_path: &str,
        recovery_phrase: &str
    ) -> ArkWallet {
        let file_path = file_path.to_string();
        let recovery_phrase = Mnemonic::from_str(recovery_phrase).unwrap();
        println!("{}", recovery_phrase);

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

            let birthday = Some(250_000);
            let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");

            rt.block_on(async {
                match RustArkWallet::create(&recovery_phrase, Network::Signet, config, db, birthday).await {
                    Ok(wallet) => {
                        println!("Wallet created successfully!");
                        Ok(wallet)
                    }
                    Err(e) => {
                        println!("Wallet creation failed with error: {:?}", e);
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

    pub fn maintenance(&self) {
        println!("Starting wallet maintenance");

        let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");
        rt.block_on(async {
            match self.get_wallet().maintenance().await {
                Ok(_) => {
                    println!("Wallet maintenance completed successfully");
                }
                Err(e) => {
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
