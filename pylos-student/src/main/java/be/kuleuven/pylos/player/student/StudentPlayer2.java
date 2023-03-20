package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class StudentPlayer2 extends PylosPlayer {

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

        Action bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, 1, null, true);
        game.moveSphere(bestMove.getSphere(), bestMove.getTo());

    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere); */

        PylosSphere sphereToBeRemove = getSphereToBeRemoved(board);
        game.removeSphere(sphereToBeRemove);
    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere);
         * game.pass() */

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


    /* privates --------------------------------------------------------------------------------------------- */


    /* Minimax for moving a free sphere to a location on the board

    // DONE : adding the paths of adding a reserve sphere to all possible locations
    // TODO : adding the paths of moving to all possible higher squares
    // Huidige error: als een vierkant gemaakt wordt, negeert het algoritme dat nog waardoor die REMOVE_FIRST staat genegeerd wordt
    // Mogelijke te implementeren oplossing: altijd eerst state controleren voor met minimax verder te gaan en op basis daarvan verdere aftakkingen bekijken

     */

    private Action minimax(PylosGameState state, PylosPlayerColor color, PylosBoard board, int depth, Action last, boolean isMax) {
        PylosGameSimulator pylosGameSimulator = new PylosGameSimulator(state, color, board);

        // stop condition
        if (depth == 5 || pylosGameSimulator.getWinner() == color) {
            last.setScores(evaluateBoard(board));
            return last;
        }

        switch (state) {
            case MOVE:
                // Possible free places to move to
                ArrayList<PylosLocation> allPossibleLocations = new ArrayList<>();
                for (PylosLocation bl : board.getLocations()) {
                    if (bl.isUsable()) allPossibleLocations.add(bl);
                }

                if (isMax) {
                    int currentMax = Integer.MIN_VALUE;
                    Action currentMaxAction = null;

                    for (PylosLocation pl : allPossibleLocations) {
                        PylosSphere toMove = board.getReserve(pylosGameSimulator.getColor());
                        Action action = new Action(toMove, pylosGameSimulator.getColor(), toMove.getLocation(), pl);

                        pylosGameSimulator.moveSphere(action.getSphere(), action.getTo());
                        Action bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth+1, action, false);

                        if (currentMax < bestNext.getScores()) {
                            currentMax = bestNext.getScores();
                            action.setScores(currentMax);
                            currentMaxAction = action;
                        }

                        pylosGameSimulator.undoAddSphere(action.getSphere(), state, color);
                    }
                    return currentMaxAction;
                }

                else {
                    int currentMin = Integer.MAX_VALUE;
                    Action currentMinAction = null;

                    for (PylosLocation pl : allPossibleLocations) {
                        PylosSphere toMove = board.getReserve(pylosGameSimulator.getColor());
                        Action action = new Action(toMove, pylosGameSimulator.getColor(), toMove.getLocation(), pl);

                        pylosGameSimulator.moveSphere(action.getSphere(), action.getTo());
                        Action bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth+1, action, true);

                        if (currentMin > bestNext.getScores()) {
                            currentMin = bestNext.getScores();
                            action.setScores(currentMin);
                            currentMinAction = action;
                        }

                        pylosGameSimulator.undoAddSphere(action.getSphere(), state, color);
                    }
                    return currentMinAction;

                }


            case REMOVE_FIRST:
            case REMOVE_SECOND:
                return last;

        }

        return last;
    }

    private int evaluateBoard(PylosBoard board) {

        // evaluation function
        return 1;
    }


    private boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    // get random sphere that can be removed
    private PylosSphere getSphereToBeRemoved(PylosBoard board) {
        ArrayList<PylosSphere> allRemovableLocations = new ArrayList<>();
        for (PylosSphere sphere : board.getSpheres(this)) {
            if (sphere.canRemove()) {
                allRemovableLocations.add(sphere);
            }
        }
        return allRemovableLocations.size() == 1 ? allRemovableLocations.get(0) : allRemovableLocations.get(random.nextInt(allRemovableLocations.size() - 1));
    }

}