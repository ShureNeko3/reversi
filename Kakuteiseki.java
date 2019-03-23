package reversi;

public class Kakuteiseki {
	 int nothing = 0;
     int black = 1;
	 int white = 2;
	 int turn = black;
	 int available = 3;
	 void check(int board[][], int board_full[][][]) {
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					for (int k = 0; k < 4; k++) {
						board_full[i][j][k] = 0;
					}
				}
			}
			/*
			 * for(int i=1;i<=8;i++){ board[i][1]=black; board[i][3]=black;
			 * board[1][i]=black; board[i][i]=black; board[9-i][i]=black;
			 * 
			 * } board[1][6]=black; board[2][7]=black; board[3][8]=black;
			 */
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					int n = i - j;
					int m = 9 - (i + j);

					int count0 = 0;
					int count1 = 0;
					int count2 = 0;
					int count3 = 0;
					for (int a = 1; a <= 8; a++) {
						if (board[a][j] == nothing || board[a][j] == available) {
							count0++;
						}
						if (board[i][a] == nothing || board[i][a] == available) {
							count1++;
						}
					}
					if (n >= 0) {
						for (int b = 1; b <= 8 - n; b++) {
							if (board[b + n][b] == nothing
									|| board[b + n][b] == available) {
								count2++;
							}
						}
					}
					if (n < 0) {
						n = -1 * n;
						for (int b = 1; b <= 8 - n; b++) {
							if (board[b][b + n] == nothing
									|| board[b][b + n] == available) {
								count2++;
							}
						}
					}
					if (m >= 0) {
						for (int c = 1; c <= 8 - m; c++) {
							if (board[9 - m - c][c] == nothing
									|| board[9 - m - c][c] == available) {
								count3++;
							}
						}
					}
					if (m < 0) {
						m = -1 * m;
						for (int c = 1; c <= 8 - m; c++) {
							if (board[9 - c][m + c] == nothing
									|| board[9 - c][m + c] == available) {
								count3++;
							}
						}
					}

					if (count0 == 0) {
						board_full[i][j][0] = turn;
					}
					if (count1 == 0) {
						board_full[i][j][1] = turn;
					}
					if (count2 == 0) {
						board_full[i][j][2] = turn;
					}
					if (count3 == 0) {
						board_full[i][j][3] = turn;
					}
				}
			}

		}
	 
	 public void settled_check_down(int x, int y, int board[][], int turn,
				int board_settled_value[][]) {
			if (board[x][y + 1] == turn
					&& y != 8
					&& (board_settled_value[x][y] == turn
							|| board_settled_value[x][y + 2] == turn || board_full[x][y + 1][1] != 0)) {
				board_settled_value[x][y + 1] = turn;
				settled_check_down(x, y + 1, board, turn, board_settled_value);
			}
		}

		public void settled_check_up(int x, int y, int board[][], int turn,
				int board_settled_value[][]) {
			if (board[x][y - 1] == turn
					&& y != 1
					&& (board_settled_value[x][y] == turn
							|| board_settled_value[x][y - 2] == turn || board_full[x][y - 1][1] != 0)) {
				board_settled_value[x][y - 1] = turn;
				settled_check_up(x, y - 1, board, turn, board_settled_value);
			}
		}

		public void settled_check_right(int x, int y, int board[][], int turn,
				int board_settled_value[][]) {
			if (board[x + 1][y] == turn
					&& x != 8
					&& (board_settled_value[x][y] == turn
							|| board_settled_value[x + 2][y] == turn || board_full[x + 1][y][0] != 0)) {
				board_settled_value[x + 1][y] = turn;
				settled_check_right(x + 1, y, board, turn, board_settled_value);
			}
		}

		public void settled_check_left(int x, int y, int board[][], int turn,
				int board_settled_value[][]) {
			if (board[x - 1][y] == turn
					&& x != 1
					&& (board_settled_value[x - 2][y] == turn
							|| board_settled_value[x][y] == turn || board_full[x - 1][y][0] != 0)) {
				board_settled_value[x - 1][y] = turn;
				settled_check_left(x - 1, y, board, turn, board_settled_value);
			}
		}

		public void settled_check_all_go_up(int x, int y, int board[][], int turn,
				int board_settled_value[][]) {
			if (board[x][y - 1] == turn
					&& ((board_settled_value[x - 1][y - 2] == turn || board_settled_value[x + 1][y] == turn) || board_full[x][y - 1][2] != 0)
					&& ((board_settled_value[x][y - 2] == turn || board_settled_value[x][y] == turn) || board_full[x][y - 1][1] != 0)
					&& ((board_settled_value[x - 1][y] == turn || board_settled_value[x + 1][y - 2] == turn) || board_full[x][y - 1][3] != 0)
					&& ((board_settled_value[x - 1][y - 1] == turn || board_settled_value[x + 1][y - 1] == turn) || board_full[x][y - 1][0] != 0)) {
				board_settled_value[x][y - 1] = turn;
				settled_check_all_go_up(x, y - 1, board, turn, board_settled_value);
			}
		}

		public void settled_check_all_go_down(int x, int y, int board[][],
				int turn, int board_settled_value[][]) {
			if (board[x][y + 1] == turn
					&& ((board_settled_value[x - 1][y] == turn || board_settled_value[x + 1][y + 2] == turn) || board_full[x][y + 1][2] != 0)
					&& ((board_settled_value[x][y] == turn || board_settled_value[x][y + 2] == turn) || board_full[x][y + 1][1] != 0)
					&& ((board_settled_value[x - 1][y + 2] == turn || board_settled_value[x + 1][y] == turn) || board_full[x][y + 1][3] != 0)
					&& ((board_settled_value[x - 1][y + 1] == turn || board_settled_value[x + 1][y + 1] == turn) || board_full[x][y + 1][0] != 0)) {
				board_settled_value[x][y + 1] = turn;
				settled_check_all_go_down(x, y + 1, board, turn,
						board_settled_value);
			}
		}

		public void settled_check_all_go_right(int x, int y, int board[][],
				int turn, int board_settled_value[][]) {
			if (board[x + 1][y] == turn
					&& ((board_settled_value[x][y - 1] == turn || board_settled_value[x + 2][y + 1] == turn) || board_full[x + 1][y][2] != 0)
					&& ((board_settled_value[x + 1][y - 1] == turn || board_settled_value[x + 1][y + 1] == turn) || board_full[x + 1][y][1] != 0)
					&& ((board_settled_value[x][y + 1] == turn || board_settled_value[x + 2][y - 1] == turn) || board_full[x + 1][y][3] != 0)
					&& ((board_settled_value[x][y] == turn || board_settled_value[x + 2][y] == turn) || board_full[x + 1][y][0] != 0)) {
				board_settled_value[x + 1][y] = turn;
				settled_check_all_go_right(x + 1, y, board, turn,
						board_settled_value);
			}
		}

		public void settled_check_all_go_left(int x, int y, int board[][],
				int turn, int board_settled_value[][]) {
			if (board[x - 1][y] == turn
					&& ((board_settled_value[x - 2][y - 1] == turn || board_settled_value[x][y + 1] == turn) || board_full[x - 1][y][2] != 0)
					&& ((board_settled_value[x - 1][y - 1] == turn || board_settled_value[x - 1][y + 1] == turn) || board_full[x - 1][y][1] != 0)
					&& ((board_settled_value[x - 2][y + 1] == turn || board_settled_value[x][y - 1] == turn) || board_full[x - 1][y][3] != 0)
					&& ((board_settled_value[x - 2][y] == turn || board_settled_value[x][y] == turn) || board_full[x - 1][y][0] != 0)) {
				board_settled_value[x - 1][y] = turn;
				settled_check_all_go_left(x - 1, y, board, turn,
						board_settled_value);
			}
		}

		public void settled_check(int board[][], int turn,
				int board_settled_value[][], int board_full[][][]) {
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					board_settled_value[i][j] = nothing;
				}
			}

			if (board[1][1] == turn) {
				board_settled_value[1][1] = turn;
			}
			settled_check_down(1, 1, board, turn, board_settled_value);
			settled_check_right(1, 1, board, turn, board_settled_value);

			for (int i = 2; i <= 4; i++) {
				if (board[i][i] == turn
						&& ((board_settled_value[i - 1][i] == turn || board_settled_value[i + 1][i] == turn) || board_full[i][i][0] != 0)
						&& ((board_settled_value[i][i - 1] == turn || board_settled_value[i][i + 1] == turn) || board_full[i][i][1] != 0)
						&& ((board_settled_value[i - 1][i - 1] == turn || board_settled_value[i + 1][i + 1] == turn) || board_full[i][i][2] != 0)
						&& ((board_settled_value[i - 1][i + 1] == turn || board_settled_value[i + 1][i - 1] == turn) || board_full[i][i][3] != 0)) {
					board_settled_value[i][i] = turn;

				}
				settled_check_all_go_right(i, i, board, turn, board_settled_value);
				settled_check_all_go_down(i, i, board, turn, board_settled_value);
			}
			if (board[1][8] == turn) {
				board_settled_value[1][8] = turn;

			}
			settled_check_up(1, 8, board, turn, board_settled_value);
			settled_check_right(1, 8, board, turn, board_settled_value);
			for (int i = 2; i <= 4; i++) {
				if (board[i][9 - i] == turn
						&& ((board_settled_value[i - 1][9 - i] == turn || board_settled_value[i + 1][9 - i] == turn) || board_full[i][9 - i][0] != 0)
						&& ((board_settled_value[i][8 - i] == turn || board_settled_value[i][10 - i] == turn) || board_full[i][9 - i][1] != 0)
						&& ((board_settled_value[i - 1][8 - i] == turn || board_settled_value[i + 1][10 - i] == turn) || board_full[i][9 - i][2] != 0)
						&& ((board_settled_value[i - 1][10 - i] == turn || board_settled_value[i + 1][8 - i] == turn) || board_full[i][9 - i][3] != 0)) {
					board_settled_value[i][9 - i] = turn;

				}
				settled_check_all_go_right(i, 9 - i, board, turn,
						board_settled_value);
				settled_check_all_go_up(i, 9 - i, board, turn, board_settled_value);
			}
			if (board[8][1] == turn) {
				board_settled_value[8][1] = turn;

			}
			settled_check_down(8, 1, board, turn, board_settled_value);
			settled_check_left(8, 1, board, turn, board_settled_value);
			for (int i = 2; i <= 4; i++) {
				if (board[9 - i][i] == turn
						&& ((board_settled_value[8 - i][i] == turn || board_settled_value[10 - i][i] == turn) || board_full[9 - i][i][0] != 0)
						&& ((board_settled_value[9 - i][i - 1] == turn || board_settled_value[9 - i][i + 1] == turn) || board_full[9 - i][i][1] != 0)
						&& ((board_settled_value[8 - i][i - 1] == turn || board_settled_value[10 - i][i + 1] == turn) || board_full[9 - i][i][2] != 0)
						&& ((board_settled_value[8 - i][i + 1] == turn || board_settled_value[10 - i][i - 1] == turn) || board_full[9 - i][i][3] != 0)) {
					board_settled_value[9 - i][i] = turn;

				}
				settled_check_all_go_left(9 - i, i, board, turn,
						board_settled_value);
				settled_check_all_go_down(9 - i, i, board, turn,
						board_settled_value);
			}
			if (board[8][8] == turn) {
				board_settled_value[8][8] = turn;

			}
			settled_check_up(8, 8, board, turn, board_settled_value);
			settled_check_left(8, 8, board, turn, board_settled_value);
			for (int i = 2; i <= 4; i++) {
				if (board[9 - i][9 - i] == turn
						&& ((board_settled_value[8 - i][9 - i] == turn || board_settled_value[10 - i][9 - i] == turn) || board_full[9 - i][9 - i][0] != 0)
						&& ((board_settled_value[9 - i][8 - i] == turn || board_settled_value[9 - i][10 - i] == turn) || board_full[9 - i][9 - i][1] != 0)
						&& ((board_settled_value[8 - i][8 - i] == turn || board_settled_value[10 - i][10 - i] == turn) || board_full[9 - i][9 - i][2] != 0)
						&& ((board_settled_value[8 - i][10 - i] == turn || board_settled_value[10 - i][8 - i] == turn) || board_full[9 - i][9 - i][3] != 0)) {
					board_settled_value[9 - i][9 - i] = turn;

				}

				settled_check_all_go_left(9 - i, 9 - i, board, turn,
						board_settled_value);
				settled_check_all_go_up(9 - i, 9 - i, board, turn,
						board_settled_value);

			}

		}
}
