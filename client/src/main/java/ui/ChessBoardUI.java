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
        boolean isWhite = false;

        if (teamColor.equalsIgnoreCase("White")) {
            horizOrient = "hgfedcba";
            vertOrient = "12345678";
            isWhite = true;
        } else {
            horizOrient = "abcdefgh";
            vertOrient = "87654321";
            isWhite = false;
        }

        printTiles();
        printCoords(horizOrient, vertOrient);
        printPieces(isWhite);
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

    public void printPieces(boolean isWhite) {
        int startRow = isWhite ? 1 : 8;
        int endRow = isWhite ? 9 : 0;
        int rowIncrement = isWhite ? 1 : -1;

        for (int i = startRow; i != endRow; i += rowIncrement) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
            }
        }



    }

    public ChessPiece getPieceUI(ChessPosition position, ChessBoard board) {
        return board.getPiece(position);
    }

    /*    private ChessPiece[][] copy() {
        ChessPiece[][] copiedBoard = new ChessPiece[9][9];
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (board[i][j] != null) {
                    copiedBoard[i][j] = board[i][j].copy();
                }
            }
        }
        return copiedBoard;
    }
       public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        if (board[row][col] == null) {
            return null;
        } else {
            return board[row][col];
        }
    }

        public String getPieceAbbr(int i, int j) {
        if (board[i][j] != null) {
            if (board[i][j].getTeamColor() == ChessGame.TeamColor.WHITE) {
                return switch (board[i][j].getPieceType()) {
                    case KING -> "K";
                    case QUEEN -> "Q";
                    case BISHOP -> "B";
                    case KNIGHT -> "N";
                    case ROOK -> "R";
                    case PAWN -> "P";
                };
            } else {
                return switch (board[i][j].getPieceType()) {
                    case KING -> "k";
                    case QUEEN -> "q";
                    case BISHOP -> "b";
                    case KNIGHT -> "n";
                    case ROOK -> "r";
                    case PAWN -> "p";
                };
            }
        }
        return null;
    }

    */

}
