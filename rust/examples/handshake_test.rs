use ark_ffi::HandshakeTester;

fn main() {
    println!("ARK Handshake Tester");
    println!("================================");

    // Create the handshake tester
    let tester = HandshakeTester::new();

    // Test with the default ASP endpoint
    let asp_url = "https://ark.signet.2nd.dev".to_string();
    // let asp_url = "https://91.134.58.91".to_string();
    // let asp_url = "https://91.134.58.91:443".to_string();
    println!("\n🔗 Testing handshake with ASP: {}", asp_url);

    // Test without timeout first
    println!("Running handshake test...");
    let result = tester.test_handshake(asp_url.clone());
    println!("Result:\n{}", result);

    // Test with timeout (useful for mobile testing)
    println!("Running handshake test with 30s timeout...");
    let result_with_timeout = tester.test_handshake_with_timeout(asp_url, 30);
    println!("Result with timeout:\n{}", result_with_timeout);

    println!("Handshake testing complete!");
}
