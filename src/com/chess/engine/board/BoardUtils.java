package com.chess.engine.board;


public class BoardUtils {
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;
    public static boolean[] FIRST_COLUMN = initCol(0);
    public static boolean[] SECOND_COLUMN = initCol(1);
    public static boolean[] SEVENTH_COLUMN = initCol(6);
    public static boolean[] EIGHTH_COLUMN = initCol(7);


    public static final boolean[] EIGHTH_RANK = initRow(0);
    public static final boolean[] SEVENTH_RANK = initRow(8);
    public static final boolean[] SIXTH_RANK = initRow(16);
    public static final boolean[] FIFTH_RANK = initRow(24);
    public static final boolean[] FOURTH_RANK = initRow(32);
    public static final boolean[] THIRD_RANK = initRow(40);
    public static final boolean[] SECOND_RANK = initRow(48);
    public static final boolean[] FIRST_RANK = initRow(56);

    private BoardUtils() {
        throw new RuntimeException("You cannot instantiate me!");
    }

    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }

    // make entire col true
    private static boolean[] initCol(int colNum) {
        final boolean[] column = new boolean[NUM_TILES];
        do {
            column[colNum] = true;
            colNum += NUM_TILES_PER_ROW;
        } while (colNum < NUM_TILES);
        return column;
    }

    // make entire row true
    // eg row 2 (tile coordinate 8) means arr[8] to arr[15] is true
    private static boolean[] initRow(int tileCoordinateForStartOfRow) {
        final boolean[] row = new boolean[NUM_TILES];
        do {
            row[tileCoordinateForStartOfRow] = true;
            tileCoordinateForStartOfRow++;
        } while (tileCoordinateForStartOfRow % NUM_TILES_PER_ROW != 0);
        return row;
    }
}
