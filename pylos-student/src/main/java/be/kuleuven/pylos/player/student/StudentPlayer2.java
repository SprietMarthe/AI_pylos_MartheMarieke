//package be.kuleuven.pylos.player.student;
//
//import be.kuleuven.pylos.game.*;
//import be.kuleuven.pylos.player.PylosPlayer;
//
//import java.util.ArrayList;
//
//
//// TODO : construct evaluation function
//// TODO : extra --> alfa beta pruning
//
//public class StudentPlayer2 extends PylosPlayer {
//
//    int maxDepth = 2;
//
//    @Override
//    public void doMove(PylosGameIF game, PylosBoard board) {
//        /* board methods
//         * 	PylosLocation[] allLocations = board.getLocations();
//         * 	PylosSphere[] allSpheres = board.getSpheres();
//         * 	PylosSphere[] mySpheres = board.getSpheres(this);
//         * 	PylosSphere myReserveSphere = board.getReserve(this); */
//
//        /* game methods
//         * game.moveSphere(myReserveSphere, allLocations[0]); */
//
//        Action2 startAction = new Action2();
//
//        Action2 bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction);
//        game.moveSphere(bestMove.getSphere(), bestMove.getTo());
//
//    }
//
//    @Override
//    public void doRemove(PylosGameIF game, PylosBoard board) {
//        /* game methods
//         * game.removeSphere(mySphere); */
//
//        Action2 startAction = new Action2();
//
//        Action2 bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction);
//        PylosSphere sphereToBeRemoved = bestMove.getSphere();
//
//        game.removeSphere(sphereToBeRemoved);
//    }
//
//    @Override
//    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
//        /* game methods
//         * game.removeSphere(mySphere);
//         * game.pass() */
//
//        Action2 startAction = new Action2();
//
//        Action2 bestMove =  minimax(game.getState(), this.PLAYER_COLOR, board, maxDepth, startAction);
//        if (bestMove.getSphere() == null) game.pass();
//        else {
//            PylosSphere sphereToBeRemoved = bestMove.getSphere();
//            game.removeSphere(sphereToBeRemoved);
//        }
//
//    }
//
//
//
//    /* privates --------------------------------------------------------------------------------------------- */
//
//
//     // Minimax recursive function
//    private Action2 minimax(PylosGameState state, PylosPlayerColor color, PylosBoard board, int depth, Action2 last) {
//
//        PylosGameSimulator pylosGameSimulator = new PylosGameSimulator(state, color, board);
//
//        // stop condition
//        if (depth == 0 || state == PylosGameState.COMPLETED) {
//            last.setScore(evaluateBoard(board, color));
//            return last;
//        }
//
//        // generate all the possible children actions of last action
//        giveLastActionChildren(state, color, board, last);
//
//        // maximize if color is color of this player
//        if (color == this.PLAYER_COLOR) {
//            int bestValue = Integer.MIN_VALUE;
//            Action2 currentMaxAction = null;
//
//            for (Action2 a : last.getChildren()) {
//
//                doSimulation(pylosGameSimulator, a);
//
//                Action2 bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, a);
//                if (bestValue < bestNext.getScore()) {
//                    bestValue = bestNext.getScore();
//                    currentMaxAction = a;
//                }
//
//                undoSimulation(pylosGameSimulator, a);
//            }
//
//            return currentMaxAction;
//        }
//
//        // minimize if color is color of other player
//        else {
//            int bestValue = Integer.MAX_VALUE;
//            Action2 currentMinAction = null;
//
//            for (Action2 a : last.getChildren()) {
//
//                doSimulation(pylosGameSimulator, a);
//
//                Action2 bestNext = minimax(pylosGameSimulator.getState(), pylosGameSimulator.getColor(), board, depth-1, a);
//                if (bestValue > bestNext.getScore()) {
//                    bestValue = bestNext.getScore();
//                    currentMinAction = a;
//                }
//
//                undoSimulation(pylosGameSimulator, a);
//            }
//
//            return currentMinAction;
//        }
//    }
//
//    // Generate all the possible children actions of last action
//    private void giveLastActionChildren(PylosGameState state, PylosPlayerColor color, PylosBoard board, Action2 action) {
//
//        switch (state) {
//            case MOVE:
//                // possible top square places to move to
//                ArrayList<PylosLocation> allUsableTopLocations = getUsableTopLocations(board);
//
//                // all paths of board spheres to a higher square
//                for (PylosLocation to : allUsableTopLocations) {
//                    for (PylosSphere sphere : board.getSpheres(color)) {
//                        if (!sphere.isReserve() && sphere.canMoveTo(to)) {
//                            PylosLocation from = sphere.getLocation();
//                            Action2 child = new Action2(sphere, ActionType2.MOVE, color, from, to);
//                            action.addChild(child);
//                        }
//                    }
//                }
//
//                // possible free places to add to
//                ArrayList<PylosLocation> allUsableLocations = getUsableLocations(board);
//
//                // all paths of reserve spheres to the board
//                PylosSphere sphereToMove = board.getReserve(color);
//                for (PylosLocation to : allUsableLocations) {
//                    PylosLocation from = sphereToMove.getLocation();
//                    Action2 child = new Action2(sphereToMove, ActionType2.ADD, color, from, to);
//                    action.addChild(child);
//                }
//
//                break;
//
//            case REMOVE_FIRST:
//
//                // possible spheres of playercolor that can move to reserve
//                ArrayList<PylosSphere> allThatCanBeRemoved = getCanBeRemoved(board, color);
//                for (PylosSphere sphere : allThatCanBeRemoved) {
//                    Action2 child = new Action2(sphere, ActionType2.REMOVE_FIRST, color, sphere.getLocation(), null);
//                    action.addChild(child);
//                }
//
//                break;
//
//            case REMOVE_SECOND:
//
//                // action pass
//                action.addChild(new Action2(null, ActionType2.PASS, color, null, null));
//
//                // possible spheres of playercolor that can move to reserve
//                allThatCanBeRemoved = getCanBeRemoved(board, color);
//                for (PylosSphere sphere : allThatCanBeRemoved) {
//                    Action2 child = new Action2(sphere, ActionType2.REMOVE_SECOND, color, sphere.getLocation(), null);
//                    action.addChild(child);
//                }
//
//        }
//    }
//
//
//    private void doSimulation(PylosGameSimulator pylosGameSimulator, Action2 a) {
//
//        switch (a.getActionType()) {
//
//            case MOVE:
//            case ADD:
//                pylosGameSimulator.moveSphere(a.getSphere(), a.getTo());
//                break;
//
//            case REMOVE_FIRST:
//            case REMOVE_SECOND:
//                pylosGameSimulator.removeSphere(a.getSphere());
//                break;
//
//            case PASS:
//                pylosGameSimulator.pass();
//        }
//    }
//
//    private void undoSimulation(PylosGameSimulator pylosGameSimulator, Action2 a) {
//
//        switch (a.getActionType()) {
//
//            case MOVE:
//                pylosGameSimulator.undoMoveSphere(a.getSphere(), a.getFrom(), PylosGameState.MOVE, a.getColor());
//                break;
//
//            case ADD:
//                pylosGameSimulator.undoAddSphere(a.getSphere(), PylosGameState.MOVE, a.getColor());
//                break;
//
//            case REMOVE_FIRST:
//                pylosGameSimulator.undoRemoveFirstSphere(a.getSphere(), a.getFrom(), PylosGameState.REMOVE_FIRST, a.getColor());
//                break;
//
//            case REMOVE_SECOND:
//                pylosGameSimulator.undoRemoveSecondSphere(a.getSphere(), a.getFrom(), PylosGameState.REMOVE_SECOND, a.getColor());
//                break;
//
//            case PASS:
//                pylosGameSimulator.undoPass(PylosGameState.REMOVE_SECOND, a.getColor());
//        }
//    }
//
//
//    // Evaluation function
//    private int evaluateBoard(PylosBoard board, PylosPlayerColor color) {
//
//        int factorOwnReserveSpheres = 40;
//        int factorOtherReserveSpheres = 30;
//        int factorThreeOfOwnInSquare = 10;
//        int factorThreeOfOtherInSquare = 5;
//        int factorFourOfOwnInSquare = 60;
//        int factorBlockFourOfOther = 30;
//        int factorCompleteSquare = 20;
//        int factorTopIsOwnSphere = 100;
//        int factorTopIsOtherSphere = 100;
//
//        // own reserve spheres
//        int score = board.getReservesSize(color) * factorOwnReserveSpheres;
//
//        // other reserve spheres
//        score -= board.getReservesSize(color) * factorOtherReserveSpheres;
//
//        // squares
//        PylosSquare[] allSquares = board.getAllSquares();
//        for (PylosSquare square : allSquares) {
//            if (square.getInSquare(color) == 4) score += factorFourOfOwnInSquare;
//            else if (square.getInSquare(color.other()) == 3 && square.getInSquare(color) == 1) score += factorBlockFourOfOther;
//            else if (square.getInSquare() == 4) score -= factorCompleteSquare;
//            else if (square.getInSquare(color) == 3 && square.getInSquare(color.other()) == 0) score += factorThreeOfOwnInSquare;
//            else if (square.getInSquare(color.other()) == 3 && square.getInSquare(color) == 0) score -= factorThreeOfOtherInSquare;
//        }
//
//        // top location is from color
//        PylosLocation[] locations = board.getLocations();
//        int size = locations.length;
//        if (locations[size-1].getSphere() != null) {
//            if (locations[size-1].getSphere().PLAYER_COLOR == color) score += factorTopIsOwnSphere;
//            else if (locations[size-1].getSphere().PLAYER_COLOR == color.other()) score -= factorTopIsOtherSphere;
//        }
//
//
//        return score;
//    }
//
//
//    // Get all usable locations to move a sphere to
//    private ArrayList<PylosLocation> getUsableLocations(PylosBoard board) {
//
//        ArrayList<PylosLocation> usableLocations =  new ArrayList<>();
//        for (PylosLocation bl : board.getLocations()) {
//            if (bl.isUsable()) usableLocations.add(bl);
//        }
//
//        return usableLocations;
//    }
//
//    // Get all usable top locations to move a sphere to
//    private ArrayList<PylosLocation> getUsableTopLocations(PylosBoard board) {
//
//        ArrayList<PylosLocation> usableTopLocations = new ArrayList<>();
//        for (PylosSquare ps : board.getAllSquares()) {
//            if (ps.isSquare()) usableTopLocations.add(ps.getTopLocation());
//        }
//
//        return usableTopLocations;
//    }
//
//    // Get all spheres on the board of a color that can be removed to reserve
//    private ArrayList<PylosSphere> getCanBeRemoved(PylosBoard board, PylosPlayerColor color) {
//
//        ArrayList<PylosSphere> canBeRemoved = new ArrayList<>();
//        for (PylosSphere sphere : board.getSpheres(color)) {
//            if (sphere.canRemove()) canBeRemoved.add(sphere);
//        }
//
//        return canBeRemoved;
//    }
//}
//
