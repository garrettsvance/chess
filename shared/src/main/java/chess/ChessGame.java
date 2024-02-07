package chess;

import java.util.Collection;
import java.util.Objects;

import static chess.ChessPiece.*;
import static chess.ChessPiece.PieceType.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard gameBoard;


    public ChessGame() {
        gameBoard = new ChessBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece playerPiece = gameBoard.getPiece(startPosition);
        if (playerPiece != null) {
            return playerPiece.pieceMoves(this.getBoard(), startPosition);
        } else {
            return null;
        }
    }

    public void validMovesPrint(Collection<ChessMove> validMoves) {
        System.out.println("Available Moves:");
        for (ChessMove move : validMoves) {
            ChessPosition startPosition = move.getStartPosition();
            ChessPosition endPosition = move.getEndPosition();
            int startRow = startPosition.getRow();
            int startCol = startPosition.getColumn();
            int endRow = endPosition.getRow();
            int endCol = endPosition.getColumn();
            String piece = String.valueOf(gameBoard.getPiece(startPosition).getPieceType());
            String color = String.valueOf(gameBoard.getPiece(startPosition).getTeamColor());
            System.out.println(color + " " + piece + " moving from " + startRow + "," + startCol + " to " + endRow + "," + endCol);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var startPosition = move.getStartPosition();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        int endCol = move.getEndPosition().getColumn();
        var endRow = move.getEndPosition().getRow();
        ChessPiece promotionPiece;
        ChessPiece playerPiece = gameBoard.getPiece(startPosition);
        String piece = String.valueOf(gameBoard.getPiece(startPosition).getPieceType());
        TeamColor pieceColor = playerPiece.getTeamColor();
        teamTurn = getTeamTurn();
        Collection<ChessMove> validMoves = validMoves(startPosition);
        System.out.println("Requested Move:");
        System.out.println(pieceColor + " " + piece + " moving from " + startRow + "," + startCol + " to " + endRow + "," + endCol);
        validMovesPrint(validMoves);
        if (gameBoard.getPiece(startPosition) == null) {
            throw new InvalidMoveException("Selected space is empty on chessboard");
        } else if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        } else if (!isValidMove(move)) {
            throw new InvalidMoveException("Move is out of bounds");
        } else if (pieceColor != getTeamTurn()) {
            throw new InvalidMoveException("Wrong team selected");
        } else if (checkFoe(pieceColor, gameBoard)) {
            if (willRemoveCheck(move)) {
                gameBoard.movePiece(move, playerPiece);
            } else if (isInCheck(pieceColor)) {
                throw new InvalidMoveException("King in Check Exception");
            }
        } else if ((playerPiece.getPieceType() == ChessPiece.PieceType.PAWN) && (promotionCheck(endRow, pieceColor))) {
            promotionPiece = new ChessPiece(getTeamTurn(), move.getPromotionPiece());
            gameBoard.movePiece(move, promotionPiece);
        } else {
            gameBoard.movePiece(move, playerPiece);

        }
        setTeamTurn((getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);
    }

    private boolean willRemoveCheck(ChessMove move) {
        ChessBoard dummyBoard = new ChessBoard(gameBoard);
        TeamColor yourTeam = dummyBoard.getPiece(move.getStartPosition()).getTeamColor();
        ChessPiece playerPiece = dummyBoard.getPiece(move.getStartPosition());
        dummyBoard.movePiece(move, playerPiece);
        System.out.println("Current Dummy Board: ");
        dummyBoard.printBoard();
        return !checkFoe(yourTeam, dummyBoard);
    }

    private boolean promotionCheck(int row, TeamColor pieceColor) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 8) || (pieceColor == ChessGame.TeamColor.BLACK && row == 1);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamTurn which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamTurn) {
        System.out.println("IsInCheck Board");
        gameBoard.printBoard();
        return checkFoe(teamTurn, gameBoard);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamTurn which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamTurn) {
        ChessBoard dummyBoard = new ChessBoard(gameBoard);
        return checkFoe(teamTurn, dummyBoard);
    }

    private boolean checkFoe(TeamColor teamTurn, ChessBoard testingBoard) {

        testingBoard.printBoard();
        ChessPosition kingPosition = kingPosition(teamTurn, testingBoard);
        if (kingPosition == null) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition foePosition = new ChessPosition(i, j);
                if (testingBoard.getPiece(foePosition) != null && testingBoard.getPiece(foePosition).getTeamColor() != teamTurn) {
                    for (ChessMove checkMove : testingBoard.getPiece(foePosition).pieceMoves(testingBoard, foePosition)) {
                        if (checkMove.getEndPosition().equals(kingPosition)) {
                            System.out.println(teamTurn + " king in Check");
                            return true;
                        }
                    }
                }
            }
        }
        System.out.println(teamTurn + " king not in check");
        return false;
    }


    private ChessPosition kingPosition(TeamColor teamTurn, ChessBoard testingBoard) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                if (((testingBoard.getPiece(position) != null) && testingBoard.getPiece(position).getPieceType() == KING) && (testingBoard.getPiece(position).getTeamColor() == teamTurn)) {
                    return position;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamTurn which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamTurn) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece checkPiece = gameBoard.getPiece(position);
                if ((checkPiece != null) && (checkPiece.getTeamColor() == teamTurn)) {
                    for (ChessMove pieceToCheck : validMoves(position)) {
                        ChessBoard dummyBoard = new ChessBoard(gameBoard);
                        dummyBoard.movePiece(pieceToCheck, checkPiece);
                        if (checkFoe(teamTurn, dummyBoard)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

}
