import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


// class to handle HTTP response tasks for HTTP server
public class HTTPResponse {

    // method to get a print writer that is connected to a client socket
    public static PrintWriter getPrintWriter(OutputStream output) {
        // initializes a PrintWriter to write to the socket's output stream (back toward client)
        PrintWriter writer;
        writer = new PrintWriter(output);
        // return the PrintWriter to use
        return writer;
    }

    // method to send a 200 "ok" response & page back to a client. Works for any html file
    public static void send200 (PrintWriter writer, OutputStream output, String filename){
        if (writer!= null) {
            // send back correct header information for a 200
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html");
            writer.println();
            // flush the writer to send the data it has written
            writer.flush();
        }
        sendFile(output, filename);
    }

    // method to send a 200 "ok" response & page back to a client. Works for any html file
    public static void send200js (PrintWriter writer, OutputStream output, String filename){
        if (writer!= null) {
            // send back correct header information for a 200
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: application/javascript");
            writer.println();
            writer.flush();
        }
            sendFile(output, filename);
    }

    // method to send a 404 "not found" response and page back to a client. Works for 1 html file "404.html"
    public static void send404 (PrintWriter writer, OutputStream output){
        if (writer != null) {
            // return correct header information for 404
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/html");
            writer.println();
            writer.flush();
        }
        sendFile(output, "404.html");
    }


    // helper to get response key using client upgrade key. "Handshake"
    public static String getUpgradeResponse (String upgradeKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String magicCode = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte [] sha1 = MessageDigest.getInstance("SHA-1").digest((upgradeKey + magicCode).getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(sha1);
    }


    // method to upgrade client communication to websocket connection
    public static void webSocketUpgrade(PrintWriter writer, String upgradeKey){
        if (writer != null) {
            try {
                // get modified key to send back
                String upgradeRequestAcceptKey = getUpgradeResponse(upgradeKey);
                // write response
                writer.println("HTTP/1.1 101 Switching Protocols");
                writer.println("Upgrade: websocket");
                writer.println("Connection: Upgrade");
                writer.println("Sec-WebSocket-Accept: " + upgradeRequestAcceptKey);
                writer.print("\r\n");

                System.out.println("Response Key: " + upgradeRequestAcceptKey);

            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                System.out.print(e.getMessage());
            }
            // flush the output stream to send the data back toward client. Log error if there is one
            writer.flush();
        }
    }

    // method to transfer byte stream from a file to socket output stream
    public static void sendFile(OutputStream output, String filename){
        InputStream htmlInput; // handle exception here. Return 404 if not found
        try {
            htmlInput = new FileInputStream(filename);
            // Transfer the input to the socket's output stream
            try {
                htmlInput.transferTo(output); // handle exception here. log error message saying return channel can't be accessed
            } catch (IOException e) {
                System.err.println("Could not transfer to output stream");
            }
            // close the input stream
            try {
                htmlInput.close(); // handle exception here, log error message
            } catch (IOException e) {
                System.err.println("Could not close input stream");
            }
        } catch (FileNotFoundException e) {
            System.err.println("404.html not found");
        }
        // flush the output stream to send the data back toward client. Log error if there is one
        try {
            output.flush();
        } catch (IOException e) {
            System.err.println("Could not flush output stream");
        }
    }

}
