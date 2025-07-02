use ark_ffi::ArkWallet;

const RECOVERY_PHRASE:  &str = "era film bless deposit agent either list galaxy guilt layer start squirrel";
const DB_PATH: &str = "./examples/";

fn main() {
    println!("Building an ArkWallet example");

    let wallet = ArkWallet::new(DB_PATH, RECOVERY_PHRASE);
    println!("ArkWallet created successfully!");

    let balance = wallet.onchain_balance();
    println!("Onchain balance: {} sats", balance);

    let pubkey = wallet.vtxo_pubkey();
    println!("Wallet public key: {}", pubkey);

    println!("Calling offchain_balance...");
    let ark_balance = wallet.offchain_balance();
    println!("Offchain balance: {} sats", ark_balance);

    println!("Attempting to sync...");
    wallet.sync_ark();
    // wallet.maintenance();

    println!("Maintenance completed, checking balance again...");
    let post_sync_ark_balance = wallet.offchain_balance();
    println!("After sync offchain balance: {} sats", post_sync_ark_balance);

    println!("Example completed.");
}
