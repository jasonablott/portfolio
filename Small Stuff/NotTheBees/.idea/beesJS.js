"use strict";



// make initial objects



// constructor type function that creates an initial bee and draws it
function makeBee(startX, startY){
    let bee = {};
    bee.x = startX;
    bee.y = startY;
    bee.speed = 0.1;
    bee.img = new Image();
    bee.img.src="beeImg.png";
    bee.img.onload = () => {context.drawImage(bee.img, bee.x, bee.y);};
    return bee;
}

// array to hold bees
let bees = [];

// number of bees to make
let numBees = 8;
// make that many bees
for (let i = 0; i < numBees; i++){
    let startX = Math.random()*i*175;
    let startY = Math.random()*i*125;
    let bee = makeBee(startX, startY);
    bees.push(bee);
}

// make the honey
let honey = {'x': 200, 'y:': 200};
honey.img = new Image();
honey.img.src="honeyImg.png"
honey.img.onload = () => {context.drawImage(honey.img, honey.x, honey.y);};

// set image sacles for drawing
let honeyScale = 0.5;
let beeScale = 0.35;



// load canvas



// get canvas to add things to
let myCanvas = document.getElementById("myCanvas");
// make cursor invisible for better user experience
myCanvas.style.cursor = "none";
let context = myCanvas.getContext("2d");



// functions for game



// keep track of mouse location
let mouseLocation = { 'x': 0, 'y':0}

// function to track the mouse coordinates
function handleMouseMove ( e ){
    //update mouse lcation
    mouseLocation.x = e.x;
    mouseLocation.y = e.y;
}

// function to update objects location based on mouse movement
function handleMove ( ){

    // move honey
    honey.x = mouseLocation.x - honey.img.width / 2;
    honey.y = mouseLocation.y - myCanvas.offsetTop - honey.img.height / 2;

    // move bees
    bees.forEach(bee => {
        if (Math.abs(bee.x - honey.x)<bee.speed){
            bee.x = honey.x;
        }
        else if (bee.x < honey.x){
            bee.x += Math.random()*bee.speed;
        } else {
            bee.x -= Math.random()*bee.speed;
        }

        if (Math.abs(bee.y - honey.y)<bee.speed){
            bee.y = honey.y;
        }
        else if (bee.y < honey.y){
            bee.y += Math.random()* bee.speed;
        } else {
            bee.y -= Math.random()* bee.speed;
        }
    });

    // update the bees speed
    bees.forEach(bee => {bee.speed += .01});
}

// function to draw each object that we have
function draw(){
    // clear the frame
    context.clearRect(0,0, 800, 800);
    // draw the honey
    //let honeyScale = 0.5;
    context.drawImage(honey.img, honey.x, honey.y, honey.img.width*honeyScale, honey.img.height*honeyScale);
    // draw the bees
    //let beeScale = 0.35;
    bees.forEach(bee => { context.drawImage(bee.img, bee.x, bee.y, bee.img.width*beeScale, bee.img.height*beeScale) });
}

// helper to check if any of the bees are touching the honey
function touchingHoney(beeX, beeY){
    if ((beeX < honey.x + (honey.img.width*honeyScale/4)) && (beeX > honey.x - (honey.img.width*honeyScale/4)) &&
        (beeY < honey.y + (honey.img.height*honeyScale/4)) && (beeY > honey.y - (honey.img.height*honeyScale/4))){
        gameRun = false;
        alert("Game Over! Refresh page to start over");
    }
}

// main game function handles mouse moves, draws, checks for contact, and requests animation frame
function mainGameLoop(){
    if (gameRun === true){
        handleMove();
        draw();
        bees.forEach(bee => {
            touchingHoney(bee.x, bee.y);
        });
        window.requestAnimationFrame(mainGameLoop);
    }
}

// variable to end mainGameLoop
let gameRun = true;

// call the main game loop
mainGameLoop();

// set our mousemove event listener
myCanvas.addEventListener("mousemove", handleMouseMove);
