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
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/*
 * The client class represents a client able to communicate via the TLSLite protocol described in about.txt.
 * The client uses a TCP connection and implements a TLS like protocol to ensure secure connection and messaging.
 */
public class Client {

    // <----------------------------------------------------------------------------------------CLIENT MEMBER VARIABLES>
    // SERVER INFORMATION
    private static final String SERVER_HOST = "127.0.0.1"; // Server host to contact
    private static final int SERVER_PORT = 5679; // Server port to contact

    // KEYS AND CERTIFICATES
    private PrivateKey clientPrivateKey; // My private key
    private BigInteger serverDHPublicKey; // The server's public DH key
    private Certificate clientCertificate; // My certificate to send to server for verification
    private Certificate CACertificate; // CA certificate for verifying client's certificate

    // DIFFIE-HELLMAN PIECES
    private BigInteger DiffieHellmanN; // N parameter for calculations
    private BigInteger DiffieHellmang; // g parameter for calculations
    private BigInteger clientDHPrivateKey; // My private DH key
    private BigInteger clientDHPublicKey;// My public DH key
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
    private byte[] nonce; // My nonce to send a server to initiate communication
    HMACData hmacData; // Object to store data for HMAC messages at end of handshake

    // <------------------------------------------------------------------------------------------------------MAIN CODE>
    public static void main(String[] args) {
        try{
            Client client = new Client(); // Get a server instance
            client.initialize(); // initialize the client
            client.handshake(); // perform handshake with server
            // receive 2 messages from server and respond with the content from them
            String serverMsg1 = client.receiveMessage(); // Get first message
            String serverMsg2 = client.receiveMessage(); // Get second message
            // Create a reply with the received messages for confirmation
            String replyMsg = "Hello from Client, here are the messages I got: " + serverMsg1 + " + " + serverMsg2;
            client.sendMessage(replyMsg); // send the reply
            System.out.print("\nEnd of Main code, client done running");
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }

    // <-------------------------------------------------------------------------------------------------INITIALIZATION>
    // method to initialize the client for communication over TCP
    public void initialize() throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("\nInitializing client...");
        this.hmacData = new HMACData(); // initialize an HMACData object to use
        myRandom = new SecureRandom(); // generate our random object for use
        calculateDHparams(); // calculates DH g and N
        generateDHKeyPair(); // generate client DH keys
        loadKeys(); // loads necessary keys
        // Set access to the Client's keystore for use later
        System.setProperty("javax.net.ssl.trustStore", "src/ClientKeyStore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "hello!");
        Socket socket = SSLSocketFactory.getDefault().createSocket(SERVER_HOST, SERVER_PORT);
        // get input and output streams for sending/receiving messages
        Input = socket.getInputStream();
        Output = socket.getOutputStream();
    }

    // method to calculate g and N for DH from RFC 3526 2048 bit
    private void calculateDHparams() {
        System.out.println("    Calculating DH params...");
        double pi = Math.PI;
        BigInteger power2048 = BigInteger.ONE.shiftLeft(2048); // 2^2048
        BigInteger power1984 = BigInteger.ONE.shiftLeft(1984); // 2^1984
        BigInteger power64 = BigInteger.ONE.shiftLeft(64); // 2^64
        BigInteger power1918 = BigInteger.ONE.shiftLeft(1918); // 2^1918
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
        // load my (client) private key
        this.clientPrivateKey = loadPrivateKey("src/clientPrivateKey.der");
        // load my (client) certificate
        this.clientCertificate = loadCertificate("src/CASignedClientCertificate.pem");
        // load CA certificate
        this.CACertificate = loadCertificate("src/CAcertificate.pem");
    }

    // method to load the client's private key
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

    // method to load my (client) AND CA certificates
    private Certificate loadCertificate(String filePath) throws IOException, CertificateException {
        System.out.println("        Loading a certificate...");
        // load and return the certificate using CertificateFactory
        FileInputStream in = new FileInputStream(filePath);
        CertificateFactory CertFact = CertificateFactory.getInstance("X.509");
        return CertFact.generateCertificate(in);
    }

    // <------------------------------------------------------------------------------------------------------HANDSHAKE>
    // method to perform handshake
    public void handshake() {
        try {
            System.out.println("\nHandshake...");
            // get nonce and send to server
            generateAndSendNonce();
            // send my DH public key, my certificate, and my signed dh public key
            sendClientCertificateAndKeys();
            // Receive and verify server certificate and server's DH public key
            receiveAndVerifyServerCertificateAndKeys();
            // Derive shared secret
            deriveSharedSecret();
            // Derive Session keys from shared secret using HKDF
            generateSessionKeys();
            // receive the server's HMAC handshake message & verify the received message by computing our own & comparing
            verifyServerHMAC(receiveServerHMAC());
            // send final response back to server including previous message
            sendClientHMAC();
            // now we have confirmed we are secure and sharing the same session keys
            System.out.println("Handshake Complete!\n");
        } catch (Exception e) {
            System.out.println("Error in Handshake: " + e.getMessage());
        }
    }

    // method to generate a nonce for each initiated communication
    private void generateAndSendNonce() throws IOException {
        System.out.println("    Making and sending nonce...");
        byte[] nonce = new byte[32];
        myRandom.nextBytes(nonce);
        // put nonce into member variable for use with session keys
        this.nonce = nonce;
        // store data for HMAC
        hmacData.setNonce(nonce);
        // send nonce to server
        Output.write(nonce);
    }

    // method to generate DH public and private key pair for client
    private void generateDHKeyPair() {
        System.out.println("    Generating DH Key pair...");
        // generate the client's public and private DH Keys
        clientDHPrivateKey = new BigInteger(2048, myRandom); // get a random private DH key
        // Calculate DH Public key w/ selected g, N, and the new DH private key (serverDHPub = g^serverDHPriv)
        clientDHPublicKey = DiffieHellmang.modPow(clientDHPrivateKey, DiffieHellmanN);
        // make sure the random key value is less than N
        while(clientDHPublicKey.compareTo(DiffieHellmanN) >= 0) {
            clientDHPrivateKey = new BigInteger(2048, myRandom);
            clientDHPublicKey = DiffieHellmang.modPow(clientDHPrivateKey, DiffieHellmanN);
        }
    }

    // method to send client certificate, client DH public key, and signed DH public key encrypted with RSA private key to a server
    private void sendClientCertificateAndKeys()
            throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        System.out.println("    Sending Client Certificate And Keys...");
        byte[] signedKey = getSignedDHKey(clientDHPublicKey, clientPrivateKey);
        HandshakeMessage ClientHSMsg = new HandshakeMessage(clientCertificate, clientDHPublicKey, signedKey);
        hmacData.setClientHandshakeMsg(ClientHSMsg); // store data for HMAC
        ClientHSMsg.send(Output);
    }

    // helper method to sign/encrypt client DH public key using client Private RSA key
    private byte[] getSignedDHKey(BigInteger clientDHPublicKey, PrivateKey clientPrivateKey) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("        Getting signed key to send...");
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(clientPrivateKey);
        signature.update(clientDHPublicKey.toByteArray());
        return signature.sign();
    }

    // method to receive and verify the server signed certificate
    private void receiveAndVerifyServerCertificateAndKeys() throws IOException, CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException, ClassNotFoundException {
        System.out.println("    Receiving and verifying server Certificate and Keys...");
        // use ObjectInputStream to read the serialized HandshakeMessage object
        ObjectInputStream objectInputStream = new ObjectInputStream(Input);
        HandshakeMessage handshakeMessage = (HandshakeMessage) objectInputStream.readObject();
        // store data for HMAC
        hmacData.setServerHandshakeMsg(handshakeMessage);
        // verify the server's certificate
        X509Certificate serverCertificate = (X509Certificate) handshakeMessage.certificate;
        serverCertificate.checkValidity();
        // verify the server's certificate with the CA certificate
        serverCertificate.verify(CACertificate.getPublicKey());
        System.out.println("        Server certificate verified.");
        // get the server's Diffie-Hellman public key
        serverDHPublicKey = handshakeMessage.DHPublicKey;
    }

    // Method to derive the shared secret
    private void deriveSharedSecret() {
        System.out.println("        Deriving shared secret...");
        SharedSecret = serverDHPublicKey.modPow(clientDHPrivateKey, DiffieHellmanN);
    }

    // method to generate session keys using HKDF. This uses the shared secret and the
    // my nonce, then generates encryption keys, MAC keys, and IVs
    private void generateSessionKeys() throws NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("    Generating session keys\n       Shared secret: " + SharedSecret);
        // change sharedSecret into byte[] for use
        byte[] sharedSecretBytes = SharedSecret.toByteArray();
        // generate session keys from shared secret
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(this.nonce, "HmacSHA256"));
        byte[] prk = hmac.doFinal(sharedSecretBytes);
        // use hkdfExpand to generate session keys
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
        System.out.println("        Session Keys Are: ");
        System.out.println("            " + serverEncryptKey.toString());
        System.out.println("            " + clientEncryptKey.toString());
        System.out.println("            " + serverMACKey.toString());
        System.out.println("            " + clientMACKey.toString());
        System.out.println("            " + serverIVSpec.getIV().toString());
        System.out.println("            " + serverIVSpec.getIV().toString());
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

    // method to receive the initial HMAC msg from the server
    private byte[] receiveServerHMAC() throws IOException {
        // read the size of the incoming HMAC message (assume the HMAC is 32 bytes for HmacSHA256)
        byte[] hmacBytes = new byte[32];
        Input.read(hmacBytes);
        System.out.println("Received Server's HMAC: " + bytesToHex(hmacBytes));
        return hmacBytes;
    }

    // method to verify the HMAC received from the server
    public void verifyServerHMAC(byte[] serverHMAC) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("Verifying server's HMAC Message...");
        // get HMAC data we have stored
        byte[] allHMACData = hmacData.getData();
        // HMAC the HMAC data
        byte[] ExpectedHMAC = hmacSHA256(allHMACData, serverMACKey);
        // now store data for next HMAC
        hmacData.setServerHMACInitiationMsg(serverHMAC);
        // Check if the computed HMAC matches the received HMAC
        if (!Arrays.equals(ExpectedHMAC, serverHMAC)) {
            throw new IOException("Server's HMAC verification failed.");
        }
        System.out.println("Server's HMAC verified successfully.");
    }

    // method to create our response message
    void sendClientHMAC() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("Creating client HMAC...");
        // get HMAC data we have stored
        byte[] allHMACData = hmacData.getData();
        // HMAC the HMAC data
        byte[] HMACBytes = hmacSHA256(allHMACData, clientMACKey);
        // Send the HMAC to the client
        Output.write(HMACBytes);
        Output.flush();
        System.out.println("Sent HMAC to client: " + bytesToHex(HMACBytes));
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
            cipher.init(Cipher.ENCRYPT_MODE, clientEncryptKey, clientIVSpec);
            byte[] encryptedMessage = cipher.doFinal(messageBytes);
            // Compute the HMAC
            byte[] hmac = hmacSHA256(encryptedMessage, clientMACKey);
            // send msg and hmac combined in a Message object
            Message msgToSend = new Message(encryptedMessage, hmac);
            msgToSend.send(Output);
        } catch (Exception e) {
            System.out.println("Error sending message: " + e);
            e.printStackTrace();
        }
    }

    // method to receive message from server
    public String receiveMessage() {
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
            byte[] hmacCalculated = hmacSHA256(encryptedMessage, serverMACKey);
            if (!Arrays.equals(hmacCalculated, hmacReceived)) {
                throw new IOException("Server received a message that was not encrypted");
            }
            // decrypt using the appropriate session encryption key and IV spec
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, serverEncryptKey, serverIVSpec);
            byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
            // debug check
            System.out.println("--> Decrypted message: " + new String(decryptedMessage));
            // return message
            return new String(decryptedMessage);
        } catch (Exception e) {
            System.out.println("Error receiving message: " + e);
            e.printStackTrace();
        }
        return null;
    }
}