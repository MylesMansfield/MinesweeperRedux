import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.LinkedBlockingQueue;

public class Game {
    private static final int tileWidth  = 18;
    private static final int tileHeight = 14;
    private static final int cellWidth  = 40;
    private static final int cellHeight = 40;

    private static final int bombCount  = 40;

    private static LinkedBlockingQueue<MouseEvent> clickQueue = new LinkedBlockingQueue<MouseEvent>();

    private static GameUI gameUI;

    public static void main(String[] args) {
        gameUI = new GameUI(tileWidth, tileHeight, cellWidth, cellHeight, bombCount);
        gameLoop();
    }

    private static void gameLoop() {
        while(true) {
            Board board = new Board(tileWidth, tileHeight, bombCount);
            int clicksMade = 0;

            while(!board.isGameLocked()) {
                handleClick(board); // Handles logic from valid clicks

                if(clicksMade == 0) gameUI.handleBegin();
                clicksMade++;

                // Update GUI
                SwingUtilities.invokeLater(() -> {
                    gameUI.updateControlPanel();
                    gameUI.updateBoardPanel(board.getTileBoard());
                });
            }

            gameUI.handleEnd(board.getIsWin());

            // Block until user is ready to start new game then letting thread continue will generate new game
            while(true); // Bootleg Wait TODO: block main until user selects restart
        }
    }

    public static void updateClick(MouseEvent event) {
        try {
            clickQueue.put(event);
        } catch (InterruptedException e) {
            // Ignore Click Event if something actually ends up interrupting this
        }
    }

    private static void handleClick(Board board) {
        MouseEvent event = null;

        try {
            event = clickQueue.take();
        } catch (InterruptedException e) {
            // Ignore Click Event if something actually ends up interrupting this
        }

        if(event == null) return;

        int tileRow = event.getY() / cellHeight;
        int tileCol = event.getX() / cellWidth;

        if(event.getButton() == 1) { // Left Click
            board.handleClick(tileRow, tileCol);
        } else { // Right Click
            board.changeFlag(tileRow, tileCol);
        }
    }
}
