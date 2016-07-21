/**
 * @author Nail Alishev
 *         11-401
 *         Chess
 */
public enum Figure {
    ROOKBLACKLEFT("RookBlack.png", "ROOK", false), HORSEBLACKLEFT("HorseBlack.png", "HORSE", false),
    BISHOPBLACKLEFT("BishopBlack.png", "BISHOP", false), QUEENBLACK("QueenBlack.png", "QUEEN", false),
    KINGBLACK("KingBlack.png", "KING", false), BISHOPBLACKRIGTH("BishopBlack.png", "BISHOP", false),
    HORSEBLACKRIGHT("HorseBlack.png", "HORSE", false), ROOKBLACKRIGHT("RookBlack.png", "ROOK", false),
    ROOKWHITELEFT("RookWhite.png", "ROOK", true), HORSEWHITELEFT("HorseWhite.png", "HORSE", true),
    BISHOPWHITELEFT("BishopWhite.png", "BISHOP", true), QUEENWHITE("QueenWhite.png", "QUEEN", true),
    KINGWHITE("KingWhite.png", "KING", true), BISHOPWHITERIGHT("BishopWhite.png", "BISHOP", true),
    HORSEWHITERIGHT("HorseWhite.png", "HORSE", true), ROOKWHITERIGHT("RookWhite.png", "ROOK", true),

    PAWNBLACK("PawnBlack.png", "PAWN", false), PAWNWHITE("PawnWhite.png", "PAWN", true);

    private String filePath;
    private String value;
    private boolean isWhite;

    Figure(String filePath, String value, boolean isWhite) {
        this.filePath = filePath;
        this.value = value;
        this.isWhite = isWhite;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isWhite() {
        return this.isWhite;
    }
}
