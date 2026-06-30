import { useState } from 'react';

// component for chat page
export function ChatCode({username, roomname, messages, addMessage, client, loginFunction, clearMsg}) {

// state to track message
const [message, setMessage] = useState("");
//const [chat, setChat]= useState("");

//messages.map((message, index) => { chat += message +"\n";});
// method to update message
const updateMessage = (event) => {
  setMessage(event.target.value);
}

// method to handle user click on leave button
const  handleLeaveClick = () =>{
  // send message to server
  client.send("leave " + username + " " + roomname);
  // clear messages in app
  clearMsg();
  // switch login to false to return to login page
  loginFunction(false);
} 

// method to handle user click on send button
const  handleSendClick = () =>{
  // send message to server
  client.send("message " + username + " " + message);
} 

// code to return the chat component
return (
  <div className="chat">
     
    <form className="form-container">


  
        <h2 id="roomTitle">{roomname}</h2>

        <label id="userTitle"><b>{username}</b></label>

        <textarea id="msg" onChange={updateMessage} placeholder="Type message.." name="msg" required></textarea>

        <button type="button" onClick={handleSendClick} className="btn" id="sendButton">Send Message</button>

        <textarea id="chat" placeholder="message history" name="chat"></textarea>

        <button type="button" onClick={handleLeaveClick} className="btn leave" id="leaveButton">Leave Room</button>

    </form>
  </div>
);
}