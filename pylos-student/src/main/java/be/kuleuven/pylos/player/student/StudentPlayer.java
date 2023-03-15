package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jan on 20/02/2015.
 */
public class StudentPlayer extends PylosPlayer {

	Random random = new Random(1);

	@Override
	public void doMove(PylosGameIF game, PylosBoard board) {
		/* board methods
			* 	PylosLocation[] allLocations = board.getLocations();
			* 	PylosSphere[] allSpheres = board.getSpheres();
			* 	PylosSphere[] mySpheres = board.getSpheres(this);
			* 	PylosSphere myReserveSphere = board.getReserve(this); */

		/* game methods
			* game.moveSphere(myReserveSphere, allLocations[0]); */

		// Hier die tree search functie uitvoeren
		// Nadenken over die evaluatiefunctie



		PylosSphere reserveSphere = board.getReserve(this);
		PylosLocation randomLocation = getRandomFeasibleLocation(board);
		game.moveSphere(reserveSphere, randomLocation);

	}

	@Override
	public void doRemove(PylosGameIF game, PylosBoard board) {
		/* game methods
			* game.removeSphere(mySphere); */
		/* removeSphere a random sphere */
		PylosSphere sphereToBeRemove = getSphereToBeRemoved(board);
		game.removeSphere(sphereToBeRemove);
	}

	@Override
	public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
		/* game methods
			* game.removeSphere(mySphere);
			* game.pass() */


		// Search for removables
		ArrayList<PylosSphere> removableSpheres = new ArrayList<>();
		for (PylosSphere ps : board.getSpheres(this)) {
			if (!ps.isReserve() && !ps.getLocation().hasAbove()) {
				removableSpheres.add(ps);
			}
		}

		// Random decision between remove (if possible) and pass
		if (getRandomBoolean() && removableSpheres.size() > 0) {
			game.removeSphere(removableSpheres.get(0));
		} else game.pass();
	}



	// get random feasible location
	private PylosLocation getRandomFeasibleLocation(PylosBoard board) {
		ArrayList<PylosLocation> allPossibleLocations = new ArrayList<>();
		for (PylosLocation bl : board.getLocations()) {
			if (bl.isUsable()) {
				allPossibleLocations.add(bl);
			}
		}
		return allPossibleLocations.size() == 1 ? allPossibleLocations.get(0) : allPossibleLocations.get(random.nextInt(allPossibleLocations.size() - 1));
	}

	private PylosSphere getSphereToBeRemoved(PylosBoard board) {
		ArrayList<PylosSphere> allRemovableLocations = new ArrayList<>();
		for (PylosSphere sphere : board.getSpheres(this)) {
			if (sphere.canRemove()) {
				allRemovableLocations.add(sphere);
			}
		}
		return allRemovableLocations.size() == 1 ? allRemovableLocations.get(0) : allRemovableLocations.get(random.nextInt(allRemovableLocations.size() - 1));
	}

	private boolean getRandomBoolean() {
		return random.nextBoolean();
	}



}
