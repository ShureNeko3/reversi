package reversi;

public class Min_max {
	public int minMax(int turn, int myturn, int board[][], int level, int l,
			int skip_previous, int alpha, int beta) {
		// ノードの評価値
		int value = 0;
		// 子ノードから伝播してきた評価値
		int childValue;
		// ミニマックス法で求めた最大の評価値を持つ場所
		int bestX = 0;
		int bestY = 0;

		// if()
		// System.out.println(level);
		// ゲーム木の末端では盤面評価
		// その他のノードはMIN or MAXで伝播する
		if (level == 0) {
			// 実際は盤面を評価して評価値を決める
			int eva = 0;
			int turn_num = 0;
			int non_turn_num = 0;
			int eva_board = 0;
			int non_eva_board = 0;
			int settled_num = 0;
			int non_settled_num = 0;

	       kakutei.check(board, board_full);
			settled_check(board, turn, board_settled_value, board_full);

			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == turn) {
						eva_board += board_value[i][j];
						turn_num++;
					}
					if (board[i][j] == (3 - turn)) {
						non_eva_board -= board_value[i][j];
						non_turn_num++;
					}
					if (board[i][j] == available) {
						board[i][j] = nothing;
					}
					if (board_settled_value[i][j] == turn) {
						settled_num++;
					}
				}
			}

			settled_check(board, 3 - turn, board_settled_value, board_full);
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board_settled_value[i][j] == 3 - turn) {
						non_settled_num++;
					}
				}
			}

			int ava_count = check_available(board, turn);
			if (turn_count <= 12) {
				eva = 5 * ava_count - 2 * (turn_num - non_turn_num);
				// System.out.println("1   "+4*ava_count+" "+3*(-turn_num+non_turn_num));
			}
			if (turn_count >= 13 && turn_count <= 28) {
				eva = 4 * ava_count - 1 * (turn_num - non_turn_num) + eva_board
						+ non_eva_board + 2 * (settled_num - non_settled_num);
				// System.out.println("2   "+5*ava_count+" "+2*(-turn_num+non_turn_num)+" "+eva_board+" "+non_eva_board);
			}
			if (turn_count >= 29 && turn_count <= 42) {
				eva = 4 * ava_count + eva_board + non_eva_board - 0
						* (turn_num - non_turn_num) + 4
						* (settled_num - non_settled_num);
				// System.out.println("3   "+5*ava_count+" "+eva_board+" "+non_eva_board+" "+(settled_num-non_settled_num));
			}
			if (turn_count >= 43 && turn_count <= 50) {
				eva = 5 * ava_count + eva_board + non_eva_board + 5
						* (settled_num - non_settled_num);
				// System.out.println("4   "+4*ava_count+" "+eva_board+" "+non_eva_board+" "+2*(settled_num-non_settled_num));
			}
			// System.out.println(eva + "  " + 4 * ava_count + "  " + 2
			// * (turn_num - non_turn_num) + "  " + eva_board + "  "
			// + non_eva_board);

			if (turn == myturn) {
				return eva;
			}
			if (turn != myturn) {
				return -1 * eva;
			}
		}

		int skip_current = 0;
		if (puttable(board, turn) == false) {
			skip_current = 1;
		}
		if (puttable(board, turn) == true) {
			skip_current = 0;
		}

		if (skip_current == 1 && skip_previous == 1) {
			int turn_count = counter(board, turn);
			int non_turn_count = counter(board, 3 - turn);
			int eva;
			eva = turn_count - non_turn_count;
			if ((turn == myturn && eva >= 0) || (turn != myturn && eva < 0)) {
				return 1000;
			}
			if (turn == myturn && eva < 0 || (turn != myturn && eva >= 0)) {
				return -1000;
			}
		}
		if (skip_current == 1 && skip_previous == 0) {
			turn = 3 - turn;
			skip_previous = 1;
			value = minMax(turn, myturn, board, level - 1, l, skip_previous,
					alpha, beta);
			return value;
		}

		if (skip_current == 0) {
			if (turn == myturn) {
				// AIの手番では最大の評価値を見つけたいので最初に最小値をセットしておく
				value = Integer.MIN_VALUE;
			} else {
				// プレイヤーの手番では最小の評価値を見つけたいので最初に最大値をセットしておく
				value = Integer.MAX_VALUE;
			}
			int b[][] = clone(board);
			int t = turn;

			// 打てるところはすべて試す（試すだけで実際には打たない）
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (puttable(b, t, i, j)) {
						// System.out.println(i + "," + j);
						// 試しに打ってみる
						b[i][j] = t;
						// ひっくり返す
						reverse(b, i, j, t);

						// 手番を変える
						t = 3 - t;
						// 子ノードの評価値を計算（再帰）
						skip_previous = 0;
						childValue = minMax(t, myturn, b, level - 1, l,
								skip_previous, alpha, beta);

						// 子ノードとこのノードの評価値を比較する
						if (turn == myturn) {
							// AIのノードなら子ノードの中で最大の評価値を選ぶ
							if (childValue > value) {
								value = childValue;
								alpha = value;
								bestX = i;
								bestY = j;
							}
							if (value > beta) {
								t = turn;
								b = clone(board);
								return value;
							}
						} else {
							// プレイヤーのノードなら子ノードの中で最小の評価値を選ぶ
							if (childValue < value) {
								value = childValue;
								beta = value;
								bestX = i;
								bestY = j;
							}
							if (value < alpha) {
								t = turn;
								b = clone(board);
								return value;
							}
						}
						b = clone(board);
						t = turn;
					}
				}
			}
		}

		if (level == l) {
			// ルートノードなら最大評価値を持つ場所を返す
			// System.out.println(value);
			return bestX + bestY * 8;
		} else {
			// 子ノードならノードの評価値を返す
			return value;
		}
	}
}
