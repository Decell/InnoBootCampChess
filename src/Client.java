import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sentpim on 19.07.2016.
 */

public class Client extends JFrame {
    private final int BUTTONSIZE = 50;
    private boolean white;
    private Figure[] figures = Figure.values();
    private Figure[][] chessBoard;
    private boolean firstPressed = true;
    private Position firstPosition;
    private PrintWriter pw;
    private int numberOfMoves = 0;
    private Map<Position, JButton> buttons = new HashMap<Position, JButton>();


    public Client(String address) {
        final int port = 3456;
        final String host;
        if (address.equals("")) {
            host = "localhost";
        } else {
            host = address;
        }
        try {
            final Socket s = new Socket(host, port);
            addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    pw.println("end");
                    try {
                        s.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void windowClosed(WindowEvent e) {

                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });



            OutputStream os = s.getOutputStream();
            pw = new PrintWriter(os, true);
            InputStream is = s.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String whiteOrBlack = br.readLine();
            this.white = whiteOrBlack.equals("1");

            if (white) {
                this.setTitle("ВАШ ХОД");
            } else {
                this.setTitle("");
            }

            chessBoard = ChessBoard.getBoard();

            setBounds(50, 50, BUTTONSIZE * 8 + 5, BUTTONSIZE * 8 + 25);
            setLayout(new GroupLayout(getContentPane()));

            generateField();

            GameThread thread = new GameThread(br, this.white, this, this.chessBoard);
            thread.start();

            setSize(407, 428);
            setResizable(false);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateField() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton jb = new JButton();
                jb.setBounds(j * BUTTONSIZE, i * BUTTONSIZE, BUTTONSIZE, BUTTONSIZE);

                switch (i) {
                    case 0:
                        if (white) {
                            jb.setIcon(new ImageIcon(figures[j].getFilePath()));
                        } else {
                            jb.setIcon(new ImageIcon(figures[15 - j].getFilePath()));
                        }
                        validate();
                        break;
                    case 7:
                        if (white) {
                            jb.setIcon(new ImageIcon(figures[j + 8].getFilePath()));
                        } else {
                            jb.setIcon(new ImageIcon(figures[7 - j].getFilePath()));
                        }
                        validate();
                        break;
                    case 1:
                        if (white) {
                            jb.setIcon(new ImageIcon(Figure.PAWNBLACK.getFilePath()));
                        } else {
                            jb.setIcon(new ImageIcon(Figure.PAWNWHITE.getFilePath()));
                        }
                        validate();
                        break;
                    case 6:
                        if (white) {
                            jb.setIcon(new ImageIcon(Figure.PAWNWHITE.getFilePath()));
                        } else {
                            jb.setIcon(new ImageIcon(Figure.PAWNBLACK.getFilePath()));
                        }
                        validate();
                        break;
                }

                jb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if ((numberOfMoves % 2 == 0) == white) {
                            JButton buttonPressed = (JButton) e.getSource();

                            int x = buttonPressed.getY() / BUTTONSIZE;
                            int y = buttonPressed.getX() / BUTTONSIZE;

                            if (!white) {
                                x = 7 - x;
                                y = 7 - y;
                            }
                            if (firstPressed) {
                                if (chessBoard[x][y] != null) {
                                    firstPosition = new Position(x, y);
                                    firstPressed = false;
                                }
                            } else {
                                if (chessBoard[x][y] == null) {
                                    pw.println(firstPosition.toString() + "to" + x + "," + y);
                                    firstPressed = true;

                                } else {
                                    if (white == chessBoard[x][y].isWhite()) {
                                        firstPosition = new Position(x, y);
                                    } else {
                                        pw.println("EAT" + firstPosition.toString() + "to" + x + "," + y);
                                        firstPressed = true;
                                    }
                                }
                            }
                        }
                    }
                });
                add(jb);
                buttons.put(new Position(i, j), jb);

            }

        }
    }


    public void updateBoard(Position from, Position to) {
        // перерисовка поля согласно координатам
        JButton buttonFrom = buttons.get(from);
        JButton buttonTo = buttons.get(to);
        buttonTo.setIcon(buttonFrom.getIcon());
        buttonFrom.setIcon(null);
        if (this.getTitle().equals("")) {
            this.setTitle("ВАШ ХОД");
        } else {
            this.setTitle("");
        }
        numberOfMoves++;
    }

    public void alert(String alertMSG) {
        JOptionPane.showMessageDialog(this, alertMSG);
    }
}
