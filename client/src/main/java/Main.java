import chess.*;


// this is the class that you run to start your server and such
// you'll create separate classes for your chessboard ui and menu ui

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}