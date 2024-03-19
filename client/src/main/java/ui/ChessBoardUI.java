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
        String padding = "\u2005";
        boolean isWhite = false;

        if (teamColor.equalsIgnoreCase("White")) {
            horizOrient = "hgfedcba";
            vertOrient = "12345678";
            isWhite = true;
        } else {
            horizOrient = "abcdefgh";
            vertOrient = "87654321";
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    if ((i == 0 || i == 9) && (j == 0 || j == 9)) {
                        // Corner tiles
                        out.print(padding + "\u2001\u2005\u200A" + "\u200A"); // swap last val for padding on laptop
                    } else if (i == 0 || i == 9) {
                        // Horizontal coordinate tiles

                        // Desktop Padding
                        out.print("\u200A" + horizOrient.charAt(j - 1) + "\u2005" + "\u200A" + "\u200A" + "\u200A");

                        // Laptop Padding
                        //ut.print("\u200A" + horizOrient.charAt(j - 1) + "\u200A" + "\u2001");

                    } else {
                        // Vertical coordinate tiles
                        out.print(padding + vertOrient.charAt(8 - i) + padding);
                    }
                } else {
                    if ((i + j) % 2 == 0) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        out.print(SET_BG_COLOR_BLACK);
                    }
                    ChessPosition position = new ChessPosition(isWhite ? 9 - i : i, j);
                    ChessPiece piece = getPiece(position, board);
                    if (piece != null) {
                        String pieceChar = getPieceChar(piece);
                        out.print(padding + pieceChar + padding);
                    } else {
                        // Empty tile
                        out.print(padding + "\u2001\u2005\u200A" + padding);
                    }
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
