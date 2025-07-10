use anyhow::Result;
use bark::{Config, Wallet};
use bip39::Mnemonic;
use bitcoin::Network;
use std::path::PathBuf;
use std::str::FromStr;

#[tokio::main]
async fn main() -> Result<()> {
    // Hard-coded seed phrase for demonstration
    let seed_phrase = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
    let mnemonic = Mnemonic::from_str(seed_phrase)?;

    // Hard-coded configuration
    let config = Config {
        asp_address: "https://ark.signet.2nd.dev".to_string(),
        esplora_address: Some("https://esplora.signet.2nd.dev".to_string()),
        bitcoind_address: None,
        bitcoind_cookiefile: None,
        bitcoind_user: None,
        bitcoind_pass: None,
        vtxo_refresh_expiry_threshold: 288,
        fallback_fee_rate: None,
    };

    // Create a new SQLite database client (using a file in the bark directory)
    let db_path = PathBuf::from("./examples/just_bark.sqlite3");
    let db = bark::SqliteClient::open(db_path.clone())?;

    // Check if database exists and has wallet data, if so use open(), otherwise create()
    let mut wallet = if db_path.exists() {
        match Wallet::open(&mnemonic, db).await {
            Ok(wallet) => {
                println!("✅ Existing Ark wallet loaded successfully!");
                wallet
            }
            Err(_) => {
                // If open fails, try to create (database might exist but be empty)
                let db = bark::SqliteClient::open(db_path)?;
                println!("⚠️  Could not load existing wallet, creating new one...");
                Wallet::create(
                    &mnemonic,
                    Network::Signet,
                    config,
                    db,
                    None, // mnemonic_birthday
                ).await?
            }
        }
    } else {
        println!("📁 Database doesn't exist, creating new wallet...");
        Wallet::create(
            &mnemonic,
            Network::Signet,
            config,
            db,
            None, // mnemonic_birthday
        ).await?
    };

    println!("✅ Ark wallet ready!");

    // Get the first VTXO public key
    let first_vtxo_pubkey = wallet.derive_store_next_keypair(bark::KeychainKind::External)?;
    println!("🔑 First VTXO public key: {}", first_vtxo_pubkey.public_key());

    // Check wallet balance using maintenance method
    println!("🔄 Running wallet maintenance...");
    wallet.maintenance().await?;

    let balance = wallet.offchain_balance()?;
    println!("💰 Wallet balance: {} satoshis", balance.to_sat());

    Ok(())
}