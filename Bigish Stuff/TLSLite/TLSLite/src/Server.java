/*
 * Author: Jason Ablott
 * Date First Version Completed: 03/28/2025
 * Date Most Recently Modified: 03/28/3035
 * Project: TLSLite
 * For: CS 6014 Networks and Security, University of Utah MSD Program
 */

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/*
 * The server class represents a server able to communicate via the TLSLite protocol described in about.txt.
 * The server uses a TCP connection and implements a TLS like protocol to ensure secure connection and messaging.
 */
public class Server {

    // <----------------------------------------------------------------------------------------SERVER MEMBER VARIABLES>
    // SERVER INFORMATION
    private static final int SERVER_PORT = 5679; // My port number

    // KEYS AND CERTIFICATES
    private PrivateKey serverPrivateKey; // My private key
    private BigInteger clientDHPublicKey;// The public key of the client I am communicating with
    private Certificate serverCertificate; // My certificate
    private Certificate CACertificate; // CA certificate for verifying client's certificate

    // DIFFIE-HELLMAN PIECES
    private BigInteger DiffieHellmanN; // N parameter for calculations
    private BigInteger DiffieHellmang; // g parameter for calculations
    private BigInteger serverDHPrivateKey; // My private DH key
    private BigInteger serverDHPublicKey; // My public DH key
    private BigInteger SharedSecret; // Shared secret from DH to generate session keys

    // SESSION KEYS
    private SecretKeySpec serverEncryptKey;
    private SecretKeySpec clientEncryptKey;
    private SecretKeySpec serverMACKey;
    private SecretKeySpec clientMACKey;
    private IvParameterSpec serverIVSpec;
    private IvParameterSpec clientIVSpec;

    // STREAMS FOR COMMUNICATING
    static InputStream Input;
    static OutputStream Output;

    // OTHER
    private SecureRandom myRandom; // Secure Random object for generating random numbers in several places
    private byte[] nonce; // Nonce from client
    HMACData hmacData; // Object to store data for HMAC messages at end of handshake

    // <------------------------------------------------------------------------------------------------------MAIN CODE>
    public static void main(String[] args) {
        try {
            Server server = new Server(); // Get a server instance
            server.initialize(); // initialize the server
            server.listen(); // listen for and handle client connections and
            System.out.print("\nEnd of Main code, server done running");
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }

    // <-------------------------------------------------------------------------------------------------INITIALIZATION>
    // method to initialize the server state before initializing socket
    public void initialize() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
        System.out.println("\nInitializing server...");
        this.hmacData = new HMACData(); // initialize an HMACData object to use
        myRandom = new SecureRandom(); // generate our random object for use
        calculateDHparams(); // calculates DH g and N
        generateDHKeyPair();// generate server DH keys
        loadKeys(); // loads necessary keys
        // Set access to the Server's keystore for use later
        System.setProperty("javax.net.ssl.keyStore", "src/ServerKeyStore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456!");
    }

    // method to calculate g and N for DH from RFC 3526 2048 bit
    private void calculateDHparams() {
        System.out.println("    Calculating DH params...");
        double pi = Math.PI;
        BigInteger power2048 = BigInteger.ONE.shiftLeft(2048); //  2^2048
        BigInteger power1984 = BigInteger.ONE.shiftLeft(1984); //  2^1984
        BigInteger power64 = BigInteger.ONE.shiftLeft(64); //  2^64
        BigInteger power1918 = BigInteger.ONE.shiftLeft(1918); //  2^1918
        BigInteger term1 = power1918.multiply(BigInteger.valueOf((long) (pi * Math.pow(2, 1918))));
        BigInteger term2 = BigInteger.valueOf(124476);
        BigInteger part = term1.add(term2); // [2^1918 * pi] + 124476
        BigInteger term3 = part.multiply(power64); // 2^64 * ([2^1918 * pi] + 124476)
        // Set G and n for Diffie Hellman
        DiffieHellmanN = power2048.subtract(power1984).subtract(BigInteger.ONE).add(term3);
        DiffieHellmang = BigInteger.valueOf(2);
    }

    // method to load keys/cert during initialization
    private void loadKeys() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
        System.out.println("    Loading keys...");
        // load my (server) private key
        this.serverPrivateKey = loadPrivateKey("src/serverPrivateKey.der");
        // load my (server) certificate
        this.serverCertificate = loadCertificate("src/CASignedServerCertificate.pem");
        // load CA certificate
        this.CACertificate = loadCertificate("src/CAcertificate.pem");
    }

    // method to load the server's private key
    private PrivateKey loadPrivateKey(String filepath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("        Loading a private key...");
        // load the file that contains the key
        FileInputStream in = new FileInputStream(filepath);
        byte[] keyBytes = new byte[in.available()];
        in.read(keyBytes);
        in.close();
        // create a PKCS8EncodedKeySpec to load the key
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        // use KeyFactory to generate the private key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    // method to load my (server) AND CA certificates
    private Certificate loadCertificate(String filePath) throws IOException, CertificateException {
        System.out.println("        Loading a certificate...");
        // load and return the certificate using CertificateFactory
        FileInputStream in = new FileInputStream(filePath);
        CertificateFactory CertFact = CertificateFactory.getInstance("X.509");
        return CertFact.generateCertificate(in);
    }

    // <---------------------------------------------------------------------------------------------------------LISTEN>
    // method to listen, connect, and communicate
    public void listen() throws IOException {
        System.out.println("\nStart Server listening...");
        ServerSocket listener = SSLServerSocketFactory.getDefault().createServerSocket(SERVER_PORT);
        System.out.println("Server listening on port " + SERVER_PORT);
        try {
            // accept a tcp connection to begin communication
            Socket socket = listener.accept();
            System.out.println("Client connected");
            // get streams from the socket to use for communication
            Input = socket.getInputStream();
            Output = socket.getOutputStream();
            // perform handshake
            handshake();
            // send 2 test messages to the client after handshake
            sendMessage("Hello from the server");
            sendMessage("Server says hello!");
            // receive a confirmation from the client
            receiveMessage();
            // close connection after messages have been sent and received
            socket.close();
            System.out.println("\nConnection with client closed.");
        } catch (Exception e) {
            System.out.println("Error Listening: " + e.getMessage());
        } finally {
            listener.close();
            System.out.println("Server shutting down...");
        }
    }

    // <------------------------------------------------------------------------------------------------------HANDSHAKE>
    // method to perform handshake
    private void handshake() {
        try {
            System.out.println("\nHandshake...");
            // first, receive the nonce from the client. The method stores in member variable
            receiveNonce();
            // third, send server DHPub, server Certificate, and signed server DHPub to client
            sendServerCertificateAndKeys();
            // receive and verify the client's certificate and DH public key
            receiveAndVerifyClientCertificateAndKeys();
            // derive the shared secret using my DH private key and the received DH Pub key
            deriveSharedSecret();
            // derive the session keys from the shared secret using HKDF
            generateSessionKeys();
            // send HMAC handshake finish message and store it for client response check
            sendServerHMAC();
            // receive and verify the client response by computing expected result and comparing
            verifyClientHMAC(receiveClientHMAC());
            // now we have confirmed we are secure and sharing the same session keys
            System.out.println("Handshake Complete!\n");
        } catch (Exception e) {
            System.out.println("Error in Handshake: " + e.getMessage());
        }
    }

    // method to receive the nonce from the client
    void receiveNonce() throws IOException {
        System.out.println("    Receiving nonce...");
        // read in nonce
        byte[] nonce = new byte[32];
        Input.read(nonce);
        // put nonce into member variable for use with session keys
        this.nonce = Arrays.copyOf(nonce, nonce.length);
        // store data for HMAC
        hmacData.setNonce(nonce);
    }

    // method to generate DH key pair, returns the server's DH Pub key as a byte array
    private void generateDHKeyPair() {
        System.out.println("    Generating DH key pair...");
        // generate the server's public and private DH Keys
        serverDHPrivateKey = new BigInteger(2048, myRandom); // get a random private DH key
        // Calculate DH Public key w/ selected g, N, and the new DH private key (serverDHPub = g^serverDHPriv)
        serverDHPublicKey = DiffieHellmang.modPow(serverDHPrivateKey, DiffieHellmanN);
        // make sure the random key value is less than N
        while(serverDHPublicKey.compareTo(DiffieHellmanN) >= 0) {
            serverDHPrivateKey = new BigInteger(2048, myRandom);
            serverDHPublicKey = DiffieHellmang.modPow(serverDHPrivateKey, DiffieHellmanN);
        }
    }

    // method to send server certificate, server DH public key, and signed DH public key encrypted with RSA private key to a client
    private void sendServerCertificateAndKeys()
            throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
                System.out.println("    Sending Server Certificate And Keys...");
                byte[] signedKey = getSignedDHKey(serverDHPublicKey, serverPrivateKey);
                HandshakeMessage ServerHSMsg = new HandshakeMessage(serverCertificate, serverDHPublicKey, signedKey);
                hmacData.setServerHandshakeMsg(ServerHSMsg); // store data for HMAC
                ServerHSMsg.send(Output);
    }

    // helper method to sign/encrypt server DH public key using server Private RSA key
    private byte[] getSignedDHKey(BigInteger serverDHPublicKey, PrivateKey serverPrivateKey) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("        Getting signed key to send...");
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(serverPrivateKey);
        signature.update(serverDHPublicKey.toByteArray());
        return signature.sign();
    }

    // method to receive and verify the client certificate
    private void receiveAndVerifyClientCertificateAndKeys() throws IOException, CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException, ClassNotFoundException {
        System.out.println("    Receiving and verifying client Certificate and Keys...");
        // use ObjectInputStream to read the serialized HandshakeMessage object
        ObjectInputStream objectInputStream = new ObjectInputStream(Input);
        HandshakeMessage handshakeMessage = (HandshakeMessage) objectInputStream.readObject();
        // store data for HMAC
        hmacData.setClientHandshakeMsg(handshakeMessage);
        // verify the client certificate
        X509Certificate clientCertificate = (X509Certificate) handshakeMessage.certificate;
        clientCertificate.checkValidity();
        // verify the client certificate with the CA certificate
        clientCertificate.verify(CACertificate.getPublicKey());
        System.out.println("        Client certificate verified.");
        // get the client's Diffie-Hellman public key
        clientDHPublicKey = handshakeMessage.DHPublicKey;
    }

    // Method to derive the shared secret
    private void deriveSharedSecret() {
        System.out.println("        Deriving shared secret...");
        SharedSecret = clientDHPublicKey.modPow(serverDHPrivateKey, DiffieHellmanN);
    }

    // method to generate session keys using HKDF. This uses the shared secret and the
    // clients nonce, then generates encryption keys, MAC keys, and IVs
    private void generateSessionKeys() throws NoSuchAlgorithmException, InvalidKeyException {
                System.out.println("    Generating session keys\n           Shared secret: " + SharedSecret);
                // change sharedSecret into byte[] for use
                byte[] sharedSecretBytes = SharedSecret.toByteArray();
                // generate session keys from shared secret
                Mac hmac = Mac.getInstance("HmacSHA256");
                hmac.init(new SecretKeySpec(nonce, "HmacSHA256"));
                byte[] prk = hmac.doFinal(sharedSecretBytes);
                // second hkdfExpand to generate session keys
                byte[] serverEncrypt = hkdfExpand(prk, "server encrypt");
                byte[] clientEncrypt = hkdfExpand(serverEncrypt, "client encrypt");
                byte[] serverMAC = hkdfExpand(clientEncrypt, "server MAC");
                byte[] clientMAC = hkdfExpand(serverMAC, "client MAC");
                byte[] serverIV = hkdfExpand(clientMAC, "server IV");
                byte[] clientIV = hkdfExpand(serverIV, "client IV");
                // create the actual keys for use with AES and HMAC
                serverEncryptKey = new SecretKeySpec(serverEncrypt, "AES");
                clientEncryptKey = new SecretKeySpec(clientEncrypt, "AES");
                serverMACKey = new SecretKeySpec(serverMAC, "HmacSHA256");
                clientMACKey = new SecretKeySpec(clientMAC, "HmacSHA256");
                serverIVSpec = new IvParameterSpec(serverIV);
                clientIVSpec = new IvParameterSpec(clientIV);
                // print out keys for debugging
                System.out.println("            Session Keys Are: ");
                System.out.println("                " + serverEncryptKey.toString());
                System.out.println("                " + clientEncryptKey.toString());
                System.out.println("                " + serverMACKey.toString());
                System.out.println("                " + clientMACKey.toString());
                System.out.println("                " + serverIVSpec.getIV().toString());
                System.out.println("                " + clientIVSpec.getIV().toString());
    }

    // hkdf expanded helper method for generating session keys
    private byte[] hkdfExpand(byte[] prk, String tag) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(prk, "HmacSHA256"));
        byte[] tagBytes = tag.getBytes();
        byte[] expandedKey = hmac.doFinal(tagBytes);
        // AES keys only need 16 bytes
        return Arrays.copyOf(expandedKey, 16);
    }

    // <-----------------------------------------------------------------------------------------------------HMAC TOOLS>
    // method to compute HMAC of a message using a given key
    private byte[] hmacSHA256(byte[] data, SecretKey key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // HMAC SHA 256 to compute MAC
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        return mac.doFinal(data);
    }

    // method to send HMAC to client at end of handshake
    void sendServerHMAC() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("Creating server's HMAC...");
        // get HMAC data we have stored
        byte[] allHMACData = hmacData.getData();
        // HMAC the HMAC data
        byte[] HMACBytes = hmacSHA256(allHMACData, serverMACKey);
        // now store message data for next HMAC
        hmacData.setServerHMACInitiationMsg(HMACBytes);
        // Send the HMAC to the client
        Output.write(HMACBytes);
        Output.flush();
        System.out.println("Sent HMAC to client: " + bytesToHex(HMACBytes));
    }

    // method to receive HMAC response from client
    private byte[] receiveClientHMAC() throws IOException {
        // read the size of the incoming HMAC message (assume the HMAC is 32 bytes for HmacSHA256)
        byte[] hmacBytes = new byte[32];
        Input.read(hmacBytes);
        System.out.println("Received Client's HMAC: " + bytesToHex(hmacBytes));
        return hmacBytes;
    }

    // method to verify the HMAC received from the client
    public void verifyClientHMAC(byte[] clientHMAC) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("Verifying client's HMAC Response...");
        // get HMAC data we have stored
        byte[] allHMACData = hmacData.getData();
        // HMAC the HMAC data
        byte[] ExpectedHMAC = hmacSHA256(allHMACData, clientMACKey);
        // Check if the computed HMAC matches the received HMAC
        if (!Arrays.equals(ExpectedHMAC, clientHMAC)) {
            throw new IOException("Client's HMAC verification failed.");
        }
        System.out.println("Client's HMAC verified successfully.");
    }

    // helper for debugging to print HMAC byte[]
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // <-----------------------------------------------------------------------------------------------SEND AND RECEIVE>
    // method to send an encrypted  message using the server encryption key, and including the HMAC
    public void sendMessage(String message) {
        try {
            System.out.println("\nSending message...\n-->" + message);
            // put message into byte array for use
            byte[] messageBytes = message.getBytes();
            // encrypt the msg using AES in CBC mode, with our session key and IV
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverEncryptKey, serverIVSpec);
            byte[] encryptedMessage = cipher.doFinal(messageBytes);
            // Compute the HMAC
            byte[] hmac = hmacSHA256(encryptedMessage, serverMACKey);
            // send msg and hmac combined in a Message object
            Message msgToSend = new Message(encryptedMessage, hmac);
            msgToSend.send(Output);
        } catch (Exception e) {
            System.out.println("Error sending message: " + e);
            e.printStackTrace();
        }
    }

    // method to receive message from client
    public void receiveMessage() {
        try {
            System.out.println("\nReceiving message...");
            ObjectInputStream objInputStream = new ObjectInputStream(Input);
            // get Message
            Message Message = (Message) objInputStream.readObject();
            // receive msg bytes
            byte[] encryptedMessage = Message.EncryptedMessageBytes;
            // receive hmac bytes
            byte[] hmacReceived = Message.hmac;
            // verify HMAC
            byte[] hmacCalculated = hmacSHA256(encryptedMessage, clientMACKey);
            if (!Arrays.equals(hmacCalculated, hmacReceived)) {
                throw new IOException("Server received a message that was not encrypted");
            }
            // decrypt using the appropriate session encryption key and IV spec
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, clientEncryptKey, clientIVSpec);
            byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
            // debug check
            System.out.println("--> Decrypted message: " + new String(decryptedMessage));
        } catch (Exception e) {
            System.out.println("Error receiving message: " + e);
            e.printStackTrace();
        }
    }
}