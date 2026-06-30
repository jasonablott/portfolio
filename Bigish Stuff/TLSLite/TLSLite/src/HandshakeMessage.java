/*
 * Author: Jason Ablott
 * Date First Version Completed: 03/28/2025
 * Date Most Recently Modified: 03/28/3035
 * Project: TLSLite
 * For: CS 6014 Networks and Security, University of Utah MSD Program
 */

import java.io.*;
import java.math.BigInteger;
import java.security.cert.Certificate;

/*
 * The HandshakeMessage class is used to package information for ease of  sending and receiving as objects during
 * the handshake process.
 */
public class HandshakeMessage implements Serializable {

    // member variables (message components)
    public Certificate certificate;
    public BigInteger DHPublicKey;
    public byte[] signedDHPublicKey;

    // constructor to create a message with given fields
    public HandshakeMessage(Certificate certificate, BigInteger DHPublicKey, byte[] signedDHPublicKey) {
        this.certificate = certificate;
        this.DHPublicKey = DHPublicKey;
        this.signedDHPublicKey = signedDHPublicKey;
    }

    // method to send a handshake message using an ObjectOutputStream
    void send(OutputStream out) throws IOException {
        ObjectOutputStream objOutStream = new ObjectOutputStream(out);
        objOutStream.writeObject(this);
        objOutStream.flush();
    }

    // method to convert the HandshakeMessage object into a byte array
    public byte[] getBytes() throws IOException {
        // serialize the object into a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // write the current HandshakeMessage object to the stream
        objectOutputStream.writeObject(this);
        objectOutputStream.flush();
        // return the byte array containing the serialized object
        return byteArrayOutputStream.toByteArray();
    }
}