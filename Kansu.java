package reversi;

public class Kansu {
	// リバース処理
		public void rev(int board[][], int i, int j, int turn, int dx, int dy,
				int key) {
			i = i + dx;
			j = j + dy;
			if (board[i][j] == (3 - turn)) {
				key++;
				rev(board, i, j, turn, dx, dy, key);
			}
			if (key >= 1 && board[i][j] == turn) {
				while (key >= 1) {
					board[i - dx][j - dy] = turn;
					key--;
				}
			}
		}

		public void reverse(int board[][], int i, int j, int turn) {
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (dx == 0 && dy == 0)
						continue;

					int key = 0;
					rev(board, i, j, turn, dx, dy, key);
				}
			}
		}

		public void clear_available(int board[][]) {
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == available) {
						board[i][j] = nothing;
					}
				}
			}
		}

		// availableの位置の取得
		public void che_ava(int board[][], int i, int j, int turn, int dx, int dy,
				int key) {
			i = i + dx;
			j = j + dy;
			if (board[i][j] == (3 - turn)) {
				key++;
				che_ava(board, i, j, turn, dx, dy, key);
			}
			if (key >= 1 && board[i][j] == turn) {
				board[i - dx * (key + 1)][j - dy * (key + 1)] = available;
			}
		}

		public int check_available(int board[][], int turn) {
			int count = 0;
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == nothing) {
						boolean flag = false;
						for (int dx = -1; dx <= 1; dx++) {
							for (int dy = -1; dy <= 1; dy++) {
								if (dx == 0 && dy == 0)
									continue;
								int key = 0;
								che_ava(board, i, j, turn, dx, dy, key);
								if (board[i][j] == available) {
									flag = true;
									count++;
									break;
								}
							}
							if (flag)
								break;
						}
					}

				}

			}
			return count;
		}

		// availableの数の取得
		public int counter_available(int board[][]) {
			int counter = 0;

			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == available) {
						counter++;
					}
				}
			}
			return counter;
		}

		public boolean puttable(int board[][], int turn, int x, int y) {
			clear_available(board);
			check_available(board, turn);
			return board[x][y] == available;
		}

		public boolean puttable(int board[][], int turn) {
			int count = 0;
			clear_available(board);
			count = check_available(board, turn);
			if (count >= 1) {
				return true;
			} else {
				return false;
			}
		}

		public int counter(int board[][], int turn) {
			int counter = 0;

			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == turn) {
						counter++;
					}
				}
			}
			return counter;
		}

		// blackの数の取得
		public int counter_black(int board[][]) {
			int counter = 0;

			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == black) {
						counter++;
					}
				}
			}
			return counter;
		}

		// whiteの数の取得
		public int counter_white(int board[][]) {
			int counter = 0;

			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == white) {
						counter++;
					}
				}
			}
			return counter;
		}

		public void copy(int save[][], int board[][]) {
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					save[i][j] = board[i][j];
				}
			}
		}

		public int[][] clone(int board[][]) {
			int[][] ret = new int[10][10];
			copy(ret, board);
			return ret;
		}
}
