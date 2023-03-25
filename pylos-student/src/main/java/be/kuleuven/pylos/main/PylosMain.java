package be.kuleuven.pylos.main;

import be.kuleuven.pylos.battle.Battle;
import be.kuleuven.pylos.game.PylosBoard;
import be.kuleuven.pylos.game.PylosGame;
import be.kuleuven.pylos.game.PylosGameObserver;
import be.kuleuven.pylos.player.PylosPlayer;
import be.kuleuven.pylos.player.PylosPlayerObserver;
import be.kuleuven.pylos.player.codes.PylosPlayerBestFit;
import be.kuleuven.pylos.player.codes.PylosPlayerMiniMax;
import be.kuleuven.pylos.player.codes.PylosPlayerRandomFit;
import be.kuleuven.pylos.player.student.StudentPlayer;
import be.kuleuven.pylos.player.student.StudentPlayerBestFit;
import be.kuleuven.pylos.player.student.StudentPlayerRandomFit;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Update started on 23/2/2023 by Marieke Beke & Marthe Spriet
 */

public class PylosMain {

	public PylosMain() {

	}

	public void startPerformanceBattles() throws IOException {
		Random random = new Random(0);
		PylosPlayer[] players = new PylosPlayer[]{new PylosPlayerBestFit(), new PylosPlayerMiniMax(2), new PylosPlayerMiniMax(5), new PylosPlayerMiniMax(8)};

		int[] wins = new int[players.length];
		for (int i = 0; i < players.length; i++) {
			PylosPlayer player = new StudentPlayerBestFit();
			PylosPlayer playerDark = players[i];
			double[] results = Battle.play(player, playerDark, 1000);
			wins[i] = (int) Math.round(results[0] * 100);
		}

		for (int win : wins) {
			System.out.print(win + "\t");
		}
	}

	public void startSingleGame() {

		Random random = new Random(0);

		PylosPlayer randomPlayerCodes = new PylosPlayerMiniMax(3);
		PylosPlayer randomPlayerStudent = new StudentPlayer();

		PylosBoard pylosBoard = new PylosBoard();
		PylosGame pylosGame = new PylosGame(pylosBoard, randomPlayerCodes, randomPlayerStudent, random, PylosGameObserver.CONSOLE_GAME_OBSERVER, PylosPlayerObserver.NONE);

		pylosGame.play();
	}

	public void startBattle(boolean normal) throws IOException {
		double startTime = System.currentTimeMillis();
		if (!normal) {
			String path = "Experiments\\Exp.txt";
			FileWriter fw = new FileWriter(path, true);
			int diepteStudent = 2;
			int diepteCodes = 2;
			double bestWin = 0;
			String stringBestWin = "";
			for (int j = 1; j < 4; j++) {
				for (int k = 1; k < 3; k++) {
					for (int l = 1; l < 4; l++) {
						for (int m = 1; m < 4; m++) {
							for (int n = 1; n < 4; n++) {
								for (int o = 1; o < 3; o++) {
									PylosPlayer playerLight = new StudentPlayer(diepteStudent, k, l, m, n, o, j);
									PylosPlayer playerDark = new PylosPlayerMiniMax(diepteCodes);
									double[] wins = Battle.play(playerLight, playerDark, 100);

									if (bestWin < wins[0]) {
										bestWin = wins[0];

										String values = wins[0] + " " + wins[1] + " " + wins[2];
										stringBestWin = diepteStudent + " " + diepteCodes
												+ " " + k
												+ " " + l
												+ " " + m
												+ " " + n
												+ " " + o
												+ " " + (double) j
												+ " " + values
												+ " " + "\n";
									}
								}
							}
						}
					}
				}
			}
//					int factorOwnReserveSpheres = 1;
//					int factorThreeOfOwnInSquare = 1;
//					int factorFourOfOwnInSquare = 1;
//					int factorCompleteSquare = 1;
//					int factorTopIsOwnSphere = 1;
//					PylosPlayer playerLight = new StudentPlayer(maxDepth, factorOwnReserveSpheres, factorThreeOfOwnInSquare, factorFourOfOwnInSquare, factorCompleteSquare, factorTopIsOwnSphere, factorOwnAndOther);
////		PylosPlayer playerLight = new StudentPlayerRandomFit();
//					for (int s = 1; s < 6; s++) {
//						PylosPlayer playerDark = new PylosPlayerMiniMax(s);
////		PylosPlayer playerDark = new PylosPlayerRandomFit();
////		PylosPlayer playerDark = new PylosPlayerMiniMax(3);
////		PylosPlayer playerDark = new PylosPlayerBestFit();
//						double[] wins = Battle.play(playerLight, playerDark, 100);
//
//						String values = wins[0] + " " + wins[1] + " " + wins[2];
//						fw.write(maxDepth + " " + s
//								+ " " + factorOwnReserveSpheres
//								+ " " + factorThreeOfOwnInSquare
//								+ " " + factorFourOfOwnInSquare
//								+ " " + factorCompleteSquare
//								+ " " + factorTopIsOwnSphere
//								+ " " + factorOwnAndOther
//								+ " " + values + " " + "\n");
//					}
			fw.write(stringBestWin);
			fw.close();
		}
		else{
			PylosPlayer playerLight = new StudentPlayer();
//			PylosPlayer playerLight = new StudentPlayerRandomFit();

			PylosPlayer playerDark = new PylosPlayerMiniMax(2);
//			PylosPlayer playerDark = new PylosPlayerRandomFit();
//			PylosPlayer playerDark = new PylosPlayerMiniMax(3);
//			PylosPlayer playerDark = new PylosPlayerBestFit();

			Battle.play(playerLight, playerDark, 100);
		}
		double playTime = System.currentTimeMillis();
		System.out.println("Time to run: " + (playTime - startTime)/1000);
	}

	public static void main(String[] args) throws IOException {

		/* !!! vm argument !!! -ea */

//		new PylosMain().startSingleGame();
		new PylosMain().startBattle(true);

	}

}

/*
        Action klasse: sphere, color, from, to (Location), children, scores

        Gebruik PylosGameIF om functies te vinden



        9/3
        Ongeveer: 1 seconde per game
        Na simulatie moet je de staat weer herstellen
        	sim oproepen in doMove


        Klasse action, action type (add, remove first/second, move)
        Elke action kan zijn eigen ook reversen
        monte carlo tree search

        Verschil met andere spelers: hoe evalueer je bord


ev
	1 voor bord -> hoe goed voor u
	Door simulatie


	bestFit: >85%
	MinMax: >50%

	nuttig: profiler
		zien waar cpu tijd spendeert
		gaan naar visualVM
			> Sampler > Hot spots ( per methode )

	2 methoden: minimax voor speler ligth en dan ook voor speler dark



	Hoe via AI:
		NN definiëren + evaluatie + parameters
		door neuraal netwerk sturen + trainen
		nu niet self learning


	Lijst met alle mogelijke acties en deze steeds opvragen voor simulatie
*/
