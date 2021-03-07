package com.chess.engine.board;


import java.util.*;

public class BoardUtils {
    public static final int START_TILE_INDEX = 0;

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

    // can only be accessed within this class,
    // used in public static fns getCoordinateFromPGN and getPGNFromCoordinate
    private static final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    private static final Map<String, Integer> PGN_COORDINATE_MAP = initializePGNToCoordinateMap();

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

    public static int getCoordinateFromPGN(final String notation) {
        return PGN_COORDINATE_MAP.get(notation);
    }


    public static String getPGNFromCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }


    // reason we use map is to map pgn string to coordinate
    private static Map<String, Integer> initializePGNToCoordinateMap() {
        final Map<String, Integer> pgnToCoordinateMap = new HashMap<>();
        for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            pgnToCoordinateMap.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return Collections.unmodifiableMap(pgnToCoordinateMap);
    }



    private static List<String> initializeAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }
}
