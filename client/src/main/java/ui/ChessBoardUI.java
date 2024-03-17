package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoardUI {

    private final PrintStream out;

    public ChessBoardUI() {
        this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }


    public void printBoard(ChessGame game) {
        ChessBoard board = game.getBoard();
        String teamColor = String.valueOf(game.getTeamTurn());
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
        }

        printTiles();
        printCoords(horizOrient, vertOrient);
        printPieces(isWhite, board);
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

    public void printPieces(boolean isWhite, ChessBoard board) {
        int startRow = isWhite ? 1 : 8;
        int endRow = isWhite ? 9 : 0;
        int rowIncrement = isWhite ? 1 : -1;

        for (int i = startRow; i != endRow; i += rowIncrement) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = getPiece(position, board);
                if (piece != null) {
                    String pieceChar = getPieceChar(piece);
                    out.print(pieceChar + " ");
                } else {
                    out.print(EMPTY);
                }
            }
            out.println();
        }
    }

    public ChessPiece getPiece(ChessPosition position, ChessBoard board) {
        return board.getPiece(position);
    }

    public String getPieceChar(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        }
    }

}
