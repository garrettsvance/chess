import chess.*;
import ui.ChessBoardUI;


// this is the class that you run to start your server and such
// you'll create separate classes for your chessboard ui and menu ui

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessGame game = new ChessGame();

        ChessBoard board = game.getBoard();
        board.resetBoard();
        game.setBoard(board);

        ChessBoardUI startBoard = new ChessBoardUI();
        startBoard.printBoard(game);
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        startBoard.printBoard(game);
    }
}