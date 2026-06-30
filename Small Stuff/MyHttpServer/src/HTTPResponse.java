import java.io.*;
import java.net.Socket;


// class to handle HTTP response tasks for HTTP server
public class HTTPResponse {

    // method to get a print writer that is connected to a client socket
    public static PrintWriter getPrintWriter(Socket clientSocket){
        // initializes a PrintWriter to write to the socket's output stream (back toward client)
        PrintWriter writer = null; // handle exception here by logging error message
        try {
            writer = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Connection output stream failed");
        }
        // return the printwriter to use
        return writer;
    }

    // method to send a 200 "ok" response & page back to a client. Works for any html file
    public static void send200 (PrintWriter writer, Socket clientSocket, String filename){
        // send back correct header information for a 200
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html");
        writer.println();
        // flush the writer to send the data it has written
        writer.flush();
        // get data from filename html file with a FileInputStream
        InputStream htmlInput; // handle exception here. Return 404 if not found.
        try {
            htmlInput = new FileInputStream(filename);
            // Transfer the input from the html file to the socket's output stream
            try {
                htmlInput.transferTo(clientSocket.getOutputStream()); // handle exception here. Log error saying return channel can't be accessed
            } catch (IOException e) {
                System.err.println("Could not transfer to output stream");
            }
            // close the input stream
            try {
                htmlInput.close(); // handle exception here. Log error message.
            } catch (IOException e) {
                System.err.println("Could not close input stream");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            HTTPResponse.send404(writer, clientSocket);
        }
        // flush the output stream to send the data back toward client
        try {
            clientSocket.getOutputStream().flush(); // handle exception here, log error message
        } catch (IOException e) {
            System.err.println("Could not flush output stream");
        }
    }

    // method to send a 404 "not found" response and page back to a client. Works for 1 html file "404.html"
    public static void send404 (PrintWriter writer, Socket clientSocket){
        if (writer != null) {
            // return correct header information for 404
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/html");
            writer.println();
            writer.flush();
        }
        InputStream htmlInput; // handle exception here. Return 404 if not found
        try {
            htmlInput = new FileInputStream("404.html");
            // Transfer the input to the socket's output stream
            try {
                htmlInput.transferTo(clientSocket.getOutputStream()); // handle exception here. log error message saying return channel can't be accessed
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
            clientSocket.getOutputStream().flush();
        } catch (IOException e) {
            System.err.println("Could not flush output stream");
        }
    }

}
