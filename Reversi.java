package reversi;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

public class Reversi extends Applet implements Runnable {
	// スレッド用
	int count = 1;
	int running = 1;
	Thread th = null;
	// state==1 入力待ち
	// state==2 選択音と画面の遷移の処理中
	// state==3 人対人で駒を置いた時の処理中
	// state==4 コンピュータ対人で駒を置いた時の処理中
	// state==-1 決着がついた
	int state = 1;

	// 画面情報
	// screen==1 メニュー画面
	// screen==2 人対人の対局画面
	// screen==3 コンピュータ対人の対局画面
	int screen = 1;

	// 画像読み込み
	Image img;
	Image img_black;
	Image img_white;
	Image img_nothing;
	Image img_available;
	Graphics OSG;

	// 効果音を読み込む変数の宣言
	AudioClip sound;
	AudioClip sound_select;
	AudioClip sound_cancel;

	// 盤情報
	int[][] board = new int[10][10];

	// 盤の状態
	int nothing = 0;
	int black = 1;
	int black_count = 2;
	int white = 2;
	int white_count = 2;
	int available = 3;
	int available_count;
	int available_count_player;
	int available_count_computer;

	// ターン情報
	int turn = black;
	int skip = 0;

	// 盤の位置の情報
	int width;
	int length;

	// 終了状態の確認と勝者情報
	int finish = 0;
	int winer;
	int finish_final = 0;
	int finish_final_black = 0;

	// AI用変数宣言
	int[][] board_print = new int[10][10];
	int[][] board_value = new int[10][10];
	int[][] board_check = new int[10][10];
	int[][] board_settled_value = new int[10][10];
	int[][][] board_full = new int[10][10][4];
	int min = 401;
	int max = 0;
	int best_length;
	int best_width;
	int turn_count = 4;
	int depth = 0;
	int[][][] current = new int[6][10][10];
	int[] put_length = new int[6];
	int[] put_width = new int[6];
	int[] skip_ai = new int[6];
	int[][][] value = new int[6][10][10];
	int[][] save = new int[10][10];
	int rise = 0;
	
	Kakuteiseki kakutei;

	public void init() {
		// キー待ち開始
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		// 画像の読み込み
		img_black = getImage(getDocumentBase(), "../image/black.png");
		img_white = getImage(getDocumentBase(), "../image/white.png");
		img_nothing = getImage(getDocumentBase(), "../image/nothing.png");
		img_available = getImage(getDocumentBase(), "../image/available.png");
		img = createImage(2000, 2000);
		OSG = img.getGraphics();
		OSG.setColor(Color.black);
		// 効果音の読み込み
		sound = getAudioClip(getDocumentBase(),
				"../music/turning_a_lock1.wav");
		sound_select = getAudioClip(getDocumentBase(),
				"../music/crrect_answer2.wav");
		sound_cancel=getAudioClip(getDocumentBase(),"../music/キャンセル音8.wav");
		// 盤の初期化
		for (int i = 0; i <= 9; i++) {
			for (int j = 0; j <= 9; j++) {
				board[i][j] = nothing;
				board_settled_value[i][j] = nothing;
			}
		}
		board[4][4] = white;
		board[5][5] = white;
		board[4][5] = black;
		board[5][4] = black;
		board[3][4] = available;
		board[4][3] = available;
		board[5][6] = available;
		board[6][5] = available;
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				board_value[i][j] = 0;
				if ((i == 1 && j == 1) || (i == 1 && j == 8)
						|| (i == 8 && j == 1) || (i == 8 && j == 8)) {
					board_value[i][j] = 15;
				}
				if ((i == 2 && j == 2) || (i == 7 && j == 2)
						|| (i == 2 && j == 7) || (i == 7 && j == 7)) {
					board_value[i][j] = -10;
				}
				if ((i == 1 && j == 2) || (i == 2 && j == 1)
						|| (i == 7 && j == 1) || (i == 1 && j == 7)
						|| (i == 7 && j == 8) || (i == 8 && j == 7)
						|| (i == 2 && j == 8) || (i == 8 && j == 2)) {
					board_value[i][j] = -2;
				}
				if ((i == 3 && j == 1) || (i == 1 && j == 3)
						|| (i == 6 && j == 1) || (i == 1 && j == 6)
						|| (i == 8 && j == 6) || (i == 6 && j == 8)
						|| (i == 1 && j == 6) || (i == 3 && j == 8)) {
					board_value[i][j] = 2;
				}
				if (i >= 4 && i <= 5 && j >= 4 && j <= 5) {
					board_value[i][j] = 1;
				}
			}
		}
	}
	// 描画のちらつきを抑える
	public void update(Graphics g) {
		paint(g);
	}
	// スレッド起動
	public void start() {
		if (th == null) {
			th = new Thread(this);
			th.start();
		}
	}

	/***********
	 * キー取得*
	 * *********/
	public void processMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_CLICKED) {
			int X = e.getX();
			int Y = e.getY();
			// メニュー画面で
			if (screen == 1) {
				// プレイヤー×プレイヤーを選択したとき
				if (X <= 510 && X >= 83 && Y <= 200 && Y >= 175) {
					screen = 2;
					state = 2;
				}
				// プレイヤー×コンピュータを選択したとき
				if (X <= 480 && X >= 83 && Y <= 250 && Y >= 225) {
					screen = 3;
					state = 2;
				}
			}
			// 人対人画面で
			if (screen == 2 && state == 1) {
				length = (X - 400) / 50;
				width = (Y - 50) / 50;
				// 置けるところを選択したとき
				if (length >= 0 && length <= 9 && width >= 0 && width <= 9
						&& (board[length][width] == available)) {
					state = 3;
				}
				if (X <= 1150 && X >= 900 && Y <= 200 && Y >= 170) {
					sound_cancel.play();
					state = 1;
					screen = 1;
					finish = 0;
					winer = 0;
					turn_count = 4;
					turn = black;
					black_count = white_count = 2;
					// 盤の初期化
					for (int i = 0; i <= 9; i++) {
						for (int j = 0; j <= 9; j++) {
							board[i][j] = nothing;
							board_settled_value[i][j] = nothing;
							for (int k = 0; k < 4; k++) {
								board_full[i][j][k] = nothing;
							}
						}
					}
					board[4][4] = white;
					board[5][5] = white;
					board[4][5] = black;
					board[5][4] = black;
					board[3][4] = available;
					board[4][3] = available;
					board[5][6] = available;
					board[6][5] = available;
				}
			}
			// 人対コンピュータ画面で
			if (screen == 3 && state == 1) {
				length = (X - 400) / 50;
				width = (Y - 50) / 50;
				// 置けるところを選択したとき
				if (length >= 0 && length <= 9 && width >= 0 && width <= 9
						&& (board[length][width] == available)) {
					state = 4;
				}
				if (X <= 1150 && X >= 900 && Y <= 200 && Y >= 170) {
					
					sound_cancel.play();
					state = 1;
					screen = 1;
					finish = 0;
					winer = 0;
					turn_count = 4;
					turn = black;
					black_count = white_count = 2;

					// 盤の初期化
					for (int i = 0; i <= 9; i++) {
						for (int j = 0; j <= 9; j++) {
							board[i][j] = nothing;
							board_settled_value[i][j] = nothing;
							for (int k = 0; k < 4; k++) {
								board_full[i][j][k] = nothing;
							}
						}
					}
					board[4][4] = white;
					board[5][5] = white;
					board[4][5] = black;
					board[5][4] = black;
					board[3][4] = available;
					board[4][3] = available;
					board[5][6] = available;
					board[6][5] = available;
				}
			}
		}
	}

	/********
	 * 描画 *
	 ********/
	public void paint(Graphics g) {
		OSG.clearRect(0, 0, 2000, 2000);

		// メニュー画面
		if (screen == 1) {
			OSG.setFont(new Font("Serif", Font.BOLD, 30));
			OSG.drawString("<メニュー画面>", 130, 150);
			OSG.drawString("黒 プレイヤー × 白 プレイヤー", 80, 200);
			OSG.drawString("黒 プレイヤー × 白   COMP", 80, 250);
		}

		// プレー画面
		if (screen == 2 || screen == 3) {
			for (int i = 1; i < 9; i++) {
				for (int j = 1; j < 9; j++) {
					if (board[i][j] == black) {
						OSG.drawImage(img_black, (i + 8) * 50, (j + 1) * 50,
								this);
					}
					if (board[i][j] == white) {
						OSG.drawImage(img_white, (i + 8) * 50, (j + 1) * 50,
								this);
					}
					if (board[i][j] == nothing) {
						OSG.drawImage(img_nothing, (i + 8) * 50, (j + 1) * 50,
								this);
					}
					if (board[i][j] == available) {
						OSG.drawImage(img_available, (i + 8) * 50,
								(j + 1) * 50, this);
					}
				}
			}
			OSG.drawString("メニューに戻る⏎", 900, 200);
		}

		// 人対人
		if (screen == 2) {
			if (turn == black && finish == 0 && skip == 0) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("黒のターン（黒" + black_count + "×白" + white_count
						+ "）", 500, 80);
			}
			if (turn == black && finish == 0 && skip == 1) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("黒のターン（白スキップ）（黒" + black_count + "×白"
						+ white_count + "）", 500, 80);
			}
			if (turn == white && finish == 0 && skip == 0) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("白のターン（黒" + black_count + "×白" + white_count
						+ "）", 500, 80);
			}
			if (turn == white && finish == 0 && skip == 1) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("白のターン（黒スキップ)（黒" + black_count + "×白"
						+ white_count + "）", 500, 80);
			}
			if (finish == 1 && winer == black) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("黒の勝ち（黒" + black_count + "×白" + white_count
						+ "）", 500, 80);
			}
			if (finish == 1 && winer == white) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("白の勝ち（黒" + black_count + "×白" + white_count
						+ "）", 500, 80);
			}
			if (finish == 1 && winer == 0) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("引き分け（黒" + black_count + "×白" + white_count
						+ "）", 500, 80);
			}
		}
		// 人対コンピュータ
		if (screen == 3) {
			// テスト
			String str = turn_count + "";
			OSG.drawString(str, 0, 0);
			if (turn == black && finish == 0 && skip == 0) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("黒（あなた）のターン（黒" + black_count + "×白"
						+ white_count + "）", 500, 80);
			}
			if (turn == black && finish == 0 && skip == 1) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("黒（あなた）のターン（白スキップ）（黒" + black_count + "×白"
						+ white_count + "）", 500, 80);
			}
			if (turn == white && finish == 0 && skip == 0) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("白（COMP）のターン（黒" + black_count + "×白"
						+ white_count + "）", 500, 80);
			}
			if (turn == white && finish == 0 && skip == 1) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("白（COMP）のターン（黒スキップ)（黒" + black_count + "×白"
						+ white_count + "）", 500, 80);
			}
			if (finish == 1 && winer == black) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("黒（あなた）の勝ち（黒" + black_count + "×白" + white_count
						+ "）", 500, 80);
			}
			if (finish == 1 && winer == white) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("白（COMP）の勝ち（黒" + black_count + "×白"
						+ white_count + "）", 500, 80);
			}
			if (finish == 1 && winer == 0) {
				OSG.setFont(new Font("Serif", Font.BOLD, 30));
				OSG.drawString("引き分け（黒" + black_count + "×白" + white_count
						+ "）", 500, 80);
			}
		}

		g.drawImage(img, 0, 0, this);
	}

	/********************
	 * 評価関数記入(AI)*
	 ********************/

	public void compute() {
		// ミニマックス法で石を打つ場所を決める
		// 戻ってくる値は bestX+bestY*MASU
		int temp = 0;
		if (turn_count <= 20) {
			temp = minMax(turn, turn, board, 7, 7, skip, Integer.MIN_VALUE,
					Integer.MAX_VALUE);
		}
		if (turn_count >= 21 && turn_count <= 44) {
			temp = minMax(turn, turn, board, 7, 7, skip, Integer.MIN_VALUE,
					Integer.MAX_VALUE);
		}
		if (turn_count >= 45 && turn_count <= 50) {
			temp = minMax(turn, turn, board, 9, 9, skip, Integer.MIN_VALUE,
					Integer.MAX_VALUE);
		}
		if (turn_count >= 51) {
			temp = yomi(turn, turn, board, 15, 15, skip, Integer.MIN_VALUE,
					Integer.MAX_VALUE);
		}
		// 場所を求める
		int x = temp % 8;
		if (x == 0) {
			x = 8;
		}
		int y = temp / 8;
		if (x == 8) {
			y--;
		}

		// System.out.println(x + " " + y);
		// その場所に実際に石を打つ

		board[x][y] = turn;
		// 実際にひっくり返す
		reverse(board, x, y, turn);
		// 手番を変える
		turn = 3 - turn;
		turn_count++;
		skip = 0;

	}


	

	/******
	 * run*
	 ******/
	public void run() {
		while (th == Thread.currentThread()) {
			while (state == 1) {
				try {
					repaint();
					Thread.sleep(500);
				} catch (InterruptedException e) {
					break;
				}
			}

			if (state == 2) {
				try {
					repaint();
					sound_select.play();
					Thread.sleep(500);
				} catch (InterruptedException e) {
					break;
				}
				state = 1;
				continue;
			}

			if (state == 3) {
				try {
					skip = 0;
					board[length][width] = turn;
					repaint();
					sound.play();
					Thread.sleep(250);
					reverse(board, length, width, turn);

					turn = 3 - turn;
					clear_available(board);
					check_available(board, turn);

					System.out.println();
					kakutei.check(board, board_full);
					for (int k = 0; k < 4; k++) {
						for (int i = 1; i <= 8; i++) {
							for (int j = 1; j <= 8; j++) {

								System.out.print(board_full[j][i][k]);

							}
							System.out.println();
						}
						System.out.println(k);
					}
					settled_check(board, 3 - turn, board_settled_value,
							board_full);
					for (int i = 1; i <= 8; i++) {
						for (int j = 1; j <= 8; j++) {
							System.out.print(board_settled_value[j][i]);
						}
						System.out.println();
					}
					System.out.println();

					for (int i = 1; i <= 8; i++) {
						for (int j = 1; j <= 8; j++) {
							System.out.print(board[j][i]);
						}
						System.out.println();
					}
					System.out.println();

					available_count = counter_available(board);
					if (available_count > 0) {
						black_count = counter_black(board);
						white_count = counter_white(board);
						repaint();
						state = 1;
					}
					if (available_count == 0) {
						turn = 3 - turn;
						clear_available(board);
						check_available(board, turn);
						available_count = counter_available(board);
						if (available_count > 0) {
							skip = 1;
							black_count = counter_black(board);
							white_count = counter_white(board);
							repaint();
							state = 1;
						}
						if (available_count == 0) {
							black_count = counter_black(board);
							white_count = counter_white(board);
							if (black_count == white_count) {
								winer = 0;
							}
							if (black_count > white_count) {
								winer = black;
							}
							if (black_count < white_count) {
								winer = white;
							}
							finish = 1;
							repaint();
							state = 1;
						}
					}
				} catch (InterruptedException e) {
					break;
				}

			}

			if (state == 4) {
				try {
					skip = 0;
					board[length][width] = turn;
					turn_count++;
					repaint();
					sound.stop();
					sound.play();
					Thread.sleep(100);
					reverse(board, length, width, turn);

					/*
					 * for (int k = 0; k < 4; k++) { for (int i = 1; i <= 8;
					 * i++) { for (int j = 1; j <= 8; j++) {
					 * 
					 * System.out.print(board_full[j][i][k]);
					 * 
					 * } System.out.println(); } System.out.println(k); }
					 */

					turn = 3 - turn;
					clear_available(board);
					check_available(board, turn);
					available_count_computer = counter_available(board);
					black_count = counter_black(board);
					white_count = counter_white(board);
					repaint();
					Thread.sleep(300);
					int first = 0;
					if (available_count_computer >= 1) {
						first = 1;
						int flag = 1;
						while (flag == 1) {
							turn = white;
							clear_available(board);
							check_available(board, turn);
							available_count_computer = counter_available(board);
							compute();

							/*
							 * Kakuteiseki2.kakutei2(board,board_full); for (int
							 * k = 0; k < 4; k++) { for (int i = 1; i <= 8; i++)
							 * { for (int j = 1; j <= 8; j++) {
							 * 
							 * System.out.print(board_full[j][i][k]);
							 * 
							 * } System.out.println(); } System.out.println(k);
							 * }
							 */
							/*
							 * Kakuteiseki2.kakutei2(board,board_full);
							 * settled_check(board, turn, board_settled_value,
							 * board_full); for (int i = 1; i <= 8; i++) { for
							 * (int j = 1; j <= 8; j++) {
							 * System.out.print(board_settled_value[j][i]); }
							 * System.out.println(); } System.out.println();
							 */
							black_count = counter_black(board);
							white_count = counter_white(board) - 1;
							repaint();
							sound.stop();
							sound.play();
							// reverse(board, best_length, best_width, turn);
							repaint();
							turn = black;
							clear_available(board);
							check_available(board, turn);
							available_count_player = counter_available(board);
							black_count = counter_black(board);
							white_count = counter_white(board);
							repaint();
							// プレイヤーが置けるとき
							if (available_count_player >= 1) {
								flag = 0;
								state = 1;
								repaint();
							}
							// プレイヤーが置けないとき
							if (available_count_player == 0) {
								Thread.sleep(1300);
								turn = white;
								clear_available(board);
								check_available(board, turn);
								available_count_computer = counter_available(board);
								if (available_count_computer >= 1) {
									skip = 1;
									flag = 1;
									black_count = counter_black(board);
									white_count = counter_white(board);
									repaint();
									Thread.sleep(1200);
								}
								if (available_count_computer == 0) {
									flag = 0;
									state = 1;
									finish = 1;
									// black_count,white_countを求めてwinerの判定をし、描画する
									black_count = counter_black(board);
									white_count = counter_white(board);
									if (black_count == white_count) {
										winer = 0;
									}
									if (black_count > white_count) {
										winer = black;
									}
									if (black_count < white_count) {
										winer = white;
									}
									state = 1;
									repaint();
								}

							}

						}
					}
					if (available_count_computer == 0 && first == 0) {
						turn = black;
						clear_available(board);
						check_available(board, turn);
						available_count_player = counter_available(board);
						if (available_count_player >= 1) {
							skip = 1;
							state = 1;
							repaint();
						}
						if (available_count_player == 0) {
							finish = 1;
							// black_count,white_countを求めてwinerの判定をし、描画する
							black_count = counter_black(board);
							white_count = counter_white(board);
							if (black_count == white_count) {
								winer = 0;
							}
							if (black_count > white_count) {
								winer = black;
							}
							if (black_count < white_count) {
								winer = white;
							}
							state = 1;
							repaint();
						}
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}
