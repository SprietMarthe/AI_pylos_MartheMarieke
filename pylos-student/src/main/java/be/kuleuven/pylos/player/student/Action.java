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
    private PylosPlayerColor color;
    private PylosLocation from, to;
    private int scores;
    private boolean hasScore;
    private ArrayList<Action> children;

    public Action(PylosSphere s, PylosPlayerColor c, PylosLocation f, PylosLocation t){
        sphere = s;
        color = c;
        from = f;
        to = t;
        scores = 0;
        hasScore = false;
        children = new ArrayList<>();
    }


}
