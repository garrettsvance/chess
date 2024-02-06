package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static chess.ChessPiece.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard gameBoard;
    ChessBoard tempBoard;
    ChessBoard checkBoard;

    boolean checkBoardCheck = false;
    public ChessGame() {
        gameBoard = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        tempBoard = new ChessBoard(gameBoard);
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
        tempBoard = new ChessBoard(gameBoard);
        teamTurn = getTeamTurn();
        //System.out.println("Making move for the " + teamTurn + " team.");
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
        } else if /*(isInCheck(getTeamTurn()))*/ { // check for whether your piece is landing on another
            throw new InvalidMoveException("King in Check");
        }
        if ((playerPiece.getPieceType() == ChessPiece.PieceType.PAWN) && (promotionCheck(endRow, pieceColor))) {
                //System.out.println("Pawn is in play");
                promotionPiece = new ChessPiece(getTeamTurn(), move.getPromotionPiece());
                gameBoard.movePiece(move, promotionPiece);
        } else {
            gameBoard.movePiece(move, playerPiece);
            String resultBoard = toString();
            System.out.println(resultBoard);
        }
        setTeamTurn((getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);
    }

    public boolean eliminatesEnemy(ChessMove move) {
        TeamColor yourTeam = gameBoard.getPiece(move.getStartPosition()).getTeamColor();
        ChessPiece playerPiece = gameBoard.getPiece(move.getStartPosition());
        if (gameBoard.getPiece(move.getEndPosition()).getTeamColor() != yourTeam) {
            System.out.println("Executing move on temp board (hopefully)");
            tempBoard.movePiece(move, playerPiece);
            if (isInCheck(yourTeam)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean promotionCheck(int row, TeamColor pieceColor) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 8) || (pieceColor == ChessGame.TeamColor.BLACK && row == 1);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamTurn which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamTurn) {
        ChessPosition kingPosition = kingPosition(teamTurn);
        if (kingPosition == null) {
            return false;
        }
        //System.out.println(teamTurn + " king found at: " + kingPosition.getRow() + ", " + kingPosition.getColumn());
        tempBoard = checkBoardCheck ? checkBoard : new ChessBoard(gameBoard); //TODO: check for copy. is this copying fresh board, or current?
        return checkFoe(teamTurn, kingPosition);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamTurn which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamTurn) {
        ChessPosition kingPosition = kingPosition(teamTurn);
        if (kingPosition == null) {
            return false;
        }
        //System.out.println(teamTurn + " king found at: " + kingPosition.getRow() + ", " + kingPosition.getColumn());
        tempBoard = new ChessBoard(gameBoard);
        return checkFoe(teamTurn, kingPosition);
    }

    private boolean checkFoe(TeamColor teamTurn, ChessPosition kingPosition) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition foePosition = new ChessPosition(i, j);
                if (tempBoard.getPiece(foePosition) != null && tempBoard.getPiece(foePosition).getTeamColor() != teamTurn) {
                    for (ChessMove checkMove : tempBoard.getPiece(foePosition).pieceMoves(tempBoard, foePosition)) {
                        if (checkMove.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    /*public boolean isInCheckmate(TeamColor teamTurn)  { // create an integrated for loop for each king move, and then call "is in check" for each one
        ChessPosition kingPosition = kingPosition(teamTurn);
        //assert kingPosition != null;
        ChessPiece kingPiece = gameBoard.getPiece(kingPosition);
        ChessBoard checkBoard = new ChessBoard(gameBoard);
        checkBoardCheck = true;
        for (ChessMove kingMoves : tempBoard.getPiece(kingPosition).pieceMoves(tempBoard, kingPosition)) {
            var tempRow = kingMoves.getEndPosition().getRow();
            var tempCol = kingMoves.getEndPosition().getColumn();
            ChessPosition newPosition = new ChessPosition(tempRow, tempCol);
            ChessMove tempKingMove = new ChessMove(kingPosition, newPosition, null);
            checkBoard.movePiece(tempKingMove, kingPiece);
            if (isInCheck(teamTurn)) {
                return true;
            }
        }
        checkBoardCheck = false;
        return false;
    }*/


    private ChessPosition kingPosition(TeamColor teamTurn) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j); // switching below row from tempboard to gameboard
                if (((gameBoard.getPiece(position) != null) && gameBoard.getPiece(position).getPieceType() == ChessPiece.PieceType.KING) && (gameBoard.getPiece(position).getTeamColor() == teamTurn)) {
                    return position;
                }
            }
        }
        //System.out.println(teamTurn + " king not found");
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
                        checkBoard = new ChessBoard(gameBoard);
                        checkBoardCheck = true;
                        checkBoard.movePiece(pieceToCheck, checkPiece);
                        if (isInCheck(teamTurn)) {
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
        return checkBoardCheck == chessGame.checkBoardCheck && teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard) && Objects.equals(tempBoard, chessGame.tempBoard) && Objects.equals(checkBoard, chessGame.checkBoard);
    }



    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard, tempBoard, checkBoard, checkBoardCheck);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", gameBoard=" + gameBoard +
                ", tempBoard=" + tempBoard +
                ", checkBoard=" + checkBoard +
                ", checkBoardCheck=" + checkBoardCheck +
                '}';
    }
}
