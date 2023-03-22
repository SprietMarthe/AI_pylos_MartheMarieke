package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;
import java.util.Random;


// TODO : create the children for remove first
// TODO : create the children for remove second
// TODO : construct evaluation function
// TODO : extra --> alfa beta pruning

public class StudentPlayer2 extends PylosPlayer {

    Random random = new Random(1);
    int maxDepth = 3;

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
        /* board methods
         * 	PylosLocation[] allLocations = board.getLocations();
         * 	PylosSphere[] allSpheres = board.getSpheres();
         * 	PylosSphere[] mySpheres = board.getSpheres(this);
         * 	PylosSphere myReserveSphere = board.getReserve(this); */

        /* game methods
         * game.moveSphere(myReserveSphere, allLocations[0]); */

        Action startAction = new Action();

        Action bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction);
        game.moveSphere(bestMove.getSphere(), bestMove.getTo());

    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere); */

        Action startAction = new Action();

        Action bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction);
        PylosSphere sphereToBeRemoved = bestMove.getSphere();

        game.removeSphere(sphereToBeRemoved);
    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere);
         * game.pass() */

        Action startAction = new Action();

        Action bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction);
        if (bestMove.getSphere() == null) game.pass();
        else {
            PylosSphere sphereToBeRemoved = bestMove.getSphere();
            game.removeSphere(sphereToBeRemoved);
        }

    }


    /* privates --------------------------------------------------------------------------------------------- */


     // Minimax recursive function
    private Action minimax(PylosGameState state, PylosPlayerColor color, PylosBoard board, int depth, Action last) {
        PylosGameSimulator pylosGameSimulator = new PylosGameSimulator(state, color, board);

        // stop condition
        if (depth == 0 || state == PylosGameState.COMPLETED) {
            last.setScores(evaluateBoard(board));
            return last;
        }

        giveLastActionChildren(state, color, board, last);

        // Maximize if color is color of this player
        if (color == this.PLAYER_COLOR) {
            int bestValue = Integer.MIN_VALUE;
            Action currentMaxAction = null;

            for (Action a : last.getChildren()) {

                doSimulation(pylosGameSimulator, a);

                Action bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, a);
                if (bestValue < bestNext.getScores()) {
                    bestValue = bestNext.getScores();
                    currentMaxAction = a;
                }

                undoSimulation(pylosGameSimulator, a);
            }

            return currentMaxAction;
        }

        // Minimize if color is color of other player
        else {
            int bestValue = Integer.MAX_VALUE;
            Action currentMinAction = null;

            for (Action a : last.getChildren()) {

                doSimulation(pylosGameSimulator, a);

                Action bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, a);
                if (bestValue > bestNext.getScores()) {
                    bestValue = bestNext.getScores();
                    currentMinAction = a;
                }

                undoSimulation(pylosGameSimulator, a);
            }

            return currentMinAction;
        }
    }


    private void giveLastActionChildren(PylosGameState state, PylosPlayerColor color, PylosBoard board, Action action) {

        // Add all the children of last action in case of MOVE
        if (state == PylosGameState.MOVE) {

            // Possible top square places to move to
            ArrayList<PylosLocation> allUsableTopLocations = getUsableTopLocations(board);

            // All paths of board spheres to a higher square
            for (PylosLocation to : allUsableTopLocations) {
                for (PylosSphere sphere : board.getSpheres(color)) {
                    if (!sphere.isReserve() && sphere.canMoveTo(to)) {
                        PylosLocation from = sphere.getLocation();
                        Action child = new Action(sphere, ActionType.MOVE, color, from, to);
                        action.addChild(child);
                    }
                }
            }


            // Possible free places to move to
            ArrayList<PylosLocation> allUsableLocations = getUsableLocations(board);

            // All paths of reserve spheres to the board
            PylosSphere sphereToMove = board.getReserve(color);
            for (PylosLocation to : allUsableLocations) {
                PylosLocation from = sphereToMove.getLocation();
                Action child = new Action(sphereToMove, ActionType.ADD, color, from, to);
                action.addChild(child);
            }

        }

        // Add all the children of last action in case of REMOVE_FIRST
        else if (state == PylosGameState.REMOVE_FIRST) {
            PylosSphere toRemove = getSphereToBeRemoved(board, color);
            action.addChild(new Action(toRemove, ActionType.REMOVE_FIRST, color, toRemove.getLocation(), null));
            // TODO
        }

        // Add all the children of last action in case of REMOVE_SECOND
        else {
            action.addChild(new Action(null, ActionType.PASS, color, null, null));
            // TODO
        }
    }


    private void doSimulation(PylosGameSimulator pylosGameSimulator, Action a) {

        switch (a.getActionType()) {

            case MOVE:
            case ADD:
                pylosGameSimulator.moveSphere(a.getSphere(), a.getTo());
                break;

            case REMOVE_FIRST:
            case REMOVE_SECOND:
                pylosGameSimulator.removeSphere(a.getSphere());
                break;

            case PASS:
                pylosGameSimulator.pass();
        }
    }

    private void undoSimulation(PylosGameSimulator pylosGameSimulator, Action a) {

        switch (a.getActionType()) {

            case MOVE:
                pylosGameSimulator.undoMoveSphere(a.getSphere(), a.getFrom(), PylosGameState.MOVE, a.getColor());
                break;

            case ADD:
                pylosGameSimulator.undoAddSphere(a.getSphere(), PylosGameState.MOVE, a.getColor());
                break;

            case REMOVE_FIRST:
                pylosGameSimulator.undoRemoveFirstSphere(a.getSphere(), a.getFrom(), PylosGameState.REMOVE_FIRST, a.getColor());
                break;

            case REMOVE_SECOND:
                pylosGameSimulator.undoRemoveSecondSphere(a.getSphere(), a.getFrom(), PylosGameState.REMOVE_SECOND, a.getColor());
                break;

            case PASS:
                pylosGameSimulator.undoPass(PylosGameState.REMOVE_SECOND, a.getColor());
        }
    }


    // Evaluation function
    private int evaluateBoard(PylosBoard board) {

        return 1;
    }


    // Get random sphere that can be removed
    private PylosSphere getSphereToBeRemoved(PylosBoard board, PylosPlayerColor color) {
        ArrayList<PylosSphere> allRemovableLocations = new ArrayList<>();
        for (PylosSphere sphere : board.getSpheres(color)) {
            if (sphere.canRemove()) {
                allRemovableLocations.add(sphere);
            }
        }
        return allRemovableLocations.size() == 1 ? allRemovableLocations.get(0) : allRemovableLocations.get(random.nextInt(allRemovableLocations.size() - 1));
    }

    // Get all usable locations to move a sphere to
    private ArrayList<PylosLocation> getUsableLocations(PylosBoard board) {

        ArrayList<PylosLocation> usableLocations =  new ArrayList<>();
        for (PylosLocation bl : board.getLocations()) {
            if (bl.isUsable()) usableLocations.add(bl);
        }

        return usableLocations;
    }

    // Get all usable top locations to move a sphere to
    private ArrayList<PylosLocation> getUsableTopLocations(PylosBoard board) {

        ArrayList<PylosLocation> usableTopLocations = new ArrayList<>();
        for (PylosSquare ps : board.getAllSquares()) {
            if (ps.isSquare()) usableTopLocations.add(ps.getTopLocation());
        }

        return usableTopLocations;
    }


}