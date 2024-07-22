import java.util.Random;

public class Board {
    private int tileWidth;
    private int tileHeight;

    private int bombCount;
    private int freeFlags;

    private boolean gameStarted = false;
    private boolean gameLocked = false;
    private boolean isWin = false;

    private Cell[][] board;

    public Board(int tileWidth, int tileHeight, int bombCount) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.bombCount = bombCount;
        this.freeFlags = bombCount;

        this.board = new Cell[tileHeight][tileWidth];

        for(int row = 0; row < tileHeight; row++) {
            for(int col = 0; col < tileWidth; col++) {
                if( (row + col) % 2 == 0 ) board[row][col] = new Cell(true);
                else board[row][col] = new Cell(false);
            }
        }
    }

    public boolean isGameLocked() { return gameLocked; }
    public boolean getIsWin() { return isWin; }

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

    public void changeFlag(int row, int col) {
        if(!gameStarted) return;

        Cell cell = board[row][col];

        if(!cell.isHidden) return;

        if(cell.hasFlag) {
            cell.hasFlag = false;
            freeFlags++;
        } else {
            if(freeFlags > 0) {
                cell.hasFlag = true;
                freeFlags--;
            }
        }
    }

    public void handleClick(int row, int col) {
        if(gameLocked) return;

        Cell cell = board[row][col];

        if(!gameStarted) { // First Click
            populateBoard(row, col);
            gameStarted = true;
        }

        // Click on existing game
        if(!cell.isHidden) return; // If visible tile ignore
        if(cell.value == -1) { // Ends game if bomb is clicked on
            cell.isHidden = false;
            gameLocked = true;
            return;
        }

        makeMove(row, col);

        if(checkWin()) {
            gameLocked = true;
        }
    }

    private void populateBoard(int firstRow, int firstCol) {
        Random random = new Random();

        // Randomly places bombs away from first click
        for(int i = 0; i < bombCount; i++) {
            int targetRow = random.nextInt(tileHeight);
            int targetCol = random.nextInt(tileWidth);

            if(board[targetRow][targetCol].value == -1 || (targetRow == firstRow && targetCol == firstCol) ||
                    isAdjacentToFirst(firstRow, firstCol, targetRow, targetCol)) i--;
            else { board[targetRow][targetCol].value = -1; }
        }

        // Updates adjacent number counts
        for(int row = 0; row < tileHeight; row++) {
            for(int col = 0; col < tileWidth; col++) {
                if(board[row][col].value == -1) continue;

                board[row][col].value = getAdjacentBombCount(row, col);
            }
        }
    }

    private boolean isAdjacentToFirst(int firstRow, int firstCol, int targetRow, int targetCol) {
        int deltaX = Math.abs(firstRow - targetRow);
        int deltaY = Math.abs(firstCol - targetCol);

        return (deltaX <= 1 && deltaY <= 1);
    }

    private int getAdjacentBombCount(int row, int col) {
        // TODO: Return adjacent bomb count to (row, col)

        int count = 0;

        if(row == 0); // Top elements don't need to be checked

        if(row == (tileHeight - 1)); // Bottom elements don't need to be checked

        if(col == 0); // Left elements don't need to be checked

        if(col == (tileWidth - 1)); // right elements don't need to be checked

        return 0;
    }

    private void makeMove(int row, int col) {
        Cell cell = board[row][col];

        cell.isHidden = false;
        if(cell.value != 0) return;

        floodFill(row, col);
    }

    private void floodFill(int row, int col) {
        // TODO: floodFill on (x, y) using Cell.isHidden for BF Traversal
    }

    private boolean checkWin() {
        for(int row = 0; row < tileHeight; row++) {
            for(int col = 0; col < tileWidth; col++) {
                if(board[row][col].value != -1 && board[row][col].isHidden) return false;
            }
        }

        return true;
    }
}
