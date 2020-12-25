package com.chess.engine.board;

public class BoardUtils {
    public static boolean[] FIRST_COLUMN = null;
    public static boolean[] SECOND_COLUMN = null;
    public static boolean[] SEVENTH_COLUMN = null;
    public static boolean[] EIGHTH_COLUMN = null;


    private BoardUtils() {
        throw new RuntimeException("You cannot instantiate me!");
    }

    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }
}
