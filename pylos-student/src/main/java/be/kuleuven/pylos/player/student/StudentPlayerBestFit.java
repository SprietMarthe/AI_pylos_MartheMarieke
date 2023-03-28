package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;


// TODO : construct evaluation function
// TODO : extra --> alfa beta pruning

public class StudentPlayerBestFit extends PylosPlayer {

    int maxDepth = 2;
    int factorOwnReserveSpheres = 2;
    int factorThreeOfOwnInSquare = 2;
    int factorFourOfOwnInSquare = 1;
    int factorCompleteSquare = 1;
    int factorTopIsOwnSphere = 2;
    double factorOwnAndOther = 3;


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


        Action bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction, Integer.MIN_VALUE, Integer.MAX_VALUE);
        game.moveSphere(bestMove.getSphere(), bestMove.getTo());

    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere); */

        Action startAction = new Action();

        Action bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction, Integer.MIN_VALUE, Integer.MAX_VALUE);
        PylosSphere sphereToBeRemoved = bestMove.getSphere();

        game.removeSphere(sphereToBeRemoved);
    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        /* game methods
         * game.removeSphere(mySphere);
         * game.pass() */

        Action startAction = new Action();

        Action bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (bestMove.getSphere() == null) game.pass();
        else {
            PylosSphere sphereToBeRemoved = bestMove.getSphere();
            game.removeSphere(sphereToBeRemoved);
        }

    }



    /* privates --------------------------------------------------------------------------------------------- */


    // Minimax recursive function
    private Action minimax(PylosGameState state, PylosPlayerColor color, PylosBoard board, int depth, Action last, int alfa, int beta) {

        PylosGameSimulator pylosGameSimulator = new PylosGameSimulator(state, color, board);

        // stop condition
        if (depth == 0 || state == PylosGameState.COMPLETED) {
            last.setScore(evaluateBoard(board, color));
            return last;
        }

        // generate all the possible children actions of last action
        giveLastActionChildren(state, color, board, last);

        // maximize if color is color of this player
        if (color == this.PLAYER_COLOR) {
            int bestValue = Integer.MIN_VALUE;

            Action currentMaxAction = null;
            for (Action a : last.getChildren()) {

                doSimulation(pylosGameSimulator, a);

                Action bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, a, alfa, beta);
                if (bestNext.getScore() > beta ) { // beta cutoff
                    undoSimulation(pylosGameSimulator, a);
                    return a;
                }
                alfa = Math.max(alfa, bestNext.getScore());
                if (beta <= alfa){
                    undoSimulation(pylosGameSimulator, a);
                    return a;
                }
                if (bestValue < bestNext.getScore()) {
                    bestValue = bestNext.getScore();
                    currentMaxAction = a;
                    currentMaxAction.setScore(bestValue);
                }
                undoSimulation(pylosGameSimulator, a);
            }
            return currentMaxAction;
        }

        // minimize if color is color of other player
        else {
            int bestValue = Integer.MAX_VALUE;
            Action currentMinAction = null;
            for (Action a : last.getChildren()) {

                doSimulation(pylosGameSimulator, a);

                Action bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, a, alfa, beta);
                if (bestNext.getScore() < alfa ) { //alfa cutoff
                    undoSimulation(pylosGameSimulator, a);
                    return a;
                }
                beta = Math.min(beta, bestNext.getScore());
                if (beta <= alfa){
                    undoSimulation(pylosGameSimulator, a);
                    return a;
                }
                if (bestValue > bestNext.getScore()) {
                    bestValue = bestNext.getScore();
                    currentMinAction = a;
                    currentMinAction.setScore(bestValue);
                }
                undoSimulation(pylosGameSimulator, a);
            }
            return currentMinAction;
        }
    }

    // Generate all the possible children actions of last action
    private void giveLastActionChildren(PylosGameState state, PylosPlayerColor color, PylosBoard board, Action action) {
        action.setChildren(new ArrayList<>());
        switch (state) {
            case MOVE:
                // possible top square places to move to
                ArrayList<PylosLocation> allUsableTopLocations = getUsableTopLocations(board);

                // all paths of board spheres to a higher square
                for (PylosLocation to : allUsableTopLocations) {
                    for (PylosSphere sphere : board.getSpheres(color)) {
                        if (!sphere.isReserve() && sphere.canMoveTo(to)) {
                            PylosLocation from = sphere.getLocation();
                            Action child = new Action(sphere, ActionType3.MOVE, color, from, to);
                            action.addChild(child);
                        }
                    }
                }

                // possible free places to add to
                ArrayList<PylosLocation> allUsableLocations = getUsableLocations(board);

                // all paths of reserve spheres to the board
                PylosSphere sphereToMove = board.getReserve(color);
                for (PylosLocation to : allUsableLocations) {
                    PylosLocation from = sphereToMove.getLocation();
                    Action child = new Action(sphereToMove, ActionType3.ADD, color, from, to);
                    action.addChild(child);
                }

                break;

            case REMOVE_FIRST:

                // possible spheres of playercolor that can move to reserve
                ArrayList<PylosSphere> allThatCanBeRemoved = getCanBeRemoved(board, color);
                for (PylosSphere sphere : allThatCanBeRemoved) {
                    Action child = new Action(sphere, ActionType3.REMOVE_FIRST, color, sphere.getLocation(), null);
                    action.addChild(child);
                }

                break;

            case REMOVE_SECOND:

                // action pass
                action.addChild(new Action(null, ActionType3.PASS, color, null, null));

                // possible spheres of playercolor that can move to reserve
                allThatCanBeRemoved = getCanBeRemoved(board, color);
                for (PylosSphere sphere : allThatCanBeRemoved) {
                    Action child = new Action(sphere, ActionType3.REMOVE_SECOND, color, sphere.getLocation(), null);
                    action.addChild(child);
                }

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

class Action {
    private PylosSphere sphere;
    private ActionType3 actionType;
    private PylosPlayerColor color;
    private PylosLocation from, to;
    private int score;
    private boolean hasScore;
    private ArrayList<Action> children;

    public Action(PylosSphere s, ActionType3 a, PylosPlayerColor c, PylosLocation f, PylosLocation t){
        sphere = s;
        actionType = a;
        color = c;
        from = f;
        to = t;
        score = 0;
        hasScore = false;
        children = new ArrayList<>();
    }

    public Action() {
        children = new ArrayList<>();
    }

    public PylosSphere getSphere() {
        return sphere;
    }

    public void setSphere(PylosSphere sphere) {
        this.sphere = sphere;
    }

    public ActionType3 getActionType() { return actionType; }

    public void setActionType(ActionType3 actionType) { this.actionType = actionType; }

    public PylosPlayerColor getColor() {
        return color;
    }

    public void setColor(PylosPlayerColor color) {
        this.color = color;
    }

    public PylosLocation getFrom() {
        return from;
    }

    public void setFrom(PylosLocation from) {
        this.from = from;
    }

    public PylosLocation getTo() {
        return to;
    }

    public void setTo(PylosLocation to) {
        this.to = to;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        hasScore = true;
    }

    public boolean isHasScore() {
        return hasScore;
    }

    public void setHasScore(boolean hasScore) {
        this.hasScore = hasScore;
    }

    public ArrayList<Action> getChildren() { return children; }

    public void addChild(Action action) {
        children.add(action);
    }

    public void setChildren(ArrayList<Action> children) {
        this.children = children;
    }
}

enum ActionType {
    MOVE,
    ADD,
    REMOVE_FIRST,
    REMOVE_SECOND,
    PASS
}