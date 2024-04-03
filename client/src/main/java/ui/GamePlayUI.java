package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREY;

public class GamePlayUI {

    private final PrintStream out;

    public GamePlayUI() {this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);}

    public void gamePlay(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.println();
        out.println();
        out.println("Enter Action Selection: ");
        out.println("1. Help");
        out.println("2. Redraw Chessboard");
        out.println("3. Leave");
        out.println("4. Make Move");
        out.println("5. Resign");
        out.println("6. Highlight Legal Moves");
    }

    public void gamePlayHelp(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.println();
        out.println();
        out.println("Help - show command structure");
        out.println("Redraw Chessboard - display the current chessboard");
        out.println("Leave - exit the game");
        out.println("Make Move - move a chess piece on your team");
        out.println("Resign - concede victory to opponent");
        out.println("Highlight Legal Moves - show possible moves for a given chess piece on your team");
    }

}
