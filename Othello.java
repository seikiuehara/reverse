public class Othello {
    private int turn; // 黒: 0, 白: 1
    private int[] board;

    private static final int BLACK = 0;
    private static final int WHITE = 1;
    private static final int EMPTY = -1;
    private static final int SIZE = 8;
    private static final int[] DIRECTIONS = { -1, 1, -SIZE, SIZE, -SIZE - 1, -SIZE + 1, SIZE - 1, SIZE + 1 };

    public Othello() {
        board = new int[SIZE * SIZE];
        for (int i = 0; i < board.length; i++) {
            board[i] = EMPTY;
        }
        // 中央に初期配置
        board[27] = WHITE;
        board[28] = BLACK;
        board[35] = BLACK;
        board[36] = WHITE;

        turn = BLACK;
    }

    public void change() {
        turn = 1 - turn;
    }

    public boolean check(int place) {
        if (!inBounds(place) || board[place] != EMPTY)
            return false;

        for (int dir : DIRECTIONS) {
            int p = place + dir;

            if (!inBounds(p) || !onSameLine(place, p, dir))
                continue;
            if (board[p] == 1 - turn) {
                while (inBounds(p) && board[p] == 1 - turn && onSameLine(place, p, dir)) {
                    p += dir;
                    if (!inBounds(p))
                        break;
                }
                if (inBounds(p) && board[p] == turn && onSameLine(place, p, dir)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void place(int place) {
        if (!check(place))
            return;

        board[place] = turn;

        for (int dir : DIRECTIONS) {
            int p = place + dir;
            if (!inBounds(p) || !onSameLine(place, p, dir))
                continue;

            if (board[p] == 1 - turn) {
                int temp = p;
                while (inBounds(temp) && board[temp] == 1 - turn && onSameLine(place, temp, dir)) {
                    temp += dir;
                }

                if (inBounds(temp) && board[temp] == turn && onSameLine(place, temp, dir)) {
                    // 挟まれているので石をひっくり返す
                    int flip = place + dir;
                    while (flip != temp) {
                        board[flip] = turn;
                        flip += dir;
                    }
                }
            }
        }

        change();
    }

    public boolean pass() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            if (board[i] == EMPTY && check(i))
                return false;
        }
        return true;
    }

    public String judge() {
        int black = 0;
        int white = 0;
        for (int cell : board) {
            if (cell == BLACK)
                black++;
            else if (cell == WHITE)
                white++;
        }
        if (black > white)
            return "Black wins";
        else if (white > black)
            return "White wins";
        else
            return "Draw";
    }

    public int[] get_board() {
        return board.clone();
    }

    public int get_turn() {
        return turn;
    }

    private boolean inBounds(int index) {
        return 0 <= index && index < SIZE * SIZE;
    }

    private boolean onSameLine(int from, int to, int dir) {
        int fromRow = from / SIZE, fromCol = from % SIZE;
        int toRow = to / SIZE, toCol = to % SIZE;
        if (dir == -1 || dir == 1) {
            return fromRow == toRow;
        }
        if (dir == -SIZE || dir == SIZE) {
            return true;
        }
        if (dir == -SIZE - 1 || dir == SIZE + 1) {
            return Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol);
        }
        if (dir == -SIZE + 1 || dir == SIZE - 1) {
            return Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol);
        }
        return false;
    }
}
