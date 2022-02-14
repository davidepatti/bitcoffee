# bitcoffee
 Java-based Bitcoin internals implementation, based on the classic Jimmy Song's book https://programmingbitcoin.com/
 Please notice:
 * I wrote this project for learning and reaserch purposes, feel free to use it as you like, but not in production, e.g. dont use it to create and broadcast your bitcoin on mainnet:)
 * Althought is not intended to be an exact replica of the python code presented in the book above, the majority of the tests and exercises have been implemented in the Tests package of the project.
 
 Currently implemented (updated on Feb 14, 2022):
 
 * From scratch Crypto-primitives: Finite field point math , Elliptic Curve Digital Signature Algorithm (secp256k1), SEC (compressed/uncompressed) , DER, SHA256, RIPEMD160, encode Base58, little/big endian and hex utilities, Varint econding
 * Transactions serialization and parsing
 * Bitcoin Script language parsing and execution
 * Proof-of-work, Difficulty adjustment
 * Connection to external nodes to fetch and validate blocks
 * SPV, Merkle Trees, Bloom filters
 * Segregated Witness: p2wpkh, p2sh-p2wpkh, p2wsh, p2sh-p2wsh
 * Command line client for testing the library
