package com.chess.engine.board;


public class BoardUtils {
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;
    public static boolean[] FIRST_COLUMN = initCol(0);
    public static boolean[] SECOND_COLUMN = initCol(1);
    public static boolean[] SEVENTH_COLUMN = initCol(6);
    public static boolean[] EIGHTH_COLUMN = initCol(7);
    public static final boolean[] SECOND_ROW = initRow(8);
    public static final boolean[] SEVENTH_ROW = initRow(48);

    private BoardUtils() {
        throw new RuntimeException("You cannot instantiate me!");
    }

    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }

    private static boolean[] initCol(int colNum) {
        final boolean[] column = new boolean[NUM_TILES];
        do {
            column[colNum] = true;
            colNum += NUM_TILES_PER_ROW;
        } while (colNum < NUM_TILES);
        return column;
    }

    // make entire row true
    // eg row 2 (tile coordinate 8) means arr[8] - arr[15] is true
    private static boolean[] initRow(int tileCoordinateForStartOfRow) {
        final boolean[] row = new boolean[NUM_TILES];
        do {
            row[tileCoordinateForStartOfRow] = true;
            tileCoordinateForStartOfRow++;
        } while (tileCoordinateForStartOfRow % NUM_TILES_PER_ROW != 0);
        return row;
    }
}
