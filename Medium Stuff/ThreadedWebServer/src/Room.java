import java.net.Socket;
import java.util.ArrayList;

/*
    The Room class manages the chat rooms. It stores a list of rooms that exist, and allows the creation of rooms.
    A room object stores a list of clients (Sockets) in the room as a way to send messages to all clients in a room.
    Room objects also have methods to add, remove, and find clients in the room.

    -Room class enables a room to store message history in an ArrayList to enable sending of message history to new
     users when they join a room
 */

public class Room {


    // Member variables


    // room class keeps track of rooms, not a specific room object
    private static ArrayList<Room> rooms = new ArrayList<>();

    // name of room
    private String name;
    // each room needs a list of clients who are in the room
    private ArrayList<Socket> clients;
    // each room keeps a list of messages that have been sent to be able to send message history
    private ArrayList<String> messages;


    // methods


    // constructor to make a room with a name. Only to be used in getRoom
    private Room(String name){
        this.name = name;
        this.clients = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    // static factory method to get a room. If a room already exists, the room is returned.
    // If the room does not exist, the private constructor is used to create a new room with the
    // given name and then that new room is returned
    public static Room getRoom(String name) {
        // make a room to return and set to null initially
        Room returnRoom = null;
        // if there are rooms in the list, look through to see if the room exists and if it does, return it.
        for (Room room : rooms) {
            if (room.getName().equals(name)) {
                returnRoom = room;
                break;
            }
        }
        // otherwise the room does not exist yet, so we make the new room, add it, and return it.
        if( returnRoom == null ) {
            returnRoom = new Room(name);
            rooms.add(returnRoom);
        }
        return returnRoom;
    }

    // method to return the room name
    public String getName(){
        return name;
    }

    // method to return list of clients in room. Returns arrayList of Sockets
    public ArrayList<Socket> getClients(){
        return clients;
    }

    // method to add client to clients list
    public synchronized void addClient(Socket client){
        clients.add(client);
    }

    // method to remove client from clients list
    public synchronized void removeClient(Socket client){
        clients.remove(client);
    }

    // method to return messages
    public ArrayList<String> getMessages(){
        return messages;
    }

    public void addMessage(String message){
        messages.add(message);
    }

    // method to check if a certain client is in this room. Returns true if in room, false if not.
    public synchronized boolean hasClient(Socket client){
        return clients.contains(client);
    }

    // static method to get the list of rooms.
    public static ArrayList<Room> getRooms() {
        return rooms;
    }
}
