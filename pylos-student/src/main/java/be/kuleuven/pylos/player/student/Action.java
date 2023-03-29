package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosPlayerColor;
import be.kuleuven.pylos.game.PylosSphere;

import java.util.ArrayList;

class Action {
    private PylosSphere sphere;
    private ActionType actionType;
    private PylosPlayerColor color;
    private PylosLocation from, to;
    private int score;
    private ArrayList<Action> children;

    public Action(PylosSphere s, ActionType a, PylosPlayerColor c, PylosLocation f, PylosLocation t) {
        sphere = s;
        actionType = a;
        color = c;
        from = f;
        to = t;
        score = 0;
        children = new ArrayList<>();
    }

    public Action() {
        children = new ArrayList<>();
    }

    public PylosSphere getSphere() {
        return sphere;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public PylosPlayerColor getColor() {
        return color;
    }

    public void setColor(PylosPlayerColor color) {
        this.color = color;
    }

    public PylosLocation getFrom() {
        return from;
    }

    public PylosLocation getTo() {
        return to;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<Action> getChildren() {
        return children;
    }

    public void addChild(Action action) {
        children.add(action);
    }

    public void setChildren(ArrayList<Action> children) {
        this.children = children;
    }
}
