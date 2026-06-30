/*
 * Author: Jason Ablott
 * Date First Version Completed: 03/28/2025
 * Date Most Recently Modified: 03/28/3035
 * Project: TLSLite
 * For: CS 6014 Networks and Security, University of Utah MSD Program
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/*
 * The Message class is used to package information for ease of  sending and receiving as objects after the handshake
 * has completed and messages can be securely sent/received using encryption.
 */
public class Message implements Serializable {

    // member variables (message components)
    public byte[] EncryptedMessageBytes;
    public byte[] hmac;

    // constructor to create a message with given fields
    public Message(byte[] EncMsgBytes, byte[] hmac) {
        this.EncryptedMessageBytes = EncMsgBytes;
        this.hmac = hmac;
    }

    // write method to send a message using an ObjectOutputStream
    public void send(OutputStream out) throws IOException {
        ObjectOutputStream objOutStream = new ObjectOutputStream(out);
        objOutStream.writeObject(this);
        objOutStream.flush();
    }
}