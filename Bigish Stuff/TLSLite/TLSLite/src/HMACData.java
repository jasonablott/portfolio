/*
 * Author: Jason Ablott
 * Date First Version Completed: 03/28/2025
 * Date Most Recently Modified: 03/28/3035
 * Project: TLSLite
 * For: CS 6014 Networks and Security, University of Utah MSD Program
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*
 * The HMACData class is used to store handshake message data for the HMAC messages at the end of the handshake
 * process.
 */
public class HMACData {

    // member variables
    private byte[] nonce; // nonce
    private HandshakeMessage serverHandshakeMsg; // server certificate and keys in msg form
    private HandshakeMessage clientHandshakeMsg; // client certificate and keys in msg form
    private byte[] ServerHMACInitiationMsg; // HMAC message from server in byte[] form

    // setters
    public void setNonce(byte[] nonce){ this.nonce = nonce; }
    public void setServerHandshakeMsg(HandshakeMessage serverHandshakeMsg){
        this.serverHandshakeMsg = serverHandshakeMsg;
    }
    public void setClientHandshakeMsg(HandshakeMessage clientHandshakeMsg){
        this.clientHandshakeMsg = clientHandshakeMsg;
    }
    public void setServerHMACInitiationMsg(byte[] serverHMACInitiationMsg){
        this.ServerHMACInitiationMsg = serverHMACInitiationMsg;
    }

    // method to get all data in one byte[] for hmac'ing
    public byte[] getData() throws IOException {
            // create a ByteArrayOutputStream to hold all the data
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // write the nonce to the stream
            if (nonce != null) { byteArrayOutputStream.write(nonce); }
            // write the server handshake message to the stream
            if (serverHandshakeMsg != null) { byteArrayOutputStream.write(serverHandshakeMsg.getBytes()); }
            // write the client handshake message to the stream
            if (clientHandshakeMsg != null) { byteArrayOutputStream.write(clientHandshakeMsg.getBytes()); }
            // write the ServerHMACInitiationMsg to the stream
            if (ServerHMACInitiationMsg != null) { byteArrayOutputStream.write(ServerHMACInitiationMsg); }
            // return the complete concatenated byte array
            return byteArrayOutputStream.toByteArray();
        }
}