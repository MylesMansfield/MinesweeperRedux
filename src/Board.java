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
            populateBoard();
            gameStarted = true;
        }

        // Click on existing game
        if(!cell.isHidden) return;

        // TODO: make move on board
        cell.isHidden = false;


        if(checkEnd()) {
            gameLocked = true;
        }
    }

    private void populateBoard() {
        // TODO Populate board with all bombs randomly except click coords
    }

    // FloodFill can use Cell.isHidden as seen variable for BFS(more like traversal)

    private boolean checkEnd() {
        // TODO
        return false;
    }
}
