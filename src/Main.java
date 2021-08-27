import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Gameplay panel = new Gameplay();

        frame.setBounds(10, 10, 700, 600);  // frame size is 700 x 600
        frame.setTitle("Swipe Brick Breaker");  // based on the ios game Swipe Brick Breaker
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
    }
}