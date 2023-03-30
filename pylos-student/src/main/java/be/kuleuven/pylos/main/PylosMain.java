package be.kuleuven.pylos.main;

import be.kuleuven.pylos.battle.Battle;
import be.kuleuven.pylos.player.PylosPlayer;
import be.kuleuven.pylos.player.codes.PylosPlayerBestFit;
import be.kuleuven.pylos.player.codes.PylosPlayerMiniMax;
import be.kuleuven.pylos.player.student.*;

import java.io.FileWriter;
import java.io.IOException;
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

	/*public void startSingleGame() {

		Random random = new Random(0);

		PylosPlayer randomPlayerCodes = new PylosPlayerMiniMax(3);
		PylosPlayer randomPlayerStudent = new StudentPlayer();

		PylosBoard pylosBoard = new PylosBoard();
		PylosGame pylosGame = new PylosGame(pylosBoard, randomPlayerCodes, randomPlayerStudent, random, PylosGameObserver.CONSOLE_GAME_OBSERVER, PylosPlayerObserver.NONE);

		pylosGame.play();
	}*/

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
//									PylosPlayer playerLight = new StudentPlayer(diepteStudent, k, l, m, n, o, j);
//									PylosPlayer playerDark = new PylosPlayerMiniMax(diepteCodes);
//									double[] wins = Battle.play(playerLight, playerDark, 100);
//
//									if (bestWin < wins[0]) {
//										bestWin = wins[0];
//
//										String values = wins[0] + " " + wins[1] + " " + wins[2];
//										stringBestWin = diepteStudent + " " + diepteCodes
//												+ " " + k
//												+ " " + l
//												+ " " + m
//												+ " " + n
//												+ " " + o
//												+ " " + (double) j
//												+ " " + values
//												+ " " + "\n";
//									}
								}
							}
						}
					}
				}
			}
			fw.write(stringBestWin);
			fw.close();
		}
		else{
//			PylosPlayer playerLight = new StudentPlayerBestFit();
			PylosPlayer playerLight = new StudentPlayerBestFitFixed();
//			PylosPlayer playerLight = new StudentPlayerRandomFit();

//			PylosPlayer playerDark = new StudentPlayer3(3);
//			PylosPlayer playerDark = new PylosPlayerMiniMax(5);
//			PylosPlayer playerDark = new PylosPlayerRandomFit();
//			PylosPlayer playerDark = new PylosPlayerMiniMax(3);
			PylosPlayer playerDark = new PylosPlayerBestFit();

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
/*
Pylos:
	https://github.com/Diab0lix/Pylos/blob/7f971d82e30c5f499d7ebc93911e71a6ec9abd7d/pylos.py#L268
	https://github.com/stanislavfort/deepPylos/blob/48ab8b26d8abf81e700c031cd425f4d9475a141f/pylos-game.py#L533


Waarom Alfa-beta
	The minimax
algorithm is effective but impractical in practice. In an
actual game, the algorithm cannot search deeper than
two turns/layers ahead using minimax without incurring
significant delays in game play. This is because it evaluates
many subtrees that can be ignored. To optimize the look
ahead search, we used alpha-beta pruning, which causes the
algorithm to quit evaluating branches that result in better
opponent scores since those branches have no effect on the
final outcome. This brings down the run time significantly


Beter:
	Monte Carlo Tree Search (MCTS)



Lerende speler:
	Meest gebruikte taal: Python

Neural Network opstarten
	https://www.infoworld.com/article/3685569/how-to-build-a-neural-network-in-java.html
 */