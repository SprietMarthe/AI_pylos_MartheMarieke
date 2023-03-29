package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;


public class StudentPlayer3 extends PylosPlayer {

    int maxDepth = 2;
    int factorOwnReserveSpheres = 2;
    int factorThreeOfOwnInSquare = 2;
    int factorFourOfOwnInSquare = 1;
    int factorCompleteSquare = 1;
    int factorTopIsOwnSphere = 2;
    double factorOwnAndOther = 3;

    public StudentPlayer3() {
    }

    public StudentPlayer3(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
        /* board methods
         * 	PylosLocation[] allLocations = board.getLocations();
         * 	PylosSphere[] allSpheres = board.getSpheres();
         * 	PylosSphere[] mySpheres = board.getSpheres(this);
         * 	PylosSphere myReserveSphere = board.getReserve(this); */

        /* game methods
         * game.moveSphere(myReserveSphere, allLocations[0]); */


        Action bestMove = takeAction(game.getState(), this.PLAYER_COLOR, board, maxDepth);
        game.moveSphere(bestMove.getSphere(), bestMove.getTo());

    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere); */

        Action bestMove =  takeAction(game.getState(), this.PLAYER_COLOR, board, maxDepth);
        PylosSphere sphereToBeRemoved = bestMove.getSphere();

        game.removeSphere(sphereToBeRemoved);
    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere);
         * game.pass() */

        Action startAction = new Action();

        Action bestMove = takeAction(game.getState(), this.PLAYER_COLOR, board, maxDepth);
        if (bestMove.getSphere() == null) game.pass();
        else {
            PylosSphere sphereToBeRemoved = bestMove.getSphere();
            game.removeSphere(sphereToBeRemoved);
        }

    }



    /* privates --------------------------------------------------------------------------------------------- */

    private Action takeAction(PylosGameState state, PylosPlayerColor color, PylosBoard board, int maxDepth) {
        PylosGameSimulator pylosGameSimulator = new PylosGameSimulator(state, color, board);
        ArrayList<Action> actionList = giveLastActionChildren(state, color, board);
        int[] values = new int[actionList.size()];
        int index = 0;
        for (Action action: actionList) {
            int value = minimax(pylosGameSimulator, state, this.PLAYER_COLOR, board, maxDepth, action, Integer.MIN_VALUE, Integer.MAX_VALUE);
            values[index++] = value;
        }
        int indexMax = 0;
        for (int i = 0; i < values.length; i++)
            indexMax = values[i] > values[indexMax] ? i : indexMax;

        return actionList.get(indexMax);
    }

    // Minimax recursive function
    private int minimax(PylosGameSimulator pylosGameSimulator, PylosGameState state, PylosPlayerColor color, PylosBoard board, int depth, Action last, int alfa, int beta) {

        doSimulation(pylosGameSimulator, last);

        // stop condition
        if (depth == 0 || state == PylosGameState.COMPLETED) {
            undoSimulation(pylosGameSimulator, last);
            if (color == this.PLAYER_COLOR)
                return evaluateBoard(board, color);
            else
                return evaluateBoard(board, color.other());
        }
        else if (depth > 0) {
            ArrayList<Action> children = giveLastActionChildren(state, pylosGameSimulator.getColor(), board);
            if (color == this.PLAYER_COLOR) {
                int bestMax = Integer.MIN_VALUE;
                for (Action action : children) {
                    int value = minimax(pylosGameSimulator, pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, action, alfa, beta);
                    if (value > beta){
                        undoSimulation(pylosGameSimulator, last);
                        return value;
                    }
                    alfa = Math.max(alfa, value);
                    if (beta <= alfa){
                        undoSimulation(pylosGameSimulator, last);
                        return value;
                    }
                    bestMax = Math.max(value, bestMax);
                }
                undoSimulation(pylosGameSimulator, last);
                return bestMax;
            }
            else {
                int bestMin = Integer.MAX_VALUE;
                for (Action action : children) {
                    int value = minimax(pylosGameSimulator, pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, action, alfa, beta);
                    if (value < alfa){
                        undoSimulation(pylosGameSimulator, last);
                        return value;
                    }
                    beta = Math.min(beta, value);
                    if (beta <= alfa){
                        undoSimulation(pylosGameSimulator, last);
                        return value;
                    }
                    bestMin = Math.min(value, bestMin);
                }
                undoSimulation(pylosGameSimulator, last);
                return bestMin;
            }
        }
        if (color == this.PLAYER_COLOR) {
            undoSimulation(pylosGameSimulator, last);
            return Integer.MAX_VALUE;
        }
        else{
            undoSimulation(pylosGameSimulator, last);
            return Integer.MIN_VALUE;
        }
    }

    // Generate all the possible children actions of last action
    private ArrayList<Action> giveLastActionChildren(PylosGameState state, PylosPlayerColor color, PylosBoard board) {
        ArrayList<Action> actionArrayList = new ArrayList<>();
        switch (state) {
            case MOVE:
                // possible top square places to move to
                ArrayList<PylosLocation> allUsableTopLocations = getUsableTopLocations(board);

                // all paths of board spheres to a higher square
                for (PylosLocation to : allUsableTopLocations) {
                    for (PylosSphere sphere : board.getSpheres(color)) {
                        if (!sphere.isReserve() && sphere.canMoveTo(to)) {
                            PylosLocation from = sphere.getLocation();
                            Action child = new Action(sphere, ActionType.MOVE, color, from, to);
                            actionArrayList.add(child);
                        }
                    }
                }

                // possible free places to add to
                ArrayList<PylosLocation> allUsableLocations = getUsableLocations(board);

                // all paths of reserve spheres to the board
                if (board.getReservesSize(color) > 0) {
                    PylosSphere sphereToMove = board.getReserve(color);
                    for (PylosLocation to : allUsableLocations) {
                        PylosLocation from = sphereToMove.getLocation();
                        Action child = new Action(sphereToMove, ActionType.ADD, color, from, to);
                        actionArrayList.add(child);
                    }
                }
                break;

            case REMOVE_FIRST:

                // possible spheres of playercolor that can move to reserve
                ArrayList<PylosSphere> allThatCanBeRemoved = getCanBeRemoved(board, color);
                for (PylosSphere sphere : allThatCanBeRemoved) {
                    Action child = new Action(sphere, ActionType.REMOVE_FIRST, color, sphere.getLocation(), null);
                    actionArrayList.add(child);
                }

                break;

            case REMOVE_SECOND:

                // action pass
                actionArrayList.add(new Action(null, ActionType.PASS, color, null, null));

                // possible spheres of playercolor that can move to reserve
                allThatCanBeRemoved = getCanBeRemoved(board, color);
                for (PylosSphere sphere : allThatCanBeRemoved) {
                    Action child = new Action(sphere, ActionType.REMOVE_SECOND, color, sphere.getLocation(), null);
                    actionArrayList.add(child);
                }
        }
        return actionArrayList;
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
    private int evaluateBoard(PylosBoard board, PylosPlayerColor color) {

        // own reserve spheres
        int score = board.getReservesSize(color) * factorOwnReserveSpheres;

        // other reserve spheres
        score -= board.getReservesSize(color.other()) * factorOwnReserveSpheres * factorOwnAndOther;

        // squares
        PylosSquare[] allSquares = board.getAllSquares();
        for (PylosSquare square : allSquares) {
            if (square.getInSquare(color) == 4) score += factorFourOfOwnInSquare;
            else if (square.getInSquare(color.other()) == 3 && square.getInSquare(color) == 1) score += factorFourOfOwnInSquare*factorOwnAndOther;
            else if (square.getInSquare() == 4) score -= factorCompleteSquare;
            else if (square.getInSquare(color) == 3 && square.getInSquare(color.other()) == 0) score += factorThreeOfOwnInSquare;
            else if (square.getInSquare(color.other()) == 3 && square.getInSquare(color) == 0) score -= factorThreeOfOwnInSquare*factorOwnAndOther;
        }

        // top location is from color
        PylosLocation[] locations = board.getLocations();
        int size = locations.length;
        if (locations[size-1].getSphere() != null) {
            if (locations[size-1].getSphere().PLAYER_COLOR == color) score += factorTopIsOwnSphere;
            else if (locations[size-1].getSphere().PLAYER_COLOR == color.other()) score -= factorTopIsOwnSphere*factorOwnAndOther;
        }


        return score;
//        return board.getReservesSize(color) - board.getReservesSize(color.other());
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

    // Get all spheres on the board of a color that can be removed to reserve
    private ArrayList<PylosSphere> getCanBeRemoved(PylosBoard board, PylosPlayerColor color) {

        ArrayList<PylosSphere> canBeRemoved = new ArrayList<>();
        for (PylosSphere sphere : board.getSpheres(color)) {
            if (sphere.canRemove()) canBeRemoved.add(sphere);
        }

        return canBeRemoved;
    }
}
