import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    public static void main(String[] args) {

        // set up a server socket to listen to requests from clients
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {   // handle exception here, if this step fails the server can't run.
            // log error
            System.err.println("Could not listen on port: 8080");
            // end program, we can't proceed if this fails. 
            System.exit(-1);
        }

        // while(true) loop ensures server keeps running
        while (true){
            // Get a client socket. This represents a "door" between server and client
            Socket clientSocket = HTTPRequest.getClientSocket(serverSocket);
            // Get the clients request
            String [] requestPart = HTTPRequest.getRequest(clientSocket);
            // check first item in first line. We'll use it below.
            String method = requestPart[0];
            // check second item in first line. We'll use it below
            String path = requestPart[1];
            // initialize a PrintWriter to write to the socket's output stream (back toward client)
            PrintWriter writer = HTTPResponse.getPrintWriter(clientSocket);
            // check for valid request, if ok send back what is requested. Handled / and /home as valid inputs, all
            // else invalid
            if (method.equals("GET") && (path.equals("/") || path.equals("/home")) && writer != null){
                HTTPResponse.send200(writer, clientSocket, "index.html");
            } else {
                // if request was not "GET" return 404 error and message to client
                HTTPResponse.send404(writer, clientSocket);
            }
            // close connection after the response has been sent
            try {
                clientSocket.close();
            } catch (IOException e) {   // handle exception here by logging error message
                System.err.println("Could not close client socket");
            }
        }
    }
}