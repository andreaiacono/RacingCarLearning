package me.andreaiacono.racinglearning.rl;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.util.RandomUtils;
import me.andreaiacono.racinglearning.core.Command;

import java.util.ArrayList;

/** Contains the available actions that can be taken in an {@link ai.djl.modality.rl.env.RlEnv}. */
public class ActionSpace extends ArrayList<NDList> {

    private static final long serialVersionUID = 8683452581122892189L;

    public ActionSpace() {
        NDManager manager = NDManager.newBaseManager();
        for (int i=0; i<Command.values().length; i++) {
                add(new NDList(manager.create(val(i))));
        }
    }

    private int[] val(int i) {
        int[] result = new int[Command.values().length];
        result[i] = 1;
        return result;
    }

    /**
     * Returns a random action.
     *
     * @return a random action
     */
    public NDList randomAction() {
        return get(RandomUtils.nextInt(size()));
    }
}
