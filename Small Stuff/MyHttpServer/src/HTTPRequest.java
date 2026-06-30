import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

// Class to help accept HTTP requests in HTTP server
public class HTTPRequest {

    // method to get client socket from a server socket
    public static Socket getClientSocket (ServerSocket serverSocket){
        // represents door between server and client
        Socket clientSocket = null; // handle exception here by printing error log
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed");
        }
        return clientSocket;
    }

    // method to get initial part of request from client to determine its validity
    public static String [] getRequest (Socket clientSocket){
        // initialize stream to get data
        InputStream input; // handle exception here by printing error log
        try {
            input = clientSocket.getInputStream();
            // scanner can handle data coming from the client via the socket
            Scanner scanner = new Scanner(input);
            // get the first line of the request, the info we need is there
            String request = scanner.nextLine();
            // split the first line so we can look at each item individually
            return request.split(" ");
        } catch (IOException e) {
            System.err.println("Input stream failed");
        }
        // return an invalid request so 404 is returned
       return new String[]{"",""};
    }


}

