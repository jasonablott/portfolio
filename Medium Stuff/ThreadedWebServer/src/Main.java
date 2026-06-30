import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
    main class starts a serverSocket to listen for client requests on port 8080. We continue listening on the port
    and when a request comes through that the serverSocket can accept, we open a new Socket to use for communication
    with a potential client. After a connection is established the main function creates a new thread and passes the
    client Socket to the thread to continue communication and allow the main to keep accepting client connections

    -Have added handling of message history for a small amount of messages to be stored short term in each room
     object while the server is running
*/
public class Main {
    public static void main(String[] args) throws IOException {

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
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();

            System.out.println("Socket " + clientSocket.getPort() + " obtained in main");
            System.out.println("starting thread now from main");

            clientHandler clientRun = new clientHandler(clientSocket);
            Thread clientThread = new Thread(clientRun);
            clientThread.start();
        }
    }
}