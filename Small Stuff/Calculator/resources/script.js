
// get elements from page for AJAX method
let x = document.getElementById("xVal");
let y = document.getElementById("yVal");
let submit = document.getElementById("calculate");
let result = document.getElementById("result");

// get elements from page for WebServer method
let x2 = document.getElementById("xVal2");
let y2 = document.getElementById("yVal2");
let submit2 = document.getElementById("calculate2");
let result2 = document.getElementById("result2");

// get elements from page for Fetch method
let x3 = document.getElementById("xVal3");
let y3 = document.getElementById("yVal3");
let submit3 = document.getElementById("calculate3");
let result3 = document.getElementById("result3");



// websocket method



let ws = new WebSocket("ws://localhost:8080");
let isConnected = false;

ws.onopen = function () {
    isConnected = true;
    console.log("Connection is established");
};

// works when mesg is recvd
ws.onmessage = function(messageEvent){
    //alert(messageEvent.data);
    result2.textContent = " WebSocket Result = " + messageEvent.data;
};

ws.onerror = function(e){

};

ws.onclose = function (e){
    console.log("Connection is now closed");
};

submit2.addEventListener("click", function (){

    let xValue = Number(x2.value);
    let yValue = Number(y2.value);
    if(!isNaN(xValue) || isNaN(yValue)){

        if(isConnected);
        ws.send(xValue + " " + yValue)
    }
});



// Fetch method



submit3.addEventListener("click", function(){
    let xValue = Number(x3.value);
    let yValue = Number(y3.value);
    if(!isNaN(xValue) && !isNaN(yValue)){

        fetch("http://localhost:8080/calculate?x=" + xValue + "&y=" + yValue)
            .then(response => {
                if (!response.ok){
                    console.log("I had an error");
                }
                return response.text();
            }) .then(data => {
            //alert(data);
            result3.textContent = "Fetch Method Result = " + data;
        });
    }
});



// AJAX Method



submit.addEventListener("click", function() {
    // ajax code to update the page based on input
    let ajaxRequest = new XMLHttpRequest();

    // force values to be number
    let xValue = Number(x.value);
    let yValue = Number(y.value);

    if(!isNaN(xValue) && !isNaN(yValue)) {
        console.log("inputs are Valid");
        ajaxRequest.open("GET", "http://localhost:8080/calculate?x=" + x.value + "&y=" + y.value);
        ajaxRequest.addEventListener("load", function () {
            result.textContent = "XMLHttpRequest Result = " + this.responseText;
        });
        // handle error
        ajaxRequest.addEventListener("error", function(){
            console.log(this.response);
        });
        // send request after set up
        ajaxRequest.send();
    } else {
        alert("input is not a number");
    }
});