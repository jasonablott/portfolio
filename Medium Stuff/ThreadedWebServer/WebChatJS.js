

// html strings to update page version


// html code for the login page
let loginHTML = "<!DOCTYPE html>\n" +
    "<html lang=\"en\">\n" +
    "<head>\n" +
    "    <meta charset=\"UTF-8\">\n" +
    "    <title>Login</title>\n" +
    "</head>\n" +
    "<body>\n" +
    "\n" +
    "<p>Please enter your username and the room to join</p>\n" +
    "<label> username: <input id=\"username\"/></label>\n" +
    "<label> room: <input id=\"roomname\"/></label>\n" +
    "<button id=\"joinButton\"> Join </button>\n" +
    "\n" +
    "</body>\n" +
    "</html>"

// html code for the chat page
let chatHTML = "<!-- some code from https://www.w3schools.com/howto/howto_js_popup_chat.asp -->\n" +
    "\n" +
    "<!DOCTYPE html>\n" +
    "<html lang=\"en\">\n" +
    "<head>\n" +
    "    <meta charset=\"UTF-8\">\n" +
    "    <link rel=\"stylesheet\" href=\"chatStyles.css\">\n" +
    "    <title>Chat</title>\n" +
    "</head>\n" +
    "<body>\n" +
    "<div class=\"chat\">\n" +
    "    <form class=\"form-container\">\n" +
    "\n" +
    "        <h2 id=\"roomTitle\">Chat</h2>\n" +
    "\n" +
    "        <!--<label id=\"messageTitle\"><b>Message</b></label>-->\n" +
    "\n" +
    "        <label id=\"userTitle\"><b>Message</b></label>\n" +
    "\n" +
    "        <textarea id=\"msg\" placeholder=\"Type message..\" name=\"msg\" required></textarea>\n" +
    "\n" +
    "        <button type=\"button\" class=\"btn\" id=\"sendButton\">Send Message</button>\n" +
    "\n" +
    "        <textarea id=\"chat\" placeholder=\"message history\" name=\"chat\"></textarea>\n" +
    "\n" +
    "        <button type=\"button\" class=\"btn leave\" id=\"leaveButton\">Leave Room</button>\n" +
    "\n" +
    "    </form>\n" +
    "</div>\n" +
    "</body>\n" +
    "</html>"


// get page elements for use


let pageReference = "index;"
// get the main body of the page and display the login screen
let htmlToUpdate = document.getElementById("mainHTML");
htmlToUpdate.innerHTML = loginHTML;
pageReference = "loginHTML";

let joinButton = document.getElementById("joinButton");
//let messageButton = document.getElementById("sendButton");
//let leaveButton = document.getElementById("leaveButton");

// get username from input
let username = (document.getElementById("username"));
// get roomname from input
let roomname = (document.getElementById("roomname"));
// get message
let message ;

// once loaded then run websocket code
htmlToUpdate.addEventListener("loadeddata", runWebSocket());


// Websocket stuff


function runWebSocket() {

    // set up websocket

    let ws = new WebSocket("ws://localhost:8080");
    let isConnected = false;

    // set up websocket listeners

    // onopen listener
    ws.onopen = function () {
        isConnected = true;
        console.log("Connection is established");
    };

    // message received listener
    ws.onmessage = function (messageEvent) {

        let msgObj = JSON.parse(messageEvent.data);

        if (msgObj.type === "join") {
            console.log("message returned from server: join: " + messageEvent.data);
            if(pageReference != "chatHTML") {
                htmlToUpdate.innerHTML = chatHTML;
                pageReference = "chatHTML";
                message = document.getElementById("msg");
                //message = document.getElementById("msg");
                console.log(msgObj);
                let myUsername = msgObj.user;
                let myRoom = msgObj.room;
                let placeToAddName = document.getElementById("userTitle")
                placeToAddName.textContent = "User: " + myUsername;
                let placeToAddRoom = document.getElementById("roomTitle")
                placeToAddRoom.textContent = "Room: " + myRoom;
                let myMessages = document.getElementById("chat");
                myMessages.value += myUsername + " joined " + myRoom + ".\n";
                htmlToUpdate.addEventListener("load", addChatListeners());
            } else {
                // new user joined (not current)
                let newUsername = msgObj.user;
                let myRoom = msgObj.room;
                let myMessages = document.getElementById("chat");
                myMessages.value += newUsername + " joined " + myRoom + ".\n";
            }

            let placeToAddName = document.getElementById("userTitle")
            // placeToAddName.textContent = "User: " + msgObj.user;
            if (placeToAddName.textContent == "Message") {
                placeToAddName.textContent = "User: " + myUsername;
            }

        } else if (msgObj.type === "leave"){
            // code to leave room
            console.log("message returned from server: leave: " + messageEvent.data);
            console.log(msgObj);
            let myUsername = msgObj.user;
            let myRoom = msgObj.room;
            // htmlToUpdate.innerHTML = loginHTML;
            // pageReference = "loginHTML";
            let myMessages = document.getElementById("chat");
            myMessages.value += myUsername + " left " + myRoom +".\n";

            console.log(msgObj);

        } else if (msgObj.type === "message"){
            // code to post message
            if (pageReference === "chatHTML"){
                console.log("message returned from server: message: " + messageEvent.data);
                let message = msgObj.message;
                console.log("message returned from server: " + message);
                let myMessages = document.getElementById("chat");
                console.log("my messages: " + myMessages);
                myMessages.value += message + "\n";
                let toClear = document.getElementById("msg")
                toClear.value = null;
            } else {
                htmlToUpdate.innerHTML=chatHTML;
                console.log("message returned from server: message: " + messageEvent.data);
                let message = msgObj.message;
                console.log("message returned from server: " + message);
                let myMessages = document.getElementById("chat");
                console.log("my messages: " + myMessages);
                myMessages.value += message + "\n";
                let toClear = document.getElementById("msg")
                toClear.value = null;

            }




        }
    };

    // error listener
    ws.onerror = function (e) {
        console.log("error");

    };

    // close listener
    ws.onclose = function (e) {
        console.log(e);
        console.log("Connection is now closed");
    };

    // function to validate user input to not be empty and only contain lower case characters
    function isValid(string){
        if (string.length === 0){
            return false;
        }
        for (let i = 0; i < string.length; i++){
            //console.log(string.charCodeAt(i));
            if (string.charCodeAt(i) < 97 || string.charCodeAt(i) > 122){
                return false;
            }
        }
        return true;
    }

    // add listeners on buttons

    // listeners for login page
    if (pageReference === "loginHTML") {
        // listener for join button, sends message to server
        joinButton.addEventListener("click", function () {
            if (isConnected) {
                if (isValid(roomname.value) && isValid(username.value)){
                    console.log("sending message: join " + username.value + " " + roomname.value);
                    ws.send("join " + username.value + " " + roomname.value);
                } else {
                    alert("invalid user input. Username and Room name require an input that can contain only contain lower case characters");
                }
            } else {
                console.log("Server connection lost");
            }
        });
    }

    // add listeners for chat page
    function addChatListeners()
    {

        let messageButton = document.getElementById("sendButton");
        let leaveButton = document.getElementById("leaveButton");

        if (pageReference === "chatHTML") {
            // listener for message button, sends message to server
            messageButton.addEventListener("click", function () {
                console.log("send clicked");
                if (isConnected) {
                    console.log("sending message: message " + username.value + " " + message.value);
                    ws.send("message " + username.value + " " + message.value);
                }
            });

            // listener for leave button, sends message to server
            leaveButton.addEventListener("click", function () {
                console.log("Leave clicked");
                if (isConnected) {
                    console.log("sending message: leave");
                    ws.send("leave");
                    htmlToUpdate.innerHTML = loginHTML;
                    pageReference = "loginHTML";
                }
            });
        }
    }
}
