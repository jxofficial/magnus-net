package com.chess.engine.board;


public class BoardUtils {
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;
    public static boolean[] FIRST_COLUMN = initCol(0);
    public static boolean[] SECOND_COLUMN = initCol(1);
    public static boolean[] SEVENTH_COLUMN = initCol(6);
    public static boolean[] EIGHTH_COLUMN = initCol(7);
    public static final boolean[] SECOND_ROW = null;
    public static final boolean[] SEVENTH_ROW = null;

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
}
