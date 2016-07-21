/**
 * Created by sentpim on 19.07.2016.
 */
public class ChessBoard {

    public static Figure[][] getBoard() {
        Figure[][] chessBoard = new Figure[8][8];
        Figure[] figures = Figure.values();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (i) {
                    case 0:
                        chessBoard[i][j] = figures[j];
                        break;
                    case 7:
                        chessBoard[i][j] = figures[j + 8];
                        break;
                    case 1:
                        chessBoard[i][j] = Figure.PAWNBLACK;
                        break;
                    case 6:
                        chessBoard[i][j] = Figure.PAWNWHITE;
                        break;
                }
            }
        }
        return chessBoard;
    }
}
