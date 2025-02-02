// include https://imagej.net/ij/macros/TurtleGraphicsLibrary.ijm
// Author: Jerome Mutterer
//
// The 'include' in the first line is ignored by the Fiji
// script editor. To work around this problem, append the
// contents of TurtleGraphicsLibrary.ijm to the end of
// this macro.

requires("1.54d");
turtleGraphics();
clearScreen();
//hideTurtle();

setColor("red");
for (i=0;i<60;i++) {
    forward(30);
    right(15*(i%5));
}

setColor("blue");
setLineWidth(2);
for (i=0;i<72;i++) {
    penDown();
    if (i%2==0) penUp();
    forward(10); 
    left(5);
}

penUp();
setPosXY(100,-100);
penDown();
setColor("magenta");
square (100);

function square(side) {
    for (i=0;i<4;i++) {
        forward(side);
        right(90);
    }
}
