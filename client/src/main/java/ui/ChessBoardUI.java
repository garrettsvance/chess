package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class ChessBoardUI {

    private final PrintStream out;

    public ChessBoardUI() {
        this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }


    public void printBoard(ChessGame game, boolean showMoves, Collection<ChessMove> legalMoves) {
        //ChessBoard board = game.getBoard();
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        String teamColor = String.valueOf(game.getTeamTurn());

        String whiteHorizontal = "abcdefgh";
        String whiteVertical = "12345678";
        String blackHorizontal = "hgfedcba";
        String blackVertical = "87654321";

        if (teamColor.equalsIgnoreCase("White")) {
            printBoardHelper(whiteHorizontal, whiteVertical, true, board, showMoves, legalMoves);
            System.out.println();
        } else {
            printBoardHelper(blackHorizontal, blackVertical, false, board, showMoves, legalMoves);
            System.out.println();
        }
    }

    public void printBoardHelper(String horizontal, String vertical, boolean isWhite, ChessBoard board, boolean showMoves, Collection<ChessMove> legalMoves) {
        String padding = "\u2005";
        boolean isLegalMove;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    if ((i == 0 || i == 9) && (j == 0 || j == 9)) { // Corner tiles
                        out.print(padding + "\u2001\u2005\u200A" + "\u200A"); // swap last val for "padding" on laptop
                    } else if (i == 0 || i == 9) {                         // Horizontal coordinate tiles
                        // Desktop Padding
                        out.print("\u200A" + horizontal.charAt(j - 1) + "\u2005" + "\u200A" + "\u200A" + "\u200A");

                        // Laptop Padding:
                        //ut.print("\u200A" + horizOrient.charAt(j - 1) + "\u200A" + "\u2001");
                    } else { // Vertical coordinate tiles
                        out.print(padding + vertical.charAt(8 - i) + padding);
                    }
                } else {
                    if ((i + j) % 2 == 0) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        out.print(SET_BG_COLOR_BLACK);
                    }
                    ChessPosition position = new ChessPosition(isWhite ? i : 9 - i, j);
                    ChessPiece piece = getPiece(position, board);
                    if (piece != null) {
                        String pieceChar = getPieceChar(piece);
                        if (showMoves) {
                            isLegalMove = false;
                            for (ChessMove move : legalMoves) {
                                if (move.getStartPosition().equals(position)) {
                                    isLegalMove = true;
                                    break;
                                }
                            }
                            if (isLegalMove) {
                                out.print(SET_BG_COLOR_GREEN);
                            }
                        }
                        out.print(padding + pieceChar + padding);
                    } else { // Empty tile
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
