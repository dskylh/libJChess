import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.game.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChessGame {
    Board gameBoard = new Board();
    static List<Square> squareList = new ArrayList<>();
    static List<Square> emptySquares = new ArrayList<>();

    Event event = GameFactory.newEvent("Tournament");
    Round round = GameFactory.newRound(event, 1);
    public Game game = new Game("1", round);

    public Board getGameBoard() {
        return gameBoard;
    }

    // Herhangi bir hareket yapildiginda message labelina güncelleme yapar.
    public void displayMove(Move m, JLabel message, boolean isMoveLegal) {
        String move = m.toString();
        if (isMoveLegal) {
            message.setText("Hamle: " + move.toUpperCase());
        } else
            message.setText("Yanlış Hamle: " + move.toUpperCase());
    }

    public boolean playGame(Square coordination, JLabel message) {

        boolean isMoveLegal = false;
        squareList.add(coordination);


        // eger kullanici ilk olarak bos bir tasa basarsa squareList'i bosaltir.
        findAllEmptySquares(gameBoard);
        if (emptySquares.contains(squareList.get(0))) {
            squareList.clear();
        } else if (squareList.size() == 2) {

            Move move = new Move(squareList.get(0), squareList.get(1));
            // yapilan hamlenin legal olup olmadigini kontrol eder ve leagalse hareketi yapar.
            if (gameBoard.legalMoves().contains(move)) {
                gameBoard.doMove(move);
                isMoveLegal = true;
            }
            // hareketten sonra message'a güncelleme yapar.
            displayMove(move, message, isMoveLegal);
            squareList.clear();
        }
        return isMoveLegal;
    }

    // tum tahtayi tarayip bos kareleri bulur.
    public void findAllEmptySquares(Board board) {
        emptySquares.clear();
        Piece[] allPieces = board.boardToArray();

        for (int i = 0; i < allPieces.length - 1; i++) {
            if (allPieces[i].value().equals("NONE"))
                emptySquares.add(Square.squareAt(i));
        }
    }

    // Oyunun yedeğini alır.
    public void backUpBoard(Game game) {
        LinkedList<MoveBackup> moves = gameBoard.getBackup();
        MoveList moveList = new MoveList();
        for (MoveBackup moveBackup : moves) {
            Move moveBackupMove = moveBackup.getMove();
            moveList.add(moveBackupMove);
        }
        game.setBlackPlayer(new GenericPlayer("1", "Player1"));
        game.setWhitePlayer(new GenericPlayer("2", "Player2"));
        game.setHalfMoves(moveList);
        DatabaseCon.sqlConnection(game);

    }


}
