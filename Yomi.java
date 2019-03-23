package reversi;

public class Yomi {
	int yomi(int turn, int myturn, int board[][], int level, int l,
			int skip_previous, int alpha, int beta) {
		// ノードの評価値
		int value = 0;
		// 子ノードから伝播してきた評価値
		int childValue;
		// ミニマックス法で求めた最大の評価値を持つ場所
		int bestX = 0;
		int bestY = 0;

		// System.out.println(level);
		// System.out.println(level);
		// System.out.println(skip_previous);
		// if()
		// System.out.println(level);
		// ゲーム木の末端では盤面評価
		// その他のノードはMIN or MAXで伝播する
		if (level == 0) {
			// 実際は盤面を評価して評価値を決める
			int turn_num = 0;
			int non_turn_num = 0;
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == turn) {
						turn_num++;
					}
					if (board[i][j] == (3 - turn)) {
						non_turn_num++;
					}
				}
			}
			int eva;
			eva = turn_num - non_turn_num;
			if (turn == myturn) {
				return eva;
			}
			if (turn != myturn) {
				return -eva;
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
			int turn_num = 0;
			int non_turn_num = 0;
			for (int i = 1; i <= 8; i++) {
				for (int j = 1; j <= 8; j++) {
					if (board[i][j] == turn) {
						turn_num++;
					}
					if (board[i][j] == (3 - turn)) {
						non_turn_num++;
					}
				}
			}
			int eva;
			eva = turn_num - non_turn_num;

			// System.out.println(eva);

			if (turn == myturn) {

				if (level == l - 1) {
					System.out.println(eva + "  1");
				}

				return eva;
			}
			if (turn != myturn) {

				if (level == l - 1) {
					System.out.println(-eva + "  1'");
				}

				return -eva;
			}
		}

		if (skip_current == 1 && skip_previous == 0) {
			turn = 3 - turn;
			skip_previous = 1;
			value = yomi(turn, myturn, board, level - 1, l, skip_previous,
					alpha, beta);

			if (level == l - 1) {
				System.out.println(value + "  2");
			}

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
						childValue = yomi(t, myturn, b, level - 1, l,
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
								b = clone(board);
								t = turn;

								if (level == l - 1) {
									System.out.println(value + "  3");
								}

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
								b = clone(board);
								t = turn;

								if (level == l - 1) {
									System.out.println(value + "  3'");
								}

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
			System.out.println();
			System.out.println(value);
			return bestX + bestY * 8;
		} else {
			// 子ノードならノードの評価値を返す
			// System.out.println(value);
			if (level == l - 1) {
				System.out.println(value + "   4");
			}

			return value;
		}
	}
}
