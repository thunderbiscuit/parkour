uniffi::setup_scaffolding!("ark");

#[derive(uniffi::Object)]
struct Calendar {
    id: i32,
    name: String,
}

#[uniffi::export]
impl Calendar {
    #[uniffi::constructor]
    fn new(id: i32, name: String) -> Calendar {
        Calendar {
            id,
            name,
        }
    }
}
