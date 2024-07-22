public class Cell {
    public boolean isHidden = true;
    public boolean isLight;

    public boolean hasFlag = false;

    public int value = 0; // -1 Represents Bomb

    public Cell(boolean isLight) {
        this.isLight = isLight;
    }

    // TODO: Use MatteBorder to make proper border with #86AE3B (Border) (134 174 59)
    // https://stackoverflow.com/questions/2174319/is-it-possible-to-have-a-java-swing-border-only-on-the-top-side
    public TileType getTileType() {
        if(isHidden) { // Hidden tile
            if(isLight) { return TileType.lightHidden; }
            else { return TileType.darkHidden; }
        } else { // Visible Tile
            if(isLight) { return TileType.lightVisible; }
            else { return TileType.darkVisible; }
        }
    }

    public TileContent getTileContent() {
        if(isHidden) { // Hidden tile
            if(hasFlag) { return TileContent.FLAG; }
            else { return TileContent.EMPTY; }
        } else { // Visible Tile
            switch(value) {
                case 0 -> { return TileContent.EMPTY; }
                case 1 -> { return TileContent.ONE; }
                case 2 -> { return TileContent.TWO; }
                case 3 -> { return TileContent.THREE; }
                case 4 -> { return TileContent.FOUR; }
                case 5 -> { return TileContent.FIVE; }
                case 6 -> { return TileContent.SIX; }
                case 7 -> { return TileContent.SEVEN; }
                case 8 -> { return TileContent.EIGHT; }
                default -> { return TileContent.BOMB; }
            }
        }
    }
}