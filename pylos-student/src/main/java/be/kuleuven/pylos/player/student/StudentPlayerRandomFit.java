package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.PylosBoard;
import be.kuleuven.pylos.game.PylosGameIF;
import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosSphere;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;
/*
        Action klasse: sphere, color, from, to (Location), children, scores

        Gebruik PylosGameIF om functies te vinden

*/
/**
 * Created by Ine on 5/05/2015.
 */
public class StudentPlayerRandomFit extends PylosPlayer{

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
		/* add a reserve sphere to a feasible random location */
        PylosSphere reserveSphere = board.getReserve(this);
        PylosLocation randomLocation = getRandomFeasibleLocation(board);
        board.move(reserveSphere, randomLocation);
    }


    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
		/* removeSphere a random sphere */

    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
		/* always pass */
        game.pass();
    }





    // get random feasible location
    private PylosLocation getRandomFeasibleLocation(PylosBoard board) {
        ArrayList<PylosLocation> allPossibleLocations = new ArrayList<>();
        for (PylosLocation bl : board.getLocations()) {
            if (bl.isUsable()) {
                allPossibleLocations.add(bl);
            }
        }
        return allPossibleLocations.size() == 1 ? allPossibleLocations.get(0) : allPossibleLocations.get(getRandom().nextInt(allPossibleLocations.size() - 1));
    }










}
