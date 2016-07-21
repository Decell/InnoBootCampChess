import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by sentpim on 19.07.2016.
 */

public class GameThread extends Thread {
    private BufferedReader br;
    Client client;
    private boolean isWhite;
    private Figure[][] board;

    public GameThread(BufferedReader br, boolean isWhite, Client client, Figure[][] board) {
        this.br = br;
        this.isWhite = isWhite;
        this.client = client;
        this.board = board;
    }

    public void run() {
        while (true) {
            try {
                String move = br.readLine();
                if (!move.equals("INVALID") && !move.equals("CHECK")) {
                    Position[] fromAndTo = parseCoordinates(move);

                    //обновление доски
                    board[fromAndTo[1].getX()][fromAndTo[1].getY()] = board[fromAndTo[0].getX()][fromAndTo[0].getY()];
                    board[fromAndTo[0].getX()][fromAndTo[0].getY()] = null;

                    if (!isWhite) {
                        fromAndTo = blackifyCoordinates(fromAndTo);
                    }
                    client.updateBoard(fromAndTo[0], fromAndTo[1]);
                } else {
                    if (move.equals("INVALID")) {
                        client.alert("Некорректный ход");
                    } else {
                        client.alert("Вам шах!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Position[] parseCoordinates(String coordinates) {
        Position[] position = new Position[2];
        String[] fromAndTo = coordinates.split("to");
        String[] from = fromAndTo[0].split(",");
        String[] to = fromAndTo[1].split(",");
        position[0] = new Position(Integer.parseInt(from[0]), Integer.parseInt(from[1]));
        position[1] = new Position(Integer.parseInt(to[0]), Integer.parseInt(to[1]));
        return position;
    }

    private Position[] blackifyCoordinates(Position[] fromAndTo) {
        Position[] position = new Position[2];
        position[0] = new Position(7 - fromAndTo[0].getX(), 7 - fromAndTo[0].getY());
        position[1] = new Position(7 - fromAndTo[1].getX(), 7 - fromAndTo[1].getY());
        return position;
    }
}
