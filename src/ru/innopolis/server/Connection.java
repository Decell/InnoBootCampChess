package ru.innopolis.server;

import ru.innopolis.boardStuff.ChessBoard;
import ru.innopolis.boardStuff.Figure;
import ru.innopolis.boardStuff.Position;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by sentpim on 19.07.2016.
 */

public class Connection implements Runnable {
    private Socket firstPlayer;
    private Socket secondPlayer;
    private Thread thread;
    private Figure[][] serverBoard = ChessBoard.getBoard();
    private int numOfMoves = 0;
    private List<Connection> connections;
    private int ID;
    private String lastMove;

    public Connection(Socket firstPlayer, List<Connection> connections, int ID) {
        this.firstPlayer = firstPlayer;
        this.connections = connections;
        this.ID = ID;
    }

    public void addSecondPlayer(Socket secondPlayer) {
        this.secondPlayer = secondPlayer;
        thread = new Thread(this);
        thread.start();
    }

    public boolean isClosed() {
        return firstPlayer.isClosed();
    }

    public void run() {
        try {
            OutputStream os = firstPlayer.getOutputStream();
            PrintWriter firstPlayerOut = new PrintWriter(os, true);
            InputStream is = firstPlayer.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader firstPlayerIn = new BufferedReader(isr);
            OutputStream os2 = secondPlayer.getOutputStream();
            PrintWriter secondPlayerOut = new PrintWriter(os2, true);
            InputStream is2 = secondPlayer.getInputStream();
            InputStreamReader isr2 = new InputStreamReader(is2);
            BufferedReader secondPlayerIn = new BufferedReader(isr2);
            firstPlayerOut.println("1"); // первый играет белыми
            secondPlayerOut.println("0"); // второй играет черными
            outer:
            while (true) {
                String playerMove;
                while (true) {
                    boolean eat = false;
                    if (numOfMoves % 2 == 0) {
                        playerMove = firstPlayerIn.readLine();

                        if (playerMove.equals("end"))
                            break outer;


                        if (playerMove.startsWith("EAT")) {
                            eat = true;
                            playerMove = playerMove.substring(3);
                        }

                    } else {

                        playerMove = secondPlayerIn.readLine();

                        if (playerMove.equals("end"))
                            break outer;

                        if (playerMove.startsWith("EAT")) {
                            eat = true;
                            playerMove = playerMove.substring(3);
                        }

                    }
                    if (isValid(playerMove, eat)) {
                        if (!checkForCheck(playerMove)) {
                            numOfMoves++;
                            break;
                        } else {
                            firstPlayerOut.println("CHECK");
                        }
                    } else {
                        firstPlayerOut.println("INVALID");
                    }
                }

                updateServerBoard(playerMove);

                firstPlayerOut.println(playerMove);
                secondPlayerOut.println(playerMove);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connections.remove(ID);
        }
    }


    private void updateServerBoard(String move) {
        String[] fromAndTo = move.split("to");
        String[] from = fromAndTo[0].split(",");
        String[] to = fromAndTo[1].split(",");
        serverBoard[Integer.parseInt(to[0])][Integer.parseInt(to[1])] =
                serverBoard[Integer.parseInt(from[0])][Integer.parseInt(from[1])];
        serverBoard[Integer.parseInt(from[0])][Integer.parseInt(from[1])] = null;
        lastMove = move;
    }

    private void revertMove() {
        String[] fromAndTo = lastMove.split("to");
        String[] from = fromAndTo[0].split(",");
        String[] to = fromAndTo[1].split(",");
        serverBoard[Integer.parseInt(from[0])][Integer.parseInt(from[1])] =
                serverBoard[Integer.parseInt(to[0])][Integer.parseInt(to[1])];
        serverBoard[Integer.parseInt(to[0])][Integer.parseInt(to[1])] = null;
    }

    private boolean isValid(String move, boolean eat) {
        String[] fromAndTo = move.split("to");
        String[] from = fromAndTo[0].split(",");
        String[] to = fromAndTo[1].split(",");
        int fromX = Integer.parseInt(from[0]);
        int fromY = Integer.parseInt(from[1]);
        int toX = Integer.parseInt(to[0]);
        int toY = Integer.parseInt(to[1]);

        if (toX == fromX && toY == fromY)                 // общая проверка, актуальная для любой фигуры
            return false;

        if (toX < 0 || toX > 7 || fromX < 0 || fromX > 7 || toY < 0 || toY > 7 || fromY < 0 || fromY > 7)
            return false;

        //узнаем какая фигура ходит
        Figure currentFigure = serverBoard[fromX][fromY];

        if (numOfMoves % 2 == 0 != currentFigure.isWhite())
            return false;


        if (currentFigure.getValue().equals("PAWN")) {
            return isValidPawnMove(fromX, fromY, toX, toY, eat);
        } else if (currentFigure.getValue().equals("ROOK")) {
            return isValidRookMove(fromX, fromY, toX, toY);
        } else if (currentFigure.getValue().equals("HORSE")) {
            return isValidHorseMove(fromX, fromY, toX, toY);
        } else if (currentFigure.getValue().equals("BISHOP")) {
            return isValidBishopMove(fromX, fromY, toX, toY);
        } else if (currentFigure.getValue().equals("QUEEN")) {
            return isValidQueenMove(fromX, fromY, toX, toY);
        } else if (currentFigure.getValue().equals("KING")) {
            return isValidKingMove(fromX, fromY, toX, toY);
        } else {
            return false;
        }
    }

    public boolean isValidPawnMove(int fromX, int fromY, int toX, int toY, boolean eat) {
        //пешка ест
        if (eat) {
            if (numOfMoves % 2 == 0) {
                return ((toX - fromX == -1) && (Math.abs(toY - fromY) == 1));
            } else {
                return ((toX - fromX == 1) && (Math.abs(toY - fromY) == 1));
            }
        } else {
            // Ходить первый раз пешкой можно на два хода вперед
            if ((numOfMoves % 2 == 0 && fromX == 6) || (numOfMoves % 2 != 0 && fromX == 1)) {
                return ((Math.abs(toX - fromX) <= 2) && (fromY == toY));
            } else {
                return ((Math.abs(toX - fromX) == 1) && (fromY == toY));
            }
        }
    }

    public boolean isValidRookMove(int fromX, int fromY, int toX, int toY) {
        if (fromX != toX && fromY != toY)
            return false;

        //двигаемся по оси X
        if (fromX == toX) {
            // если положительное число - ладья движется направо, если отрицательное - налево
            int sign = (int) Math.signum(toY - fromY);
            int i = 1;
            while (i < Math.abs(fromY - toY)) {
                if (serverBoard[fromX][fromY + sign * i] != null) {
                    return false;
                }
                i++;
            }
        }
        //двигаемся по оси Y
        else {
            int sign = (int) Math.signum(toX - fromX);
            int i = 1;
            while (i < Math.abs(fromX - toX)) {
                if (serverBoard[fromX + sign * i][fromY] != null) {
                    return false;
                }
                i++;
            }
        }

        return true;
    }

    public boolean isValidHorseMove(int fromX, int fromY, int toX, int toY) {
        return ((Math.abs(fromY - toY) == 1 && Math.abs(fromX - toX) == 2)
                || (Math.abs(fromY - toY) == 2 && Math.abs(fromX - toX) == 1));
    }

    public boolean isValidBishopMove(int fromX, int fromY, int toX, int toY) {
        if (Math.abs(fromX - toX) != Math.abs(fromY - toY))
            return false;
        if ((fromX < toX) && (fromY < toY)) {
            for (int i = 1; i < toX - fromX; i++) {
                if (serverBoard[fromX + i][fromY + i] != null)
                    return false;
            }
        } else if ((fromX > toX) && (fromY > toY)) {
            for (int i = 1; i < fromX - toX; i++) {
                if (serverBoard[fromX - i][fromY - i] != null)
                    return false;
            }
        } else if ((fromX > toX) && (fromY < toY)) {
            for (int i = 1; i < fromX - toX; i++) {
                if (serverBoard[fromX - i][fromY + i] != null)
                    return false;
            }
        } else {
            for (int i = 1; i < toX - fromX; i++) {
                if (serverBoard[fromX + i][fromY - i] != null)
                    return false;
            }
        }
        return true;
    }

    public boolean isValidQueenMove(int fromX, int fromY, int toX, int toY) {
        return (isValidBishopMove(fromX, fromY, toX, toY) || isValidRookMove(fromX, fromY, toX, toY));
    }

    public boolean isValidKingMove(int fromX, int fromY, int toX, int toY) {
        return (Math.abs(fromX - toX) <= 1 && Math.abs(fromY - toY) <= 1);
    }

    private boolean checkForCheck(String move) {
        updateServerBoard(move);     //Временно обновляю доску, чтобы проверить шахи
        Position kingPosition = findKingPosition(numOfMoves % 2 == 0);
        boolean answer = isKingUnderAttack(kingPosition);
        revertMove();  //Сбрасываю доску
        return answer;
    }

    private Position findKingPosition(boolean isWhite) {
        Position p = null;
        Figure target;
        if (isWhite) {
            target = Figure.KINGWHITE;
        } else {
            target = Figure.KINGBLACK;
        }
        outer:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (serverBoard[i][j] == target) {
                    p = new Position(i, j);
                    break outer;
                }
            }
        }
        return p;
    }

    private boolean isKingUnderAttack(Position kingPosition) {
        int x = kingPosition.getX();
        int y = kingPosition.getY();
        boolean isWhite = numOfMoves % 2 == 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (serverBoard[i][j] != null) {
                    Figure attacker = serverBoard[i][j];
                    if (isWhite) {
                        if (!attacker.isWhite()) {
                            if (checkIfAttackerAttacks(i, j, x, y, attacker))
                                return true;
                        }
                    } else {
                        if (attacker.isWhite()) {
                            if (checkIfAttackerAttacks(i, j, x, y, attacker))
                                return true;
                        }
                    }
                }
            }
        }

        return false;     // если ни одно из вышеперечисленных не true, значит король не под шахом
    }

    private boolean checkIfAttackerAttacks(int i, int j, int x, int y, Figure attacker) {
        if (attacker.getValue().equals("PAWN")) {
            if (isValidPawnMove(i, j, x, y, true))
                return true;
        } else if (attacker.getValue().equals("ROOK")) {
            if (isValidRookMove(i, j, x, y))
                return true;
        } else if (attacker.getValue().equals("HORSE")) {
            if (isValidHorseMove(i, j, x, y))
                return true;
        } else if (attacker.getValue().equals("BISHOP")) {
            if (isValidBishopMove(i, j, x, y))
                return true;
        } else if (attacker.getValue().equals("QUEEN")) {
            if (isValidQueenMove(i, j, x, y))
                return true;
        } else {
            if (isValidKingMove(i, j, x, y))
                return true;
        }
        return false;
    }

}
