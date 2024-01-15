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

        // Promotion

        if ((color == ChessGame.TeamColor.WHITE && start))
    }
}
