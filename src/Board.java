import javax.swing.*;

public class Board {
    private int tileWidth;
    private int tileHeight;

    private int bombCount;
    private int freeFlags;

    private boolean gameStarted;
    private boolean gameLocked;

    private Cell[][] board;

    public Board(int tileWidth, int tileHeight, int bombCount) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.bombCount = bombCount;
        this.freeFlags = bombCount;

        this.gameStarted = false;
        this.gameLocked = false;

        this.board = new Cell[tileHeight][tileWidth];

        for(int row = 0; row < tileHeight; row++) {
            for(int col = 0; col < tileWidth; col++) {
                if( (row + col) % 2 == 0 ) board[row][col] = new Cell(true);
                else board[row][col] = new Cell(false);
            }
        }
    }

    public boolean isGameLocked() { return gameLocked; }
    public boolean isGameStarted() { return gameStarted; }

    public Tile[] getTileBoard() {
        Tile[] returnArray = new Tile[tileHeight * tileWidth];

        int index = 0;
        for(int row = 0; row < tileHeight; row++) {
            for(int col = 0; col < tileWidth; col++) {
                returnArray[index++] = new Tile(board[row][col].getTileType(), board[row][col].getTileContent());
            }
        }

        return returnArray;
    }

    // FloodFill can use Cell.isHidden as seen variable for BFS(more like traversal)
}
