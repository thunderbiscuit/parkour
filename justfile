[group("Root")]
[doc("List all available commands.")]
@default:
  just --list --unsorted

[group("Root")]
[doc("Open repository on GitHub.")]
repo:
  open https://github.com/thunderbiscuit/parkour

[group("Rust library")]
[doc("Build the Rust library.")]
buildrustlib:
  cd ./rust/ && source build.sh
