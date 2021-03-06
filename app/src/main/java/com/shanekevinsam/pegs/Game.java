package com.shanekevinsam.pegs;

import android.util.Log;

import java.util.Random;

/**
 * Controls logic for peg game
 *
 * Created by me on 4/6/16.
 */
public class Game {
    private final String TAG = "Game";
    private Board board = null;

    /**
     * Instantiates game board with random peg removed
     */
    public Game(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5-x);
        board = new Board(new Coordinate(x,y));
    }

    /**
     * Instantiates game board with select peg removed
     *
     * @param coord Initially empty space
     */
    public Game(Coordinate coord){
        board = new Board(coord);
    }

    public Game(boolean[][] boardArray){
        try{
            board = new Board(boardArray);
        } catch (IllegalArgumentException e){
            Log.d(TAG, "Bad array passed to Game()", e);
            Random rand = new Random();
            int x = rand.nextInt(5);
            int y = rand.nextInt(5-x);
            board = new Board(new Coordinate(x,y));
        }
    }

    /**
     * @return 2d boolean array representing game state
     */
    public boolean[][] getBoard(){
        return board.getBoard();
    }

    /**
     * Checks if move is correct and performs changes to board
     *
     * @param start Coordinate of starting peg
     * @param end   Coordinate of ending place
     * @return true if move successful, false otherwise
     */
    public boolean move(Coordinate start, Coordinate end){
        if(validateMove(start, end)){
            Coordinate midCoord = calcPegBetween(start, end);
            board.removePeg(start);
            board.removePeg(midCoord);
            board.addPeg(end);
            return true;
        } else{
            return false;
        }
    }

    /**
     * Checks if a move is valid.
     * Valid if peg at start, no peg at end, correct distance and peg between
     *
     * @param start Position at start of move
     * @param end Position at end of move
     * @return true if valid, false otherwise
     */
    private boolean validateMove(Coordinate start, Coordinate end) {
        Log.d(TAG, "Validating move from " + start + " to " + end);
        try {
            return (board.checkPeg(start)
                    && !board.checkPeg(end)
                    && checkCorrectDistance(start, end)
                    && checkPegBetween(start, end));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Start or End out of bounds", e);
            return false;
        }
    }

    /**
     * Checks if start coordinates and end coordinate are a peg apart.
     * If they are on the same row or column in the board, they should be 2 away
     * If they aren't they have to be diagonal, where somehow you get 4 from the math.
     *
     * @param start Coordinate of first space
     * @param end   Coordinate of second space
     * @return  Whether or not the 2 coordinates have a single space between them
     */
    private boolean checkCorrectDistance( Coordinate start, Coordinate end){
        if (start.equals(end)){
            return false;
        } else if(start.getX() == end.getX() || start.getY() == end.getY()){
            return Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY()) == 2;
        } else {
            return Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY()) == 4
                    && Math.abs(start.getX() + start.getY()) - Math.abs(end.getX() + end.getY()) == 0;
        }
    }

    /**
     * Checks if peg is between 2 coords.
     * Assumes correctDistance is true!!!
     *
     * @param start Coordinate of first space
     * @param end   Coordinate of second space
     * @return True if peg present, false otherwise
     */
    private boolean checkPegBetween(Coordinate start, Coordinate end){
        return board.checkPeg(calcPegBetween(start, end));
    }

    /**
     * Calculates the peg between 2 coordinates
     * Assumes correctDistance is true!!!
     *
     * @param start Coordinate of first space
     * @param end   Coordinate of second space
     * @return {x, y}
     */
    private Coordinate calcPegBetween(Coordinate start, Coordinate end) {
        int x = Math.abs(start.getX() + end.getX()) / 2;
        int y = Math.abs(start.getY() + end.getY()) / 2;
        Coordinate coord = new Coordinate(x, y);
        Log.d(TAG, "MID(" + start + " " + end + ") = " + coord);
        return coord;
    }

    /**
     * Checks if a move on the board is possible.
     *
     * @return true if there are remaining moves, false otherwise
     */
    public boolean checkForRemainingMoves(){
        for (int y = 0; y <= 4; ++y) {
            for (int x = 0; x <= 4 - y; ++x) {
                if (checkValidMovesFromCoord(new Coordinate(x, y))){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * If the board is broken into a bottom left, bottom right, and top,
     *  only 2 directions have to be checked at a time
     * If bottom left:  Check right and up to right
     * If bottom right: Check left and up to left
     * If top:          check bottom left and bottom right
     *
     * Note: Center pieces overlap 2 cases
     *
     * @param coord Coordinate to check
     * @return if there is a valid move from coordinate
     */
    private boolean checkValidMovesFromCoord(Coordinate coord){
        Log.d(TAG, "Checking valid moves from " + coord);
        int x = coord.getX();
        int y = coord.getY();
        if (x + y <= 2) {
            if (validateMove(coord, new Coordinate(x + 2, y)) || validateMove(coord, new Coordinate(x, y + 2))) {
                return true;
            }
        }
        if (x >= 2) {
            if (validateMove(coord, new Coordinate(x - 2, y)) || validateMove(coord, new Coordinate(x - 2, y + 2))) {
                return true;
            }
        }
        if (y >= 2) {
            if(validateMove(coord, new Coordinate(x, y-2)) || validateMove(coord, new Coordinate(x+2, y-2))){
                return true;
            }
        }
        return false;
    }

    public int getNumPegsLeft(){
        return board.getNumPegs();
    }

    public boolean isPegAt(Coordinate coord){
        return board.checkPeg(coord);
    }
}
