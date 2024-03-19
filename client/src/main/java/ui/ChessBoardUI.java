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
        board.resetBoard();
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

        // Define the padding characters
        String padding = "\u2001"; // Narrow space
        String emptyTilePadding = padding + padding + padding;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    if ((i == 0 || i == 9) && (j == 0 || j == 9)) {
                        // Corner tiles
                        out.print(emptyTilePadding);
                    } else if (i == 0 || i == 9) {
                        // Horizontal coordinate tiles
                        out.print(padding + horizOrient.charAt(j - 1) + padding);
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
                        // Adjusting the spacing for tiles with pieces to match empty tiles and horizOrient spacing
                        out.print(padding + pieceChar + padding);
                    } else {
                        // Empty tile
                        out.print(emptyTilePadding);
                    }
                }
            }
            out.println();
        }
    }

    public void printTiles() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }
                out.print("   "); // Three spaces for each tile
            }
            out.println();
        }
    }

    public void printCoords(String horizontal, String vertical) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    if ((i == 0 || i == 9) && (j == 0 || j == 9)) {
                        out.print("   ");
                    } else if (i == 0 || i == 9) {
                        out.print(" " + horizontal.charAt(j - 1) + " ");
                    } else {
                        out.print(" " + vertical.charAt(8 - i) + " ");
                    }
                } else {
                    if ((i + j) % 2 == 0) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        out.print(SET_BG_COLOR_BLACK);
                    }
                    out.print("   ");
                }
            }
            out.println();
        }
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
                    //System.out.println(piece.getPieceType());
                    String pieceChar = getPieceChar(piece);
                    out.print(pieceChar);
                } else {
                    //System.out.println("Null piece");
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
