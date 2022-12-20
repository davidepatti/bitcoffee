# bitcoffee
 From scratch, zero-dependencies Bitcoin internals implementation in Java, based on the classic Jimmy Song's book https://programmingbitcoin.com/
 
 Please notice:
 * I wrote this project for fun,learning and reaserch purposes, feel free to use it as you like, but not in production, e.g. never use it to move your sats on the mainnet:)
 * Althought is not intended to be an exact replica of the python code presented in the book above, the majority of the tests and exercises have been replicated in the Tests package of the project.
 
 News:
 * BIP39 seedphrase support for Hierarchical Deterministic wallets
 
 Currently implemented:
 
 * From scratch Crypto-primitives: Finite field point math , Elliptic Curve Digital Signature Algorithm (secp256k1), SEC (compressed/uncompressed) , DER, SHA256, RIPEMD160, encode Base58, little/big endian and hex utilities, Varint econding
 * Transactions serialization and parsing
 * Bitcoin Script language parsing and execution
 * Proof-of-work, Difficulty adjustment
 * Connection to external nodes to fetch and validate blocks
 * SPV, Merkle Trees, Bloom filters
 * Segregated Witness: p2wpkh, p2sh-p2wpkh, p2wsh, p2sh-p2wsh
 * Command line client for testing the library

# Usage
You can import the whole repository from github as a IntelliJ project or just download the code.
There are several way to interact with bitcoffee:
* Command line: bitcoffee.java implements a basic commmand line for some of the library functions
* Check the Test classes in the Test directory to check how each functionality can be invoked. Tip: when using IntelliJ, right-click on a Test class to create a run starting point 
* A simple gui (see GUI/Dashboard) to use some functionality (still to be completed)

In alternative, you can check the code in the Tests packages to see how the single functionalities can be invoked.



* ...feel free to suggest at xedivad@gmail.com 

Support the Lightning Network, connect to node:
03740f82191202480ace717fcdf00f71a8b1eb9bdc2bb5e2106cd0ab5cb4d7a54e
