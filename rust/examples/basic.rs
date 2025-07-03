use ark_ffi::ArkWallet;

const RECOVERY_PHRASE:  &str = "era film bless deposit agent either list galaxy guilt layer start squirrel";
const DB_PATH: &str = "./examples/";

fn main() {
    println!("Building an ArkWallet example");

    let wallet = ArkWallet::new(DB_PATH, RECOVERY_PHRASE);
    let balance = wallet.onchain_balance();
    let ark_balance = wallet.offchain_balance();
    let pubkey = wallet.vtxo_pubkey();

    println!("ArkWallet created successfully!");
    println!("Onchain balance: {} sats", balance);
    println!("Offchain balance: {} sats", ark_balance);
    println!("Wallet public key: {}", pubkey);

    println!("Attempting to sync...");
    // wallet.sync_ark();
    wallet.maintenance();

    println!("Maintenance completed, checking balance again...");
    let post_maintenance_ark_balance = wallet.offchain_balance();
    println!("After sync offchain balance: {} sats", post_maintenance_ark_balance);
}
