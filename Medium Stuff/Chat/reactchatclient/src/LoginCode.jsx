import { InputWidget} from "./InputWidget";

// component for login page
export function LoginCode({loginFunction, setUser, setRoom, client, username, roomname}) {

// helper function to validate user inputs in username and roomname
function isValid(string){
    if (string.length === 0){
        return false;
    }
    for (let i = 0; i < string.length; i++){
        if (string.charCodeAt(i) < 97 || string.charCodeAt(i) > 122){
            return false;
        }
    }
    return true;
}

// method to handle user click on join button
const  handleJoinClick = () => {
    // validate user input
    if (isValid(username) && isValid(roomname)){
        loginFunction(true);
    } else {
        alert("invalid user input. Username and Room name require an input that can contain only contain lower case characters");
    }
    // send join message to server
    client.send("join " + username + " " + roomname);
} 

// code to return login component
return (
    <div>
        <p>Please enter your username and the room to join</p>

        <InputWidget    
            id="userNameInput" 
            name="username" 
            placeholder="enter username" 
            username={username}
            roomname={roomname}
            setData={setUser}>
        </InputWidget>

        <InputWidget  
            id="roomNameInput" 
            name="room" 
            placeholder="room name" 
            username={username}
            roomname={roomname}
            setData={setRoom}>
        </InputWidget>

        <button onClick={handleJoinClick}>Join Room</button>
    </div>
);
}