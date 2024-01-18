package chess;

import java.util.*;
/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType pieceType;
    private final ChessGame.TeamColor pieceColor;

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
        return pieceColor;
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

        if (piece.getPieceType() == null) {
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

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? -1 : 1;

        ChessPosition newPosition = new ChessPosition(startRow + direction, startCol);
        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
        // Check for promotion/get moves
        if (promotionCheck(startRow) && isValidMove(newMove) && isOccupied(board, newMove).equals("empty")) {
            moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
        } else if (firstMove(startRow)) {
            for (int i = 1; i <= 2; i++) {
                newPosition = new ChessPosition(startRow + direction * i, startCol);
                newMove = new ChessMove(myPosition, newPosition, null);
                if (isValidMove(newMove) && isOccupied(board, newMove).equals("empty")) {
                    moves.add(newMove);
                }
            }
        } else {
            newPosition = new ChessPosition(startRow + direction, startCol);
            newMove = new ChessMove(myPosition, newPosition, null);
            if (isValidMove(newMove) && isOccupied(board, newMove).equals("empty")) {
                moves.add(newMove);
            }
        }

        // Check for diagonal movement
        int[][] pawnAttack = {
                {1, 1}, {1, -1}
        };

        for (int[] attack: pawnAttack) {

        }

        return moves;
    }

    private boolean promotionCheck(int row) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 7) || (pieceColor == ChessGame.TeamColor.BLACK && row == 2);
    }

    private boolean firstMove(int row) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 2) || (pieceColor == ChessGame.TeamColor.BLACK && row == 7);
    }


    // ROOK
    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int i = 0; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(i, startCol);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (isValidMove(newMove)) {
                if (isOccupied(board, newMove).equals("empty")) {
                    moves.add(newMove);
                } else if (isOccupied(board, newMove).equals("enemy")) {
                    moves.add(newMove);
                    break;
                } else {
                    break;
                }
            }
        }
        for (int j = 0; j < 8; j++) {
            ChessPosition newPosition = new ChessPosition(startRow, j);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (isValidMove(newMove)) {
                if (isOccupied(board, newMove).equals("empty")) {
                    moves.add(newMove);
                } else if (isOccupied(board, newMove).equals("enemy")) {
                    moves.add(newMove);
                    break;
                } else {
                    break;
                }
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
            if (isValidMove(newMove)) {
                if (isOccupied(board, newMove).equals("empty")) {
                    moves.add(newMove);
                } else if (isOccupied(board, newMove).equals("enemy")) {
                    moves.add(newMove);
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }


    // Bishop

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] bishopMoves = {
                {1, 1}, {-1, 1}, {-1, -1}, {1, -1}
        };

        for (int[] move: bishopMoves) {
            for (int i = 1; i < 8; i++) {
                int newRow = startRow + i * move[0];
                int newCol = startCol + i * move[1];
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                if (isValidMove(newMove)) {
                    if (isOccupied(board, newMove).equals("empty")) {
                        moves.add(newMove);
                    } else if (isOccupied(board, newMove).equals("enemy")) {
                        moves.add(newMove);
                        break;
                    } else {
                        break;
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
            if (isValidMove(newMove)) {
                if (isOccupied(board, newMove).equals("empty")) {
                    moves.add(newMove);
                } else if (isOccupied(board, newMove).equals("enemy")) {
                    moves.add(newMove);
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }


    /* Check if move is within bounds of board */
    static boolean isValidMove(ChessMove move) {
        int row = move.getEndPosition().getRow();
        int col = move.getEndPosition().getColumn();
        return (row > 0 && row <= 8) && (col > 0 && col <= 8);
    }

    /* Check if move is landing on an occupied space */
    String isOccupied(ChessBoard board, ChessMove move) {
        ChessPiece originalPiece = board.getPiece(move.getStartPosition());
        ChessPiece targetSpace = board.getPiece(move.getEndPosition());
        if (targetSpace == null) {
            return "empty";
        } else if (originalPiece.getTeamColor() == targetSpace.getTeamColor()) {
            return "teammate";
        } else {
            return "enemy";
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, pieceColor);
    }
}
