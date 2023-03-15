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
import be.kuleuven.pylos.player.student.StudentPlayer2;
import be.kuleuven.pylos.player.student.StudentPlayerBestFit;
import be.kuleuven.pylos.player.student.StudentPlayerRandomFit;

import java.util.Random;

/**
 * Update started on 23/2/2023 by Marieke Beke & Marthe Spriet
 */

public class PylosMain {

	public PylosMain() {

	}

	public void startPerformanceBattles() {
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

		PylosPlayer randomPlayerCodes = new PylosPlayerRandomFit();
//		PylosPlayer randomPlayerCodes = new PylosPlayerMiniMax();
		PylosPlayer randomPlayerStudent = new StudentPlayerRandomFit();

		PylosBoard pylosBoard = new PylosBoard();
		PylosGame pylosGame = new PylosGame(pylosBoard, randomPlayerCodes, randomPlayerStudent, random, PylosGameObserver.CONSOLE_GAME_OBSERVER, PylosPlayerObserver.NONE);

		pylosGame.play();
	}

	public void startBattle() {

//		PylosPlayer playerLight = new StudentPlayer2();
		PylosPlayer playerLight = new StudentPlayerRandomFit();
		PylosPlayer playerDark = new PylosPlayerRandomFit();
		Battle.play(playerLight, playerDark, 100);
	}

	public static void main(String[] args) {

		/* !!! vm argument !!! -ea */

//		new PylosMain().startSingleGame();
		new PylosMain().startBattle();

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
*/
