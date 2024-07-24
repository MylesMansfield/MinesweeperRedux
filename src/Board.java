import java.util.LinkedList;
import java.util.Random;
import java.util.Queue;

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
                if( (row + col) % 2 == 0 ) board[row][col] = new Cell(true, row, col);
                else board[row][col] = new Cell(false, row, col);
            }
        }
    }

    public boolean isGameLocked() { return gameLocked; }
    public boolean isWin() { return isWin; }

    public int getFreeFlags() { return freeFlags; }

    // TODO Future: have this work done on main thread after making a move and feed to threadsafe buffer
    //       all current calls are done in EDT where they have to do this collation. (It's fine as is but
    //       EDT shouldn't be doing this)
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
        if(cell.hasFlag) return; // If tile has flag ignore
        if(cell.value == -1) { // Ends game if bomb is clicked on
            cell.isHidden = false;
            gameLocked = true;
            return;
        }

        makeMove(row, col);

        if(checkWin()) {
            isWin = true;
            gameLocked = true;

            // Turns all tiles that are visible into water
            for( row = 0; row < tileHeight; row++) {
                for( col = 0; col < tileWidth; col++) {
                    if(!board[row][col].isHidden) board[row][col].isWater = true;
                    board[row][col].hasFlag = false;
                    board[row][col].value = 0;
                }
            }
        }

        // FOR threadsafe buffer move getTileBoard logic here and replace old logic with returning buffer
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
        int count = 0;
        boolean checkTop = true, checkBottom = true, checkLeft = true, checkRight = true;

        if(row == 0) checkTop = false;
        if(row == (tileHeight - 1)) checkBottom = false;
        if(col == 0) checkLeft = false;
        if(col == (tileWidth - 1)) checkRight = false;

        if(checkTop) {
            if(board[row - 1][col].value == -1) count++;
            if(checkLeft && board[row - 1][col - 1].value == -1) count++;
            if(checkRight && board[row - 1][col + 1].value == -1) count++;
        }

        if(checkBottom) {
            if(board[row + 1][col].value == -1) count++;
            if(checkLeft && board[row + 1][col - 1].value == -1) count++;
            if(checkRight && board[row + 1][col + 1].value == -1) count++;
        }

        if(checkLeft && board[row][col - 1].value == -1) count++;
        if(checkRight && board[row][col + 1].value == -1) count++;

        return count;
    }

    private void makeMove(int row, int col) {
        Cell cell = board[row][col];

        if(cell.value != 0) {
            cell.isHidden = false;
            return;
        }

        floodFill(cell);
    }

    private void floodFill(Cell cell) {
        Queue<Cell> queue = new LinkedList<>();
        queue.offer(cell);

        Cell current;
        while(queue.peek() != null) {
            current = queue.poll();
            current.isHidden = false;

            if(current.value != 0) continue;

            // Check existing neighbors, if they aren't visible add to queue
            boolean checkTop = true, checkBottom = true, checkLeft = true, checkRight = true;

            if(current.row == 0) checkTop = false;
            if(current.row == (tileHeight - 1)) checkBottom = false;
            if(current.col == 0) checkLeft = false;
            if(current.col == (tileWidth - 1)) checkRight = false;

            if(checkTop) {
                if(board[current.row - 1][current.col].isHidden) queue.offer(board[current.row - 1][current.col]);
                if(checkLeft && board[current.row - 1][current.col - 1].isHidden) queue.offer(board[current.row - 1][current.col - 1]);
                if(checkRight && board[current.row - 1][current.col + 1].isHidden) queue.offer(board[current.row - 1][current.col + 1]);
            }

            if(checkBottom) {
                if(board[current.row + 1][current.col].isHidden) queue.offer(board[current.row + 1][current.col]);
                if(checkLeft && board[current.row + 1][current.col - 1].isHidden) queue.offer(board[current.row + 1][current.col - 1]);
                if(checkRight && board[current.row + 1][current.col + 1].isHidden) queue.offer(board[current.row + 1][current.col + 1]);
            }

            if(checkLeft && board[current.row][current.col - 1].isHidden) queue.offer(board[current.row][current.col - 1]);
            if(checkRight && board[current.row][current.col + 1].isHidden) queue.offer(board[current.row][current.col + 1]);
        }
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
