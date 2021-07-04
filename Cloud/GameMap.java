package Game;

import javax.swing.*;
import java.awt.*;

public class GameMap extends JPanel {

    public static final int GAME_MODE_HVH = 0;
    public static final int GAME_MODE_HVA = 1;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        for (int x1 = GameWindow.WIN_POS_X; x1 <= GameWindow.WIN_POS_X + GameWindow.WIN_WIDTH; x1 += GameWindow.WIN_WIDTH / 4) {
            g.drawLine(x1, GameWindow.WIN_POS_Y, x1, GameWindow.WIN_POS_Y + GameWindow.WIN_HEIGHT);
        }
    }

    GameMap() {
        setBackground(Color.BLACK);
    }

        void start( int gameMode, int fieldSizeX, int fieldSizeY, int winLength) {
            System.out.println("gameMode: " + gameMode + "\nfieldSizeX: " + fieldSizeX +
                    "\nfieldSizeY: " + fieldSizeY + "\nwinLength: " + winLength);
        }
}
