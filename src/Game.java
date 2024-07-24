import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.util.concurrent.LinkedBlockingQueue;

public class Game {
    private static final int tileWidth  = 18;
    private static final int tileHeight = 14;
    private static final int cellWidth  = 40;
    private static final int cellHeight = 40;

    private static final int bombCount  = 45;

    private static LinkedBlockingQueue<MouseEvent> clickQueue = new LinkedBlockingQueue<MouseEvent>();
    private static volatile boolean restartGame = false;

    private static GameUI gameUI;

    public static void main(String[] args) {
        gameUI = new GameUI(tileWidth, tileHeight, cellWidth, cellHeight, bombCount);
        gameLoop();
    }

    private static void gameLoop() {
        while(true) { // Overarching game loop that lets you play as many games as you want
            Board board = new Board(tileWidth, tileHeight, bombCount);
            int clicksMade = 0;

            SwingUtilities.invokeLater(() -> {
                gameUI.updateControlPanel(board.getFreeFlags());
                gameUI.updateBoardPanel(board.getTileBoard());
                gameUI.unlockBoard();
            });

            while(!board.isGameLocked()) { // Game loop for a single game
                handleClick(board);

                if(clicksMade == 0) gameUI.startTimer();
                clicksMade++;

                // Update GUI
                SwingUtilities.invokeLater(() -> {
                    gameUI.updateControlPanel(board.getFreeFlags());
                    gameUI.updateBoardPanel(board.getTileBoard());
                });
            }

            SwingUtilities.invokeLater(() -> {
                gameUI.handleEnd(board.isWin(), board.getTileBoard());
            });

            while(!restartGame);
            restartGame = false;
        }
    }

    public static void updateClick(MouseEvent event) {
        try {
            clickQueue.put(event);
        } catch (InterruptedException e) {}
    }

    private static void handleClick(Board board) {
        MouseEvent event = null;

        try { event = clickQueue.take(); }
        catch (InterruptedException e) {}

        if(event == null) return;

        int tileRow = event.getY() / cellHeight;
        int tileCol = event.getX() / cellWidth;

        if(event.getButton() == 1) { // Left Click
            board.handleClick(tileRow, tileCol);
        } else { // Right Click
            board.changeFlag(tileRow, tileCol);
        }
    }

    public static void restartGame() { restartGame = true; }
}
