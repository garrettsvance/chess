package chess;

import java.util.*;
/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType pieceType;
    private ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return (pieceColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece == null) {
            return Collections.emptyList();
        }

        switch(piece.getPieceType()) {
            case PAWN:
                return (getPawnMoves(board, myPosition, piece.getTeamColor()));
            case ROOK:
                return (getRookMoves(board, myPosition));
            case KNIGHT:
                return (getKnightMoves(board, myPosition));
            case BISHOP:
                return (getBishopMoves(board, myPosition));
            case QUEEN:
                List<ChessMove> queenMoves = new ArrayList<>();
                queenMoves.addAll(getRookMoves(board, myPosition));
                queenMoves.addAll(getBishopMoves(board, myPosition));
                return queenMoves;
            case KING:
                return (getKingMoves(board, myPosition));
        }
        return null;
    }

    /**** Piece Helpers ****/

    // Pawn

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int direction = (color == ChessGame.TeamColor.WHITE) ? -1 : 1;
        int newRow, newCol;

        newRow = startRow + direction;
        newCol = startCol;
        ChessPosition newPosition = new ChessPosition(newRow, newCol);
        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
        if (validCheck(board, newMove)) {
            moves.add(newMove);
        }

        if ((color == ChessGame.TeamColor.WHITE && startRow == 7) || (color == ChessGame.TeamColor.BLACK && startRow == 2)) {
            newRow = startRow + 2 * direction;
            newPosition = new ChessPosition(newRow, newCol);
            newMove = new ChessMove(myPosition, newPosition, null);
            if (validCheck(board, newMove)) {
                moves.add(newMove);
            }
        }

        // Capture

        newRow = startRow + direction;
        newCol = startCol + 1;
        newPosition = new ChessPosition(newRow, newCol);
        newMove = new ChessMove(myPosition, newPosition, null);
        if (validCheck(board, newMove)) {
            moves.add(newMove);
        }

        newRow = startRow + direction;
        newCol = startRow - 1;
        newPosition = new ChessPosition(newRow, newCol);
        newMove = new ChessMove(myPosition, newPosition, null);
        if (validCheck(board, newMove)) {
            moves.add(newMove);
        }

        //TODO: Promotion
        return moves;
    }


    // ROOK
    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int i = 0; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(i, startCol);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (validCheck(board, newMove)) {
                moves.add(newMove);
            }
        }
        for (int j = 0; j < 8; j++) {
            ChessPosition newPosition = new ChessPosition(startRow, j);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (validCheck(board, newMove)) {
                moves.add(newMove);
            }
        }
        return moves;
    }


    // Knight

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] knightMoves = {
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}
        };

        for (int[] move : knightMoves) {
            int newRow = startRow + move[0];
            int newCol = startCol + move[1];
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (validCheck(board, newMove)) {
                moves.add(newMove);
            }
        }
        return moves;
    }


    // Bishop

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Math.abs(startRow - i) == Math.abs(startCol - j)) {
                    ChessPosition newPosition = new ChessPosition(i, j);
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    if (validCheck(board, newMove)) {
                        moves.add(newMove);
                    }
                }
            }
        }
        return moves;
    }


    // King

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] kingMoves = {
                {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] move: kingMoves) {
            int newRow = startRow + move[0];
            int newCol = startCol + move[1];
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (validCheck(board, newMove)) {
                moves.add(newMove);
            }
        }
        return moves;
    }

    /* Checks to see if move is executable within game rules */
    private boolean validCheck(ChessBoard board, ChessMove move) {
        return isValidMove(move) && !isTeam(board, move);
    }

    /* Check if move is within bounds of board */
    static boolean isValidMove(ChessMove move) {
        int row = move.getEndPosition().getRow();
        int col = move.getEndPosition().getColumn();
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }

    /* Check if move is landing on your own team color */
    static boolean isTeam(ChessBoard board, ChessMove move) {
        ChessPiece originalPiece = board.getPiece(move.getStartPosition());
        ChessPiece targetPiece = board.getPiece(move.getEndPosition());
        return targetPiece == null || targetPiece.getTeamColor() != originalPiece.getTeamColor();
    }


}
