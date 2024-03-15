package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;

import static ui.EscapeSequences.*;

public class ChessBoardUI {

    private PrintStream out;

    public ChessBoardUI() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
    }

    public void printBoard(String teamColor) {
        String horizOrient;
        String vertOrient;

        if (teamColor.equalsIgnoreCase("White")) {
            horizOrient = "hgfedcba";
            vertOrient = "12345678";
        } else {
            horizOrient = "abcdefgh";
            vertOrient = "87654321";
        }

        printTiles();
        printCoords(horizOrient, vertOrient);
        printPieces();
    }

    public void printTiles() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    out.print(SET_BG_COLOR_WHITE);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }
                out.print("   "); // Three spaces for each tile
            }
        }
    }

    public void printCoords(String horizontal, String vertical) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print("   ");
        for (char c : horizontal.toCharArray()) {
            out.print(" " + c + " ");
        }
        out.println("   ");

        for (int i = 0; i < 8; i++) {
            out.print(SET_BG_COLOR_DARK_GREY);
            out.print(" " + vertical.charAt(i) + " ");
            for (int j = 0; j < 8; j++) {
                out.print("   ");
            }
            out.println(SET_BG_COLOR_DARK_GREY + " " + vertical.charAt(i) + " ");
        }

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print("   ");
        for (char c : horizontal.toCharArray()) {
            out.print(" " + c + " ");
        }
        out.println("   ");
    }

    public void printPieces() {

    }


}
