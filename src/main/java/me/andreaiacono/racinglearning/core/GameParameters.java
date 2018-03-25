package me.andreaiacono.racinglearning.core;

import me.andreaiacono.racinglearning.misc.GameParameter;

import java.util.HashMap;
import java.util.Map;

public class GameParameters {

    final Map<GameParameter, Object> params = new HashMap<>();

    public GameParameters(String[] args) {
        if (args.length > 0) {
            addParam(GameParameter.IS_HUMAN, args[0].equalsIgnoreCase("human"));
        }
        if (args.length > 1) {
            addParam(GameParameter.DRAW_INFO, args[1].equalsIgnoreCase("draw"));
        }
        if (args.length > 2) {
            addParam(GameParameter.USE_BLACK_AND_WHITE, args[2].equalsIgnoreCase("b&w"));
        }
    }

    public void addParam(GameParameter param, Object value) {
        params.put(param, value);
    }

    public String getValue(GameParameter param) {
        return params.get(param).toString();
    }

    public boolean getBool(GameParameter param) {
        return (boolean) params.get(param);
    }

}
