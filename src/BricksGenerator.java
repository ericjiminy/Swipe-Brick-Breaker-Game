import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class BricksGenerator {
    
    public int map[][]; // use a map of rows and cols to represent the grid of bricks
    public int brickWidth;
    public int brickHeight;
    public int centerHelper;    // an integer offset to center the grid of bricks in the screen
    public int totalBricks;

    private final int CEILING = 65;

    public BricksGenerator(int level, int row, int col) {
        Random rand = new Random();
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int val = rand.nextInt(level+1);
                map[i][j] = val;
                totalBricks += val;
            }
        }

        brickWidth = 540/col;
        brickHeight = 150/row;
        centerHelper = (700 - (540 + (540/brickWidth)*8)) / 2;  // calculate the offset using the screen size, grid width, and brick width
    }

    public void draw(Graphics2D g) {    // draw a grid of bricks according to the specified rows and columns
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {   // only draw bricks with value > 0
                    int x = centerHelper + j*(brickWidth+7);
                    int y = CEILING + centerHelper + i*(brickHeight+7);

                    g.setColor(new Color(255, 180, 100));
                    g.fillRect(x, y, brickWidth, brickHeight);

                    g.setColor(Color.white);    // draw brick value in the middle of each brick
                    g.setFont(new Font("Helvetica", Font.BOLD, 28));
                    int valueWidth = g.getFontMetrics().stringWidth(""+map[i][j]);
                    int valueHeight = g.getFontMetrics().getHeight();
                    g.drawString(""+map[i][j], x + brickWidth/2 - valueWidth/2, y + brickHeight/2 + valueHeight/4);
                }
            }
        }
    }

    public void decBrickValue(int row, int col) {    // decrement the brick value
        map[row][col]--;
    }
}
