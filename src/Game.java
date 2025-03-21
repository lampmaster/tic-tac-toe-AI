import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Game {
    private String[][] board;
    private boolean isPlayerOneTurn = false;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    final private Scanner scanner = new Scanner(System.in);
    final private String human = "X";
    final private String robot = "O";

    public void start() {
        System.out.println("Welcome to Tic Tac Toe!");
        board = new String[][]{
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", "9"}
        };



        while (gameStatus == GameStatus.IN_PROGRESS) {
            printBoard();
            printPlayerTurn();
            robotTurn();
            printBoard();
            printPlayerTurn();
            processUserInput();
        }
    }

    public record Move(int row, int col) {}

    private void printBoard() {
        System.out.println("-------------");
        for (String[] row : board) {
            System.out.print("| ");
            for (String cell : row) {
                System.out.print(cell + " | ");
            }
            System.out.println("\n-------------");
        }
    }

    private void printPlayerTurn() {
        String player = getPlayerName();
        if (player.equals(robot)) {
            System.out.println("Robot " + player + " turn: ");
            return;
        }
        System.out.println("Player " + player + " turn (1-9): ");
    }

    private void processUserInput() {
        if (gameStatus != GameStatus.IN_PROGRESS) {
            return;
        }
        Move move = getUserMove();

        int row = move.row;
        int col = move.col;

        board[row][col] = human;

        if (isWinner(row, col, human)) {
            gameStatus = GameStatus.HUMAN_WIN;
            System.out.println("Player " + human + " wins!");
        } else if (isDraw()) {
            gameStatus = GameStatus.DRAW;
            System.out.println("It's a draw!");
        }

        isPlayerOneTurn = !isPlayerOneTurn;
    }

    private Move getUserMove() {
        while (true) {
            String input = scanner.nextLine();

            try {
                int position = Integer.parseInt(input);

                if (position < 1 || position > board.length * board.length) {
                    System.out.println("Invalid move. Please enter a number between 1 and 9");
                    continue;
                }

                int row = (position - 1) / board.length;
                int col = (position - 1) % board.length;

                if (board[row][col].equals("X") || board[row][col].equals("O")) {
                    System.out.println("Invalid move. Cell is already taken.");
                } else {
                    return new Move(row, col);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 9");
            }
        }
    }

    private boolean isWinner(Integer row, Integer col, String player) {
        return isWinner(row, col, player, board);
    }

    private boolean isWinner(Integer row, Integer col, String player, String[][] board) {
        int symbolsToWin = 3;
        int rowCount = 0, colCount = 0, diagCount = 0, antiDiagCount = 0;

        for (int i = 0; i < board.length; i++) {
            rowCount = board[row][i].equals(player) ? rowCount + 1 : 0;
            if (rowCount == symbolsToWin) return true;

            colCount = board[i][col].equals(player) ? colCount + 1 : 0;
            if (colCount == symbolsToWin) return true;

            diagCount = board[i][i].equals(player) ? diagCount + 1 : 0;
            if (diagCount == symbolsToWin) return true;

            antiDiagCount = board[i][board.length - 1 - i].equals(player) ? antiDiagCount + 1 : 0;
            if (antiDiagCount == symbolsToWin) return true;
        }


        return false;
    }

    private boolean isDraw() {
        for (String[] row : board) {
            for (String cell : row) {
                if (!cell.equals("X") && !cell.equals("O")) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getPlayerName () {
        return isPlayerOneTurn ? human : robot;
    }

    private GameStatus checkGameStatus(Integer row, Integer col, String player) {
        if (isWinner(row, col, player)) {
            return player.equals(human) ? GameStatus.HUMAN_WIN : GameStatus.ROBOT_WIN;
        }

        return isDraw() ? GameStatus.DRAW : GameStatus.IN_PROGRESS;
    }

    private void robotTurn() {
        if (gameStatus != GameStatus.IN_PROGRESS) return;

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        List<Move> availableMoves = getAvailableMoves(board);

        for (Move move : availableMoves) {
            board[move.row][move.col] = robot;
            int score = minimax(board, move, robot);

            String emptyCell = String.valueOf(move.row * 3 + move.col + 1);
            board[move.row][move.col] = emptyCell;

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }


        if (bestMove != null) {
            board[bestMove.row][bestMove.col] = robot;
            isPlayerOneTurn = !isPlayerOneTurn;

            if (isWinner(bestMove.row, bestMove.col, robot)) {
                gameStatus = GameStatus.ROBOT_WIN;
                System.out.println("Robot(" + robot + ") wins!");
            } else if (isDraw()) {
                gameStatus = GameStatus.DRAW;
                System.out.println("It's a draw!");
            }
        }
    }

    private Integer minimax(String[][] possibleGame, Move currentMove, String player) {
        GameStatus status = checkGameStatus(currentMove.row, currentMove.col, player);
        if (status != GameStatus.IN_PROGRESS) {
            return score(status);
        }

        String nextPlayer = Objects.equals(player, robot) ? human : robot;
        int bestScore = Objects.equals(nextPlayer, robot) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        List<Move> availableMoves = getAvailableMoves(possibleGame);

        for (Move moveItem : availableMoves) {
            int row = moveItem.row;
            int col = moveItem.col;

            possibleGame[row][col] = nextPlayer;
            int currentScore = minimax(possibleGame, moveItem, nextPlayer);
            String emptyCell = String.valueOf(row * 3 + col + 1);
            possibleGame[row][col] = emptyCell;

            if (Objects.equals(nextPlayer, robot)) {
                bestScore = Integer.max(currentScore, bestScore);
            } else {
                bestScore = Integer.min(currentScore, bestScore);
            }
        }

        return bestScore;
    }

    private List<Move> getAvailableMoves(String[][] board) {
        List<Move> availableMoves = new ArrayList<>();

        for (int i = 1; i <= board.length * board.length; i++) {
            int row = (i - 1) / board.length;
            int col = (i - 1) % board.length;

            if (!board[row][col].equals("X") && !board[row][col].equals("O")) {
                availableMoves.add(new Move(row, col));
            }

        }
        return availableMoves;
    }
    private Integer score(GameStatus currentStatus) {
        if (currentStatus == GameStatus.HUMAN_WIN) {
            return -1;
        } else if (currentStatus == GameStatus.ROBOT_WIN) {
            return 1;
        } else {
            return 0;
        }
    }
}
