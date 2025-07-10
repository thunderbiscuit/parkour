use ark_ffi::ArkWallet;

const RECOVERY_PHRASE:  &str = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
const DB_PATH: &str = "./examples/";

fn main() -> Result<(), Box<dyn std::error::Error>> {
    println!("Building an ArkWallet example");

    let wallet = ArkWallet::new(DB_PATH, RECOVERY_PHRASE)?;
    let address = wallet.onchain_address().unwrap();

    println!("ArkWallet created successfully!");
    println!("Onchain balance: {} sats", address);

    Ok(())
}
