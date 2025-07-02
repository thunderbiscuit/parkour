use bark::{Config, KeychainKind, SqliteClient, Wallet as RustArkWallet};
use bitcoin::Network;
use std::path::Path;
use log::{error, info, debug};
use std::thread;
use std::env;
use std::str::FromStr;
use bip39::Mnemonic;
use std::sync::{Mutex, MutexGuard};
use std::time::Duration;

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

pub struct HandshakeTester;

impl HandshakeTester {
    #[uniffi::constructor()]
    pub fn new() -> HandshakeTester {
        android_logger::init_once(
            android_logger::Config::default()
                .with_max_level(log::LevelFilter::Trace)
                .with_tag("HandshakeTester")
        );

        env::set_var("RUST_LOG", "handshake_tester=trace,tonic=trace,hyper=trace,tower=trace");

        HandshakeTester
    }

    pub fn test_handshake(&self, asp_url: String) -> String {
        info!("Starting handshake test with ASP: {}", asp_url);

        let handle = thread::spawn(move || {
            let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");
            info!("Tokio runtime created for handshake test");

            rt.block_on(async {
                // Use webpki-only for Android compatibility
                match perform_handshake_test(&asp_url).await {
                    Ok(response) => {
                        info!("Handshake test successful: {}", response);
                        response
                    }
                    Err(e) => {
                        error!("Handshake test failed: {:?}", e);
                        format!("ERROR: {}", e)
                    }
                }
            })
        });

        handle.join()
            .expect("Background thread panicked")
    }

    pub fn test_handshake_with_timeout(&self, asp_url: String, timeout_secs: u64) -> String {
        info!("Starting handshake test with timeout {}s for ASP: {}", timeout_secs, asp_url);

        let handle = thread::spawn(move || {
            let rt = tokio::runtime::Runtime::new().expect("Could not create Tokio runtime");
            info!("Tokio runtime created for handshake test with timeout");

            rt.block_on(async {
                // Use webpki-only for Android compatibility
                match tokio::time::timeout(
                    Duration::from_secs(timeout_secs),
                    perform_handshake_test(&asp_url)
                ).await {
                    Ok(Ok(response)) => {
                        info!("Handshake test successful within timeout: {}", response);
                        response
                    }
                    Ok(Err(e)) => {
                        error!("Handshake test failed: {:?}", e);
                        format!("ERROR: {}", e)
                    }
                    Err(_) => {
                        error!("Handshake test timed out after {}s", timeout_secs);
                        format!("TIMEOUT: Handshake test timed out after {}s", timeout_secs)
                    }
                }
            })
        });

        handle.join()
            .expect("Background thread panicked")
    }
}

async fn perform_handshake_test(asp_url: &str) -> Result<String, Box<dyn std::error::Error + Send + Sync>> {
    use aspd_rpc::ArkServiceClient;
    use aspd_rpc::protos::HandshakeRequest;
    use tonic::transport::{Endpoint, ClientTlsConfig};

    debug!("Attempting to connect to ASP at: {}", asp_url);

    // Configure TLS for Android compatibility - webpki-roots only
    let tls_config = ClientTlsConfig::new()
        .with_webpki_roots();

    let endpoint = Endpoint::from_shared(asp_url.to_string())?
        .tls_config(tls_config)?
        .connect_timeout(Duration::from_secs(30))
        .timeout(Duration::from_secs(30));

    debug!("Configured webpki-only endpoint for Android");

    // Create gRPC client
    let channel = endpoint.connect().await
        .map_err(|e| {
            error!("Failed to connect to ASP: {:?}", e);
            e
        })?;

    let mut client = ArkServiceClient::new(channel);

    info!("Successfully connected to ASP, sending handshake request");

    // Create handshake request
    let request = HandshakeRequest {
        version: "0.1.0".to_string(),
    };

    debug!("Sending handshake request: {:?}", request);

    // Send handshake
    let response = client.handshake(request).await
        .map_err(|e| {
            error!("Handshake request failed: {:?}", e);
            e
        })?;

    let handshake_response = response.into_inner();
    info!("Received handshake response: {:?}", handshake_response);

    // Format response for easier reading
    let mut result = String::new();
    result.push_str("HANDSHAKE SUCCESS\n");

    if let Some(psa) = &handshake_response.psa {
        result.push_str(&format!("PSA: {}\n", psa));
    }

    if let Some(error) = &handshake_response.error {
        result.push_str(&format!("ERROR: {}\n", error));
    }

    if let Some(ark_info) = &handshake_response.ark_info {
        result.push_str("ARK INFO:\n");
        result.push_str(&format!("  Network: {}\n", ark_info.network));
        result.push_str(&format!("  ASP Pubkey: {}\n", hex::encode(&ark_info.asp_pubkey)));
        result.push_str(&format!("  Round Interval: {}s\n", ark_info.round_interval_secs));
        result.push_str(&format!("  Nb Round Nonces: {}\n", ark_info.nb_round_nonces));
        result.push_str(&format!("  VTXO Exit Delta: {}\n", ark_info.vtxo_exit_delta));
        result.push_str(&format!("  VTXO Expiry Delta: {}\n", ark_info.vtxo_expiry_delta));
        result.push_str(&format!("  HTLC Expiry Delta: {}\n", ark_info.htlc_expiry_delta));
        if let Some(max_amount) = ark_info.max_vtxo_amount {
            result.push_str(&format!("  Max VTXO Amount: {}\n", max_amount));
        }
        result.push_str(&format!("  Max Arkoor Depth: {}\n", ark_info.max_arkoor_depth));
    }

    Ok(result)
}
