import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.game.GameResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class UI {
    JPanel gui = new JPanel(new BorderLayout(3, 3));
    JButton[][] chessBoardSquares = new JButton[8][8];
    Image[][] chessPieceImages = new Image[2][6];
    JPanel chessBoard;
    JLabel message = new JLabel("Hamle: ");
    JLabel sideToMove = new JLabel("");
    static final String COLS = "ABCDEFGH";

    public static final int KING = 0, QUEEN = 1,
            ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    public static final int BLACK = 0, WHITE = 1;
    public Square pieceLocation;
    public boolean isMoveLegal;
    public ChessGame chessGame;
    public Board gameBoard;
    public static JButton unDoMoveButton = new JButton("Hamleyi Geri Al");
    JToolBar tools = new JToolBar();


    // constructor
    UI() {
        initializeGui();
        gameNotStarted(message);

    }

    // tum kullanici arayuzu initialize eder.
    private void initializeGui() {
        createImages();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        Action newGameAction = new AbstractAction("Yeni Oyun") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupNewGame();
                message.setText("Oyun Başladı.");
                displaySideToMove(gameBoard.getSideToMove(), sideToMove);
            }
        };
        // Hamleyi geri al buttonu icin action listener eklenir.
        unDoMoveButton.addActionListener(e -> {
            gameBoard.undoMove();
            drawPieces(gameBoard);
            displaySideToMove(gameBoard.getSideToMove(), sideToMove);
        });
        tools.add(newGameAction);
        tools.addSeparator();
        JToolBar tools = new JToolBar();
        tools.addSeparator();
        tools.add(message);
        tools.add(sideToMove);

        chessBoard = new JPanel(new GridLayout(0, 9)) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int) d.getWidth(), (int) d.getHeight());
                } else if (c.getWidth() > d.getWidth() && c.getHeight() > d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (Math.min(w, h));
                return new Dimension(s, s);
            }
        };
        chessBoard.setBorder(new CompoundBorder(
                new EmptyBorder(8, 8, 8, 8),
                new LineBorder(Color.DARK_GRAY)
        ));
        Color boardBG = new Color(22, 21, 18);
        chessBoard.setBackground(boardBG);
        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.setBackground(boardBG);
        boardConstrain.add(chessBoard);
        gui.add(boardConstrain);

        Insets buttonMargin = new Insets(0, 0, 0, 0);
        // 8x8 bir array'in icerisinde tüm butonların olusturulmasi
        for (int i = 0; i < chessBoardSquares.length; i++) {
            for (int j = 0; j < chessBoardSquares[i].length; j++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // satranc parcalarinin spritelari 64x64 seklinde ayrilir
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                // siyah ve beyaz taslarin renkleri belirlenir
                if ((j % 2 == 1 && i % 2 == 1)
                        //) {
                        || (j % 2 == 0 && i % 2 == 0)) {
                    b.setBackground(new Color(240, 217, 181));
                } else {
                    b.setBackground(new Color(181, 136, 99));
                }
                // button array matrisine eklenir
                chessBoardSquares[j][i] = b;
            }
        }
        // tum buttonlara action listener burada eklenir.
        doButton();
        chessBoard.add(new JLabel(""));
        for (int i = 0; i < 8; i++) {
            JLabel cols = new JLabel(COLS.substring(i, i + 1), SwingConstants.CENTER);
            cols.setForeground(Color.lightGray);
            chessBoard.add(cols);
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j == 0) {
                    JLabel rows = new JLabel("" + (9 - (i + 1)), SwingConstants.CENTER);
                    rows.setForeground(Color.lightGray);
                    chessBoard.add(rows);
                }
                chessBoard.add(chessBoardSquares[j][i]);
            }
        }
    }

    public JComponent getGui() {
        return gui;
    }

    private void createImages() {
        try {
            // internetten sprite lerin indirilmesi
            URL url = new URL("http://i.stack.imgur.com/memI0.png");
            BufferedImage bi = ImageIO.read(url);
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 6; j++) {
                    chessPieceImages[i][j] = bi.getSubimage(
                            j * 64, i * 64, 64, 64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    // yeni oyun baslatir.
    private void setupNewGame() {
        chessGame = new ChessGame();
        gameBoard = chessGame.getGameBoard();

        if (chessGame != null) {
            tools.add(unDoMoveButton);
            tools.add(message);
            tools.add(sideToMove);
            displaySideToMove(gameBoard.getSideToMove(), sideToMove);
        }
        // tahtaya taslarin koyulur.
        drawPieces(gameBoard);
        // tahta sifirlanir.
    }

    // taslari cizer
    public void drawPieces(Board gameBoard) {

        // oyun tahtasini bir arraya atar
        Piece[] pieces = gameBoard.boardToArray();


        for (int i = 0; i < pieces.length; i++) {
            Piece current_piece = pieces[i];
            // j burada sutunlari, i burada satirlari belirler.

            int j = i % 8;
            int k = i / 8;
            // 0 1 2 3 4 5 6 7 --> 7 6 5 4 3 2 1 0
            k = (k - 7) * -1;
            try {
                // tahtada hangi tas varsa o tasa gore button iconu atanir.
                if (current_piece.value().equals("NONE")) {
                    chessBoardSquares[j][k].setIcon(null);
                } else if (current_piece.value().equals("WHITE_ROOK")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[WHITE][ROOK]));
                } else if (current_piece.value().equals("WHITE_KNIGHT")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[WHITE][KNIGHT]));
                } else if (current_piece.value().equals("WHITE_BISHOP")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[WHITE][BISHOP]));
                } else if (current_piece.value().equals("WHITE_QUEEN")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[WHITE][QUEEN]));
                } else if (current_piece.value().equals("WHITE_KING")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[WHITE][KING]));
                } else if (current_piece.value().equals("WHITE_PAWN")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[WHITE][PAWN]));
                } else if (current_piece.value().equals("BLACK_ROOK")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[BLACK][ROOK]));
                } else if (current_piece.value().equals("BLACK_KNIGHT")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[BLACK][KNIGHT]));
                } else if (current_piece.value().equals("BLACK_BISHOP")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[BLACK][BISHOP]));
                } else if (current_piece.value().equals("BLACK_KING")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[BLACK][KING]));
                } else if (current_piece.value().equals("BLACK_QUEEN")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[BLACK][QUEEN]));
                } else if (current_piece.value().equals("BLACK_PAWN")) {
                    chessBoardSquares[j][k].setIcon(new ImageIcon(chessPieceImages[BLACK][PAWN]));
                }
            } catch (Exception ignored) {
            }
        }


    }


    public void gameNotStarted(JLabel message) {
        message.setText("Oyun Başlatılmadı.");
    }


    private void displaySideToMove(Side sideToMove, JLabel label) {
        if (sideToMove.equals(Side.WHITE))
            label.setText("     Sıra Beyazda.");
        else
            label.setText("     Sıra Siyahta.");
    }


    // oyun bittiginde sonuc gosterir
    public void displayResult(Side side, boolean draw) {
        JFrame frame = new JFrame("Oyun Bitti");
        JLabel sideToWin = new JLabel();
        String winner = "";
        if (draw) {
            winner = "Berabere";
            chessGame.game.setResult(GameResult.DRAW);
            chessGame.backUpBoard(chessGame.game);
        } else if (side.equals(Side.WHITE)) {
            winner = "Beyaz";
            chessGame.game.setResult(GameResult.WHITE_WON);
            chessGame.backUpBoard(chessGame.game);
        } else if (side.equals(Side.BLACK)) {
            winner = "Siyah";
            chessGame.game.setResult(GameResult.BLACK_WON);
            chessGame.backUpBoard(chessGame.game);
        }
        sideToWin.setText("Oyunu Kazanan: " + winner);
        JPanel panel = new JPanel();
        panel.add(sideToWin);
        frame.add(panel);
        frame.setSize(300, 100);
        frame.setVisible(true);
        frame.setLayout(new FlowLayout());
    }

    public void doButton() {
        for (int i = 0; i < chessBoardSquares.length; i++) {
            // matristeki 2. arrayi doner
            for (int j = chessBoardSquares[i].length - 1; j >= 0; j--) {
                int finalI = i;
                int finalJ = (j - 8) * -1;
                // 7 6 5 4 3 2 1 0 --> 1 2 3 4 5 6 7 8
                chessBoardSquares[i][j].addActionListener(e -> {
                    String col = COLS.substring(finalI, finalI + 1);
                    String row = Integer.toString(finalJ);
                    pieceLocation = Square.fromValue(col + row);
                    try {
                        isMoveLegal = chessGame.playGame(pieceLocation, message);
                        displaySideToMove(gameBoard.getSideToMove(), sideToMove);
                        drawPieces(gameBoard);
                    } catch (Exception exception) {
                        ChessGame.squareList.clear();
                    }
                    // eger bir taraf mat olduysa oyunu sonlandirir.
                    if (gameBoard.isMated()) {
                        if (gameBoard.getSideToMove() == Side.WHITE) {
                            displayResult(Side.BLACK, false);
                        } else {
                            displayResult(Side.WHITE, false);
                        }

                    }
                    // oyun cikmaza ya da berabere kalirsa oyunu sonlandirir.
                    if (gameBoard.isStaleMate() || gameBoard.isDraw()) {
                        displayResult(Side.WHITE, true);
                    }

                });
            }
        }
    }
}
