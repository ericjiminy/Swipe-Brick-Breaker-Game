import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;

public class Gameplay extends JPanel implements MouseMotionListener, MouseListener, KeyListener, ActionListener{

    private boolean started = false;    // game starts when the player left-clicks
    private boolean playing = false;    // is the game playing or paused
    private boolean gameOver = false;   // game over if the ball touches the ground and lives = 0
    private boolean levelUp = false;    // level up when totalBricks reaches 0;
    private int balls = 3;  // remaining balls/lives
    private int score = 0;  // number of blocks hit
    private int level = 1;  // current level, determines highest possible brick value
    private int totalBricks;    // the number of remaining bricks in the level
    
    private Timer timer;
    private int delay = 1;

    private final int FLOOR = 500;  // black border below the player
    private final int CEILING = 65; // black border above the bricks
    private final int PLAYERY = 475;    // player/paddle is locked at height 475

    private int playerX = 310;  // initial player position

    private final int INIT_Y = 455; // initial ball position
    private final int INIT_X_DIR = -3;  // initial ball direction
    private final int INIT_Y_DIR = -4;

    private ArrayList<int[]> activeBalls;    // balls that are currently in play

    private BricksGenerator bricks;

    public Gameplay() {
        bricks = new BricksGenerator(1, 3, 7); // 3 x 7 grid of blocks
        activeBalls = new ArrayList<int[]>();
        totalBricks = bricks.totalBricks;
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // background
        g.setColor(Color.white);
        g.fillRect(0, 0, 700, 600);

        // floor and ceiling
        g.setColor(Color.black);    // top and bottom border lines
        g.fillRect(0, FLOOR, 700, 7);
        g.fillRect(0, CEILING, 700, 7);

        // score
        g.setColor(new Color(160, 160, 170));
        g.setFont(new Font("Helvetica", Font.BOLD, 26));

        int scoreWidth = g.getFontMetrics().stringWidth("Score: " + score);
        g.drawString("Score: " + score, 670 - scoreWidth, 40);

        // balls
        int ballsWidth = g.getFontMetrics().stringWidth("Balls: " + balls);
        g.drawString("Balls: " + balls, 630 - scoreWidth - ballsWidth, 40);

        // level
        int levelWidth = g.getFontMetrics().stringWidth("Level: " + level);
        g.drawString("Level: " + level, 590 - scoreWidth - ballsWidth - levelWidth, 40);

        // bricks
        bricks.draw((Graphics2D) g);

        // player
        g.setColor(new Color(160, 160, 170));
        g.fillRect(playerX, PLAYERY, 80, 8);

        // ball
        g.setColor(new Color(50, 170, 255));

        if (balls > 0) {
            g.fillOval(playerX+33, INIT_Y, 15, 15);
        }

        for (int i = 0; i < activeBalls.size(); i++) {
            int ball[] = activeBalls.get(i);
            g.fillOval(ball[0], ball[1], 15, 15);
        }

        // start screen
        if (!started) { // show start instructions at the start of the game
            g.setColor(new Color(160, 160, 170, 120));
            g.fillRect(0, 200, 700, 120);

            g.setColor(new Color(255, 255, 255, 255));
            g.setFont(new Font("Helvetica", Font.BOLD, 60));
            int startWidth = g.getFontMetrics().stringWidth("Click to start");
            g.drawString("Click to start", 350-startWidth/2, 260);

            g.setFont(new Font("Helvetica", Font.BOLD, 32));
            int pauseWidth = g.getFontMetrics().stringWidth("'p' to pause");
            g.drawString("'p' to pause", 350-pauseWidth/2, 300);
        }

        // pause screen
        if (!timer.isRunning() && !levelUp) {   // pause game and show pause instructions when player presses 'p'
            g.setColor(new Color(160, 160, 170, 130));
            g.fillRect(0, 0, 700, 600);

            g.setColor(new Color(255, 255, 255, 255));
            g.setFont(new Font("Helvetica", Font.BOLD, 60));
            int pausedWidth = g.getFontMetrics().stringWidth("Paused");
            g.drawString("Paused", 350-pausedWidth/2, 260);

            g.setFont(new Font("Helvetica", Font.BOLD, 32));
            int unpauseWidth = g.getFontMetrics().stringWidth("'p' to unpause");
            g.drawString("'p' to unpause", 350-unpauseWidth/2, 305);
        }

        // level up screen
        if (levelUp) {
            g.setColor(new Color(160, 160, 170, 90));
            g.fillRect(0, 0, 700, 600);

            g.setColor(new Color(255, 255, 255, 255));
            g.setFont(new Font("Helvetica", Font.BOLD, 60));
            int newLevelWidth = g.getFontMetrics().stringWidth("Level: "+(level+1));
            g.drawString("Level: "+(level+1), 350-newLevelWidth/2, 280);
        }

        // game over screen
        if (gameOver) { // show game over instructions and final score
            g.setColor(new Color(160, 160, 170, 130));
            g.fillRect(0, 0, 700, 600);

            g.setColor(new Color(255, 255, 255, 255));
            g.setFont(new Font("Helvetica", Font.BOLD, 60));
            int gameOverWidth = g.getFontMetrics().stringWidth("Game Over");
            g.drawString("Game Over", 350-gameOverWidth/2, 255);

            g.setFont(new Font("Helvetica", Font.BOLD, 28));
            int finalScoreWidth = g.getFontMetrics().stringWidth("Score: " + score);
            g.drawString("Score: " + score, 350-finalScoreWidth/2, 295);

            g.setFont(new Font("Helvetica", Font.BOLD, 34));
            int playAgainWidth = g.getFontMetrics().stringWidth("'Enter' to play again");
            g.drawString("'Enter' to play again", 350-playAgainWidth/2, 337);
        }        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        if (playing) {   // check ball/player collision
            for (int b = 0; b < activeBalls.size(); b++) {
                int ball[] = activeBalls.get(b);
                Rectangle ballRect = new Rectangle(ball[0], ball[1], 15, 15);
                
                if (ballRect.intersects(new Rectangle(playerX, PLAYERY, 80, 8))) {
                    if (ball[1]+10 <= PLAYERY) {
                        ball[1] = PLAYERY-15;
                        ball[3] = -ball[3];   // ball bounces up when it touches the player/paddle
                    }
                }

                A: for (int i = 0; i < bricks.map.length; i++) {    // draw and check collisions for remaining bricks
                    for (int j = 0; j < bricks.map[0].length; j++) {
                        if (bricks.map[i][j] > 0) {
                            int brickX = bricks.centerHelper + j*(bricks.brickWidth+7);
                            int brickY = CEILING + bricks.centerHelper + i*(bricks.brickHeight+7);
                            int brickWidth = bricks.brickWidth;
                            int brickHeight = bricks.brickHeight;

                            Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);   // new rectangles to check ball/brick collisions

                            if (brickRect.intersects(ballRect)) {   // if there's a collision, decrement the brick value and update score
                                bricks.decBrickValue(i, j);
                                totalBricks--;
                                score += 1;

                                if (ball[0]+13 <= brickX || ball[0]+2 >= brickX + brickWidth) {   
                                    ball[2] = -ball[2];   // if the ball hits the side of a brick, change the x direction
                                } else {
                                    ball[3] = -ball[3];   // if the ball hits the top or bottom of a brick, change the y direction
                                }

                                

                                break A;    // if there's a collision, break from both for-loops
                            }
                        }
                    }
                }

                if (totalBricks <= 0) {
                    levelUp = true;



                    level++;
                    balls += activeBalls.size()+1;
                    activeBalls.clear();
                    bricks = new BricksGenerator(level, 3, 7); // 3 x 7 grid of blocks
                    totalBricks = bricks.totalBricks;
                    playing = false;
                    levelUp = false;
                }

                if (ball[1] >= FLOOR-10) { // check if the ball hits the floor
                    activeBalls.remove(b);  // remove the dead ball from play
                    if (balls > 0) {    // if the player still has balls left, let them shoot another ball
                        ball[0] = playerX+33;
                        ball[1] = INIT_Y;
                        ball[2] = INIT_X_DIR;
                        ball[3] = INIT_Y_DIR;
                    } else if (activeBalls.size() <= 0) {
                        ball[1] = FLOOR-20;    // if the player has no balls left, it's game over
                        gameOver = true;
                    }      
                }

                ball[0] += ball[2];   // update the ball location according to the x and y velocities
                ball[1] += ball[3];
                if (ball[0] < 0) ball[2] = -ball[2]; // the ball bounces off the side walls
                if (ball[0] > 670) ball[2] = -ball[2];
                if (ball[1] < CEILING+10) ball[3] = -ball[3];    // the ball bounces off the ceiling
            }
        }
        repaint();
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        if (playing || !gameOver) { // the paddle follows the cursor during the game
            playerX = e.getX() - 40;
            if (playerX >= 610) playerX = 610;
            if (playerX <= 0) playerX = 0; 
        }

        if (!playing && !gameOver) {    // new balls follow the paddle (at the start and when a ball is lost)
            for (int b = 0; b < activeBalls.size(); b++) {
                activeBalls.get(b)[0] = playerX+33;
            }
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {  // 'p' pauses the game
            if (playing) {
                if (timer.isRunning()) {
                    timer.stop();
                    repaint();
                }
                else timer.start();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {  // 'Enter' restarts the game when it's game over
            if (gameOver) {
                gameOver = false;
                balls = 3;
                activeBalls.clear();
                score = 0;
                level = 1;
                bricks = new BricksGenerator(level, 3, 7);
                totalBricks = bricks.totalBricks;
                started = false;
            }
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {    // mouse-click starts the game
        if (!gameOver) {
            playing = true;
            started = true;

            if (balls > 0) {
                int ball[] = {playerX+33, INIT_Y, INIT_X_DIR, INIT_Y_DIR};
                activeBalls.add(ball);
                balls--;
            }
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
