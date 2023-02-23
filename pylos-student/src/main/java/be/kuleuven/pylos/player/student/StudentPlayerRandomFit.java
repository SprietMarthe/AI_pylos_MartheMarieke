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
        //board.add(reserveSphere, randomLocation);
        game.moveSphere(reserveSphere, randomLocation);
    }


    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
		/* removeSphere a random sphere */
        PylosSphere sphereToBeRemove = getSphereToBeRemoved(board);
        game.removeSphere(sphereToBeRemove);
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

    // get random sphere that can be removed
    private PylosSphere getSphereToBeRemoved(PylosBoard board) {
        ArrayList<PylosLocation> allRemovableLocations = new ArrayList<>();
        for (PylosLocation bl : board.getLocations()) {
            if (!bl.isUsable() && bl.getSphere().PLAYER_COLOR == this.PLAYER_COLOR) {
                allRemovableLocations.add(bl);
            }
        }
        return allRemovableLocations.size() == 1 ? allRemovableLocations.get(0).getSphere() : allRemovableLocations.get(getRandom().nextInt(allRemovableLocations.size() - 1)).getSphere();
    }








}
