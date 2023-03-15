package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosPlayerColor;
import be.kuleuven.pylos.game.PylosSphere;

import java.util.ArrayList;

/*
    zijn eigen kunnen uitvoeren, simuleren en reversen
*/

public class Action {
    private PylosSphere sphere;
    private ActionType actionType;
    private PylosPlayerColor color;
    private PylosLocation from, to;
    private int scores;
    private boolean hasScore;
    private ArrayList<Action> children;

    public Action(PylosSphere s, ActionType a, PylosPlayerColor c, PylosLocation f, PylosLocation t){
        sphere = s;
        actionType = a;
        color = c;
        from = f;
        to = t;
        scores = 0;
        hasScore = false;
        children = new ArrayList<>();
    }

    public Action(PylosPlayerColor c) {
        color = c;
    }

    public void setScores(int score) {
        scores = score;
        hasScore = true;
    }

    public PylosSphere getSphere() {
        return sphere;
    }

    public void setSphere(PylosSphere sphere) {
        this.sphere = sphere;
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

    public void setFrom(PylosLocation from) {
        this.from = from;
    }

    public PylosLocation getTo() {
        return to;
    }

    public void setTo(PylosLocation to) {
        this.to = to;
    }

    public int getScores() {
        return scores;
    }

    public boolean isHasScore() {
        return hasScore;
    }

    public void setHasScore(boolean hasScore) {
        this.hasScore = hasScore;
    }
}
