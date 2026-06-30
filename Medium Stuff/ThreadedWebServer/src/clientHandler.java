import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/*
    Client handler parses http requests and responds accordingly. It handles normal http requests
    as well as websocket upgrade requests. If a websocket upgrade request comes in, it responds to upgrade the
    connection and starts listening for websocket messages. Websocket messages for the chat application are processed,
    used to perform room operations, and to send responses back to clients.

    -Have added handling of message history for a small amount of messages to be stored short term in each room
     object while the server is running
 */

public class clientHandler implements Runnable {

    // variable to store the client socket for this thread
    Socket clientSocket;
    // variable to store username for this client
    String clientsUserName;
    // variable to exit websocket loop when client disconnects
    boolean isWebsocketLive = true;

    // constructor to set this clientSocket
    public clientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    // Run method to do when thread starts
@Override
    public void run() {

        // log that thread is starting
        System.out.println("Thread " + Thread.currentThread().threadId() + " starting in runnable");

        try {
            // get the request when a socket is opened via the serverSocket
            HashMap<String, String> requestParams;
            // initialize stream to get data
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            requestParams = HTTPRequest.getRequestMap(input);

            System.out.println(requestParams);

            // get the method from the request. We'll use it below.
            String method = requestParams.get("method");
            // get the path from the request. We'll use it below
            String path = requestParams.get("path");
            // get the websocket upgrade info from the request (if it is there)
            String upgradeRequest = requestParams.get("Upgrade");
            String upgradeKey = requestParams.get("Sec-WebSocket-Key");

            // initialize a PrintWriter to write to the socket's output stream (back toward client)
            PrintWriter writer = HTTPResponse.getPrintWriter(output);

            // check for valid request, if ok send back what is requested. Handled / and /home as valid inputs, as well
            //  as a websocket request, all else invalid

            // handle websocket upgrade request
            if (upgradeRequest != null && upgradeKey != null && method.equals("GET") && path.equals("/")) {
                System.out.println("WebSocket Request: " + upgradeKey);
                // do this if the request is for a websocket upgrade
                HTTPResponse.webSocketUpgrade(writer, upgradeKey);
                System.out.println("Thread " + Thread.currentThread().threadId() + " upgraded connection to websocket");

                // now need loop to keep webSocket open indefinitely
                while (isWebsocketLive) {
                        // get incoming websocket messages
                    try {
                        String msg = readWebSocketmsg(input);
                        // interpret messages, handle rooms, and send response to client
                        msgHandler(msg);
                    } catch(Exception e) {
                        Room roomToRemove = null;
                        for (Room room : Room.getRooms()){
                            if (room.hasClient(clientSocket)){
                                roomToRemove = room;
                            }
                        }
                        if (roomToRemove != null) {
                            roomToRemove.removeClient(clientSocket);
                            isWebsocketLive = false;
                        } else {
                            System.out.println("no rooms left");
                            isWebsocketLive = false;
                        }

                    }

                }

            // handle normal HTTP GET
            } else if (method.equals("GET") && (path.equals("/") || path.equals("/home"))){
                HTTPResponse.send200(writer, output, "index2.html");
                System.out.println("Thread " + Thread.currentThread().threadId() + " sent 200");

            // Handle other cases
            } else if (method.equals("GET") && (path.equals("/WebChatJS.js"))){
                HTTPResponse.send200js(writer, output, "WebChatJS.js");
                System.out.println("Thread " + Thread.currentThread().threadId() + " sent 200 JavaScript");

            // Handle css request
            } else if (method.equals("GET") && (path.equals("/chatStyles.css"))){
                HTTPResponse.send200(writer, output, "chatStyles.css");
                System.out.println("Thread " + Thread.currentThread().threadId() + " sent 200 CSS");

            // Handle other cases
            } else {
                // if request was not handleable return 404 error and message to client
                HTTPResponse.send404(writer, output);
                System.out.println("Thread " + Thread.currentThread().threadId() + " sent 404");
            }
            // close input stream as request has been handled
            input.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // close connection after the response has been sent (this is just for http requests/responses)
        try {
            clientSocket.close();
            System.out.println("Socket " + clientSocket.getLocalPort() + " closed in runnable");
        } catch (IOException e) {   // handle exception here by logging error message
            System.err.println("Could not close client socket");
        }

        // log that thread is ending
        System.out.println("Thread " + Thread.currentThread().threadId() + " exiting in runnable");
    }

    // method to read a websocket message by parsing header and decoding payload
    private String readWebSocketmsg(InputStream in) throws IOException {
        // read 2 bytes from clientSocket via a datastream so we can read shorts and longs
        DataInputStream input = new DataInputStream(in);
        // get byte array and store header in it
        byte[] header = input.readNBytes(2);

        // check if the message is masked
        boolean masked = (header[1 & 0x80]) != 0;

        // check opcode and if it is close msg flip isWebSocket live to exit websocket loop
        int opcode = header[0] & 0x0F;
        if (opcode == 0x8){
            Room roomToRemove = null;
            for (Room room : Room.getRooms()){
                if (room.hasClient(clientSocket)){
                    roomToRemove = room;
                }
            }
            roomToRemove.removeClient(clientSocket);
            isWebsocketLive = false;
        }

        // check first length byte, if it is 126 or 127 we need to keep looking for the larger length
        long len = header[1] & 0x7F;
        if (len == 126){
            len = input.readUnsignedShort();
        }
        else if (len == 127){
            len = input.readLong();
        }

        // handle unmasking if the message is masked
        if (masked){
            // read in the mask and message
            byte[] mask = input.readNBytes(4);
            byte[] message = input.readNBytes((int)len);

            // unmask msg
            for (int i = 0; i < len; i++){
                message[i] ^= mask[i%4];
            }

            // cast bytes of message to string for use
            String msg = new String(message);
            System.out.println("Thread " + Thread.currentThread().threadId() + " received websocket message: " + msg);

            // return the message which is now decoded and in string form
            return msg;

        // handle reading message if not masked
        } else {
            byte[] message = input.readNBytes((int)len);
            String msg = new String(message);
            System.out.println("Thread " + Thread.currentThread().threadId() + " received websocket message: " + msg);

            // return the message which is now decoded and in string form
            return msg;
        }

    }

    // method to send the return message back by formatting byte header and sending message
    private void sendWebSocketmsg(String msg, OutputStream output) throws IOException {

        // send header

        // get data output stream to send the websocket response
        DataOutputStream out = new DataOutputStream(output);
        // make byte array to put header information into
        byte[] header = new byte[2];
        // get the payload length from the message we are going to send
        int len = msg.length();
        System.out.println("Length of message server is sending back: " + len);
        // populate the first byte with 1000 0001 to represent fin: last message and opcode: text
        header[0] = (byte)0x81;
        // populate the payload length in byte 2 for small messages
        if (len < 126){
            System.out.println("About to write length: " + len + " in ws header");
            header[1] = (byte) len;
        } else if (len == 126){
            header[1]= (byte) 126;
            out.writeShort(len);
        } else if (len == 127){
            header[1]= (byte) 127;
            out.writeLong(len);
        }
        System.out.println("About to write header: " + Arrays.toString(header));
        out.write(header);

        // send message

        System.out.println("About to write bytes for message: " + msg);
        out.write(msg.getBytes());
        System.out.println("Done writing message");
        out.flush();
        System.out.println("message flushed");
    }

    // method to parse a decoded message for chat server and utilize the message parts as necessary
    // for chat functionality. Handles rooms and sends response
    private void msgHandler(String msg) throws IOException {
        // get scanner to read message
        Scanner msgScanner = new Scanner(msg);
        // get first item from message to determine what type of message it is, join, leave, or message
        String msgType = msgScanner.next();

        // handle join message
        if (msgType.equals("join")){
            // parse the data needed to create response and do room stuff
            String username = msgScanner.next();
            this.clientsUserName = username;
            System.out.println("username read from message: " + username);
            String roomname = msgScanner.next();
            System.out.println("roomname read from message: " + roomname);

            // add the client to the room on server
            Room room = Room.getRoom(roomname);
            room.addClient(this.clientSocket);

            // format return msg
            String jsonifiedMsg = "{\n \"type\" : \"join\",\n \"room\" : \"" + roomname + "\",\n \"user\" : \"" + username + "\"\n }";
            System.out.println("msgHandler is returning message: " + jsonifiedMsg);
            // broadcast formatted message to room
            broadcastMsg(jsonifiedMsg, roomname);
            // send old messages from room
            sendMsgHistory(room);
            // add message to message history
            Room.getRoom(roomname).addMessage(jsonifiedMsg);
        }

        // handle leave message
        else if (msgType.equals("leave")){
            // remove the user from the room they are in on the server side
            String roomname = null;
            Room roomToRemove = null;
            for (Room room : Room.getRooms()){
                if (room.hasClient(clientSocket)){
                    roomname = room.getName();
                    roomToRemove = room;
                }
            }
            // send a leave message to the client
            String username = this.clientsUserName;

            // format return msg
            String jsonifiedMsg = "{\n \"type\" : \"leave\",\n \"room\" : \"" + roomname + "\",\n \"user\" : \"" + username + "\"\n }";
            System.out.println("msgHandler is returning message: " + jsonifiedMsg);
            // add message to message history
            Room.getRoom(roomname).addMessage(jsonifiedMsg);
            // broadcast formatted message to room
            broadcastMsg(jsonifiedMsg, roomname);
            // now remove client from room
            roomToRemove.removeClient(clientSocket);

        }

        // handle message message
        else if (msgType.equals("message")){
            // the rest of the msg is the message here
            // format return message first then we can send it back to all clients in the room
            String message = msgScanner.nextLine();
            String roomname = null;
            for (Room room : Room.getRooms()) {
                if (room.hasClient(clientSocket)) {
                    roomname = room.getName();
                }
            }

            String username = this.clientsUserName;
            // send message
            System.out.println("msgHandler is returning message: " + message + " to room: " + roomname);
            // format return msg
            String jsonifiedMsg = "{\n \"type\" : \"message\",\n \"user\" : \"" + username + "\",\n \"room\" : \"" + roomname + "\",\n \"message\" : \"" + message + "\"\n }";
            System.out.println("msgHandler is returning message: " + jsonifiedMsg);
            // add message to message history
            Room.getRoom(roomname).addMessage(jsonifiedMsg);
            // broadcast formatted message to room
            broadcastMsg(jsonifiedMsg, roomname);
        }
    }

    // method to send a formatted message to all clients in a room roomname
    private void broadcastMsg(String jsonMsg, String roomname) throws IOException {
        // get room
        Room roomToSendTo = Room.getRoom(roomname);
        // get client list from room
        ArrayList<Socket> clientsToSendTo = roomToSendTo.getClients();
        System.out.println("clients in room to get the message: " + clientsToSendTo);
        // send message to clients in room
        for (Socket clientSocket : clientsToSendTo) {
            sendWebSocketmsg(jsonMsg, clientSocket.getOutputStream());
        }
    }

    // method to send message history for a room when new user joins
    private void sendMsgHistory(Room room) throws IOException {
        // get message history from room
        ArrayList<String> msgHistory = room.getMessages();
        // send each message to this client since they are the one that joined
        for (String msg : msgHistory){
            sendWebSocketmsg(msg, clientSocket.getOutputStream());
        }
    }
}