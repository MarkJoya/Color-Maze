import processing.core.PApplet;

/**
 * Created by Mark Joya on 25/12/16.
 * Imported from existing Processing project and converted to IntelliJ syntax
 **/
public class MainApp extends PApplet {

    public static void main(String[] args) {
        PApplet.main("MainApp");
    }

    // TODO:
    //Leaderboard - for self only - run a timer during the game and show this for every iteration
    //  -this might be useful: print(System.currentTimeMillis());
    //Try and fix up avatar moving onto other side of line even after loss condition - use X symbol or highlight wall RED
    //Fix text at start
    //Change the 1,0 conditions to booleans
    //Fix line ends
    //Random maze generation?
    //Bigger maze
    //PRESS ENTER TO RESTART DURING GAME

    private final static int DIMS = 500;
    private final static int SQPERROW = 5;
    private final static int SQUAREWIDTH = DIMS / SQPERROW;
    private final static int SQUAREHALF = SQUAREWIDTH / 2;
    private final static int MIL_PER_CENTI = 10;
    private final static int MIL_PER_SEC = 1000;
    private final static int MIL_PER_MIN = 60000;

    private final int BGRED = color(255, 98, 103); //#FF6267
    private final int BGBLUE = color(98, 112, 255); //#6270FF
    private final int BGGREEN = color(14, 227, 22); //#0EE316
    private final int BGYELLOW = color(227, 217, 14); //#E3D90E
    private final int BGWHITE = color(255, 255, 255);
    private final int AVRED = color(255, 0, 0);
    private final int AVBLUE = color(0, 0, 255);
    private final int AVGREEN = color(0, 255, 0);
    private final int AVYELLOW = color(255, 255, 0);

    private Avatar player;
    private Square[][] maze;
    private Stopwatch timer;
    private boolean showStartScreen;
    private boolean restartCond;
    private boolean timerStarted;

    public void settings() {
        size(500, 500);
    }

    public void setup() {
        maze = new Square[SQPERROW][SQPERROW];
        showStartScreen = true;
        restartCond = false;
        timerStarted = false;
        newSketch();
    }

    public void draw() {
        if (showStartScreen) {
            startMenu();
        } else {
            if (!timerStarted) {
                timer = new Stopwatch(50, 50);
                timerStarted = true;
            }
            timer.displayTime();
            drawGrid();
            drawBorder();
            player.display();
            player.winCheck();
            player.loseCheck();
        }
    }

    //Sets starting state for a new sketch
    private void newSketch() {
        player = new Avatar();

        restartCond = false;

        //Set up maze squares - random colour and fixed position
        for (int i = 0; i < SQPERROW; i++) {
            for (int j = 0; j < SQPERROW; j++) {
                int colorSelect = (int) random(0, 4);
                if (colorSelect == 0) {
                    maze[i][j] = new Square(BGRED, SQUAREHALF + SQUAREWIDTH * j, SQUAREHALF + SQUAREWIDTH * i);
                } else if (colorSelect == 1) {
                    maze[i][j] = new Square(BGBLUE, SQUAREHALF + SQUAREWIDTH * j, SQUAREHALF + SQUAREWIDTH * i);
                } else if (colorSelect == 2) {
                    maze[i][j] = new Square(BGGREEN, SQUAREHALF + SQUAREWIDTH * j, SQUAREHALF + SQUAREWIDTH * i);
                } else {
                    maze[i][j] = new Square(BGYELLOW, SQUAREHALF + SQUAREWIDTH * j, SQUAREHALF + SQUAREWIDTH * i);
                }
            }
        }
        //Set top left starting square to be white by default
        maze[0][0].setColor(BGWHITE);

        //Set bottom right square to be end square
        maze[SQPERROW - 1][SQPERROW - 1].setWinSq();

        //Hard coded maze grid - doesn't include outer border
        //Format: Top, Right, Bottom, Left. Each String is a grid square, laid out in grid order.
        String[][] gridLines = {{"0010", "0100", "0011", "0000", "0010"},
                {"1010", "0100", "1001", "0010", "1100"},
                {"1000", "0110", "0011", "1100", "0101"},
                {"0000", "1010", "1100", "0101", "0101"},
                {"0110", "1001", "0000", "0100", "0101"}};

        //Set the grid lines for each square
        for (int i = 0; i < SQPERROW; i++) {
            for (int j = 0; j < SQPERROW; j++) {
                maze[i][j].setSqGrid(gridLines[i][j]);
            }
        }
        println("Starting Posn:", player.yPosGrid, player.xPosGrid);
    }

    //Shows the Start Menu screen before the game starts
    private void startMenu() {
        background(255, 255, 255, 200);
        textSize(20);
        textAlign(CENTER);
        fill(0, 0, 0);
        text("Get to the end of the maze without touching any walls. Change the color of your player to match the maze tiles before moving onto them.\n A/S/D/F: Change Colours. Arrow Keys: Movement.\n\n CLICK TO START.", DIMS / 5, DIMS / 5, DIMS * 3 / 5, DIMS * 3 / 5);
        noLoop();
    }

    //Registers a mouse press to start the game
    public void mouseClicked() {
        if (showStartScreen) {
            loop();
            showStartScreen = false;
            println("Finished showStartScreen loop");
        }
    }

    //Register what key is pressed for the player class
    //Blocks key presses if start screen is showing
    public void keyPressed() {
        if (!showStartScreen) {
            if (!restartCond) {
                player.keyPressed();
            } else if (keyCode == ENTER) {
                newSketch();
                loop();
            }
        }
    }

    //Draw the interior grid of squares
    private void drawGrid() {
        stroke(0, 0, 0, 40);
        for (int x = 0; x < SQPERROW; x++) {
            for (int y = 0; y < SQPERROW; y++) {
                maze[x][y].display();
            }
        }
    }

    //Draws the exterior border of maze
    private void drawBorder() {
        stroke(0, 0, 0);
        strokeWeight(10);
        noFill();
        rect(0, 0, DIMS, DIMS);
    }

    private boolean isColorMatch(Square gridSquare, Avatar player) {
        if (gridSquare.c == BGWHITE) return true;
        if (player.c == AVRED) if (gridSquare.c == BGRED) return true;
        if (player.c == AVBLUE) if (gridSquare.c == BGBLUE) return true;
        if (player.c == AVGREEN) if (gridSquare.c == BGGREEN) return true;
        if (player.c == AVYELLOW) if (gridSquare.c == BGYELLOW) return true;
        return false;
    }

    //Class for player character
    class Avatar {
        int c;
        int xpos;
        int ypos;
        int xPosGrid;
        int yPosGrid;
        boolean topBlock;
        boolean rightBlock;
        boolean botBlock;
        boolean leftBlock;

        Avatar() {
            c = AVRED;
            xpos = SQUAREWIDTH / 2;
            ypos = SQUAREWIDTH / 2;
            xPosGrid = 0;
            yPosGrid = 0;
        }

        //Display the character on screen
        void display() {
            stroke(0);
            strokeWeight(1);
            fill(c);
            ellipse(xpos, ypos, 25, 25);
        }

        //Determines what to do when a key is pressed - movement and colour change
        void keyPressed() {
            //Separate if conditions for up, down, left, right movement
            if (key == CODED && keyPressed) {
                if (keyCode == UP) {
                    ypos = ypos - SQUAREWIDTH;
                    yPosGrid--;
                    println("Posn:", yPosGrid, xPosGrid);
                } else if (keyCode == DOWN) {
                    ypos = ypos + SQUAREWIDTH;
                    yPosGrid++;
                    println("Posn:", yPosGrid, xPosGrid);
                } else if (keyCode == LEFT) {
                    xpos = xpos - SQUAREWIDTH;
                    xPosGrid--;
                    println("Posn:", yPosGrid, xPosGrid);
                } else if (keyCode == RIGHT) {
                    xpos = xpos + SQUAREWIDTH;
                    xPosGrid++;
                    println("Posn:", yPosGrid, xPosGrid);
                }
            } else if (keyPressed) {
                if (key == 'a' || key == 'A') {
                    c = AVRED;
                } else if (key == 's' || key == 'S') {
                    c = AVBLUE;
                } else if (key == 'd' || key == 'D') {
                    c = AVGREEN;
                } else if (key == 'f' || key == 'F') {
                    c = AVYELLOW;
                }
            }
        }

        //Checks to see if avatar is in the win square - hardcoded to be bottom right square
        void winCheck() {
            int winXpos = (SQUAREWIDTH / 2) + SQUAREWIDTH * (SQPERROW - 1);
            int winYpos = winXpos;
            if ((xpos == winXpos) && (ypos == winYpos) && (isColorMatch(maze[yPosGrid][xPosGrid], player))) {
                textSize(60);
                textAlign(CENTER);
                fill(0, 255, 255);
                text("You Win!", DIMS / 2, DIMS / 2);
                restartMessage();
            }
        }

        //Checks to see if game has been lost, conditions:
        // 1) Avatar moves outside border
        // 2) Avatar moves into a wall
        // 3) Avatar moves into a square of different color to self
        void loseCheck() {

            //Border Check
            if ((xpos < 0) || (xpos > DIMS) || (ypos < 0) || (ypos > DIMS)) {
                loseMessage();
                println("Lose: Border");
                //Wall Check
            } else {
                checkBlocked(xPosGrid, yPosGrid);
                //Grid Square Check
                if ((keyCode == UP) && (botBlock)) {
                    loseMessage();
                    println("Lose: UP blocked");
                } else if ((keyCode == RIGHT) && (leftBlock)) {
                    loseMessage();
                    println("Lose: RIGHT blocked");
                } else if ((keyCode == DOWN) && (topBlock)) {
                    loseMessage();
                    println("Lose: DOWN blocked");
                } else if ((keyCode == LEFT) && (rightBlock)) {
                    loseMessage();
                    println("Lose: LEFT blocked");
                }

                //Colour Check
                if (isColorMatch(maze[yPosGrid][xPosGrid], player)) {
                    eraseColor(); //Removes color of current tile if ok
                } else {
                    loseMessage();
                    println("Lose: Colour");
                }
            }
        }

        //Prints the lose message and stops the game
        void loseMessage() {
            textSize(60);
            textAlign(CENTER);
            fill(0, 255, 255);
            text("You Lose!", DIMS / 2, DIMS / 2);
            restartMessage();
        }

        void restartMessage() {
            textSize(30);
            text("Press ENTER to start again", DIMS / 2, DIMS / 2 + 50);
            restartCond = true;
            noLoop();
        }

        //Retrieves the wall status for the current square and sets player direction variables to "blocked" accordingly
        void checkBlocked(int xPosGrid, int yPosGrid) {
            topBlock = maze[yPosGrid][xPosGrid].topL == 1;
            rightBlock = maze[yPosGrid][xPosGrid].rightL == 1;
            botBlock = maze[yPosGrid][xPosGrid].botL == 1;
            leftBlock = maze[yPosGrid][xPosGrid].leftL == 1;
        }

        //Erases color of grid square that player is on
        void eraseColor() {
            maze[yPosGrid][xPosGrid].setColor(BGWHITE);
        }
    }

    //Class for Grid Squares
    class Square {
        int c;
        int xpos;
        int ypos;
        int leftCoord;
        int rightCoord;
        int topCoord;
        int botCoord;
        int leftL;
        int rightL;
        int topL;
        int botL;
        boolean winSq;

        Square(int tempC, int tempXpos, int tempYpos) {
            c = tempC;
            xpos = tempXpos;
            ypos = tempYpos;
            leftCoord = xpos - SQUAREHALF;
            rightCoord = xpos + SQUAREHALF;
            topCoord = ypos - SQUAREHALF;
            botCoord = ypos + SQUAREHALF;
        }

        //Displays a square and draws square walls
        void display() {
            stroke(0, 0, 0, 40);
            strokeWeight(1);
            fill(c);
            rect(leftCoord, topCoord, SQUAREWIDTH, SQUAREWIDTH);

            if (leftL == 1) {
                drawLeft();
            }
            if (rightL == 1) {
                drawRight();
            }
            if (topL == 1) {
                drawTop();
            }
            if (botL == 1) {
                drawBot();
            }

            if (winSq) {
                fill(255, 255, 255);
                textSize(20);
                text("GOAL", leftCoord + SQUAREHALF, topCoord + SQUAREHALF);
            }
        }

        //Draws left wall
        void drawLeft() {
            stroke(0, 0, 0);
            strokeWeight(5);
            line(leftCoord, topCoord, leftCoord, botCoord);
        }

        //Draws right wall
        void drawRight() {
            stroke(0, 0, 0);
            strokeWeight(5);
            line(rightCoord, topCoord, rightCoord, botCoord);
        }

        //Draws top wall
        void drawTop() {
            stroke(0, 0, 0);
            strokeWeight(5);
            line(leftCoord, topCoord, rightCoord, topCoord);
        }

        //Draws bottom wall
        void drawBot() {
            stroke(0, 0, 0);
            strokeWeight(5);
            line(leftCoord, botCoord, rightCoord, botCoord);
        }

        void setWinSq() {
            winSq = true;
        }

        //Sets a new color for a square
        void setColor(int newCol) {
            c = newCol;
        }

        // Input string determines what grid lines are drawn around the square - sets variables
        // Input Order: Top, Right, Bottom, Left as an ARRAY
        void setSqGrid(String grid) {
            if (grid.charAt(0) == '1') {
                topL = 1;
            }
            if (grid.charAt(1) == '1') {
                rightL = 1;
            }
            if (grid.charAt(2) == '1') {
                botL = 1;
            }
            if (grid.charAt(3) == '1') {
                leftL = 1;
            }
        }
    }

    //COMMENT THIS AND ALL FUNCTIONS IN IT
    class Stopwatch {
        long startTime;
        int xPos;
        int yPos;
        int centiSecs;
        int secs;
        int mins;

        Stopwatch(int atX, int atY) {
            xPos = atX;
            yPos = atY;
            startTime = millis();
            centiSecs = 0;
            secs = 0;
            mins = 0;
        }

        int currentTime() {
            return (int) (millis() - startTime);
        }

        void displayTime() {
            println("Current time is", currentTime());

            centiSecs = currentTime() / MIL_PER_CENTI;
            secs = currentTime() / MIL_PER_SEC;
            mins = currentTime() / MIL_PER_MIN;

            println(mins, secs, centiSecs);

            textSize(60);
            textAlign(CENTER);
            fill(255, 255, 255);
            text("test!", DIMS / 2, DIMS / 2);
            //TODO: SET COLOR AND POSITION AND PUT THIS IN DRAW
        }

    }
}