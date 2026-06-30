import { LoginCode} from './LoginCode';
import { ChatCode } from './ChatCode';
import React, { useRef, useState, useEffect } from 'react';
import './App.css';
 
// main component
function App() {
  
  // state to track is user is "logged in" to display login or chat page
  const [isLoggedIn, editIsLoggedIn] = useState(false);
  // state to track the username
  const [username, editUsername] = useState("");
  // state to track the roomname
  const [roomname, editRoomname] = useState("");
  // store websocket
  const ws = useRef(null);
  // store messages
  const [messages, addMessage] = useState("");

  // method to update messages by adding 
  const updateMessages = (newMessage) => {
    let newMessages = messages + newMessage
    addMessage(newMessages);
  };

  // method to clear messages
  const clearMessages = () => {
    addMessage("");
  };

  // method to edit username
  const addUsername = (username) => {
    editUsername(username);
  };

  // method to edit roomname
  const addRoomname = (roomname) => {
    editRoomname(roomname);
  };

  // method to change login status
  const setLogin = (value )=> {
    editIsLoggedIn(value);
  }

  // useEffect allows websocket connection and handling to work appropriately
  useEffect(() => {

    // create websocket
    const websocket = new WebSocket("ws://localhost:8080");

    // check that connection is established
    websocket.onopen = () => {
      console.log("Connection Established");
    }

    // handle messages coming through websocket
    websocket.onmessage = (event) => {

      // log return message to check that it looks right and server is responding
      console.log("message returned from server: " + event.data);
      // parse the response for necessary data
      let msgObj = JSON.parse(event.data);

      // handle join message
      if (msgObj.type === "join") {
          // get data from server message
          let myUsername = msgObj.user;
          let myRoom = msgObj.room;
          // get element from display 
          let myMessages = document.getElementById("chat");
          // append new message 
          myMessages.value += myUsername + " joined " + myRoom + ".\n";

          // couldn't get this method to work due to display being text area and incompatible with message storage data type
          //updateMessages(myUsername + " joined " + myRoom + ".\n");

      // handle leave message
      } else if (msgObj.type === "leave"){
        // get data from server message
        let myUsername = msgObj.user;
        let myRoom = msgObj.room;
        // get element from display and update if not logged out
        if (isLoggedIn === true){
        let myMessages = document.getElementById("chat");
        myMessages.value += myUsername + " left " + myRoom +".\n";

        // couldn't get this method to work due to display being text area and incompatible with message storage data type
        //updateMessages(myUsername + " left " + myRoom +".\n");
        }

      // handle message messages
      } else if (msgObj.type === "message"){
        // get message content from server message
        let message = msgObj.message;
        // get element from display to update
        let myMessages = document.getElementById("chat");
        // append the new message
        myMessages.value += message + "\n";

        // couldn't get this method to work due to display being text area and incompatible with message storage data type
        //updateMessages(message + "\n");
    }
  }

  // error listener logs error to console
  websocket.onerror = (error) => {
    console.log(error.data);
  }

  // close listener logs to console to notify client
  websocket.onclose = () => {
    console.log("Connection Closed");
  }

  // set useRef ws to this websocket
  ws.current = websocket;

  // cleanup
  return () => {
    if (ws.current) {
      ws.current.close();
    }
  };
// end of useEffect and empty dependency to allow only one run and prevent re-setting websocket
},[]);


// return main component which is a conditional rendering of login or chat page based on isLoggedIn
  return (
      <div>
          { (!isLoggedIn) 
          ?   
          <LoginCode 
          loginFunction={setLogin} 
          setUser={addUsername}
          setRoom={addRoomname}
          client={ws.current}
          username={username}
          roomname={roomname}
          /> 
          :
          <ChatCode
          username={username}
          messages={messages}
          client={ws.current}
          addMessage={updateMessages}
          clearMsg={clearMessages}
          loginFunction={setLogin}
          roomname = {roomname}
          />
      }
      </div>
  );
}
export default App;
