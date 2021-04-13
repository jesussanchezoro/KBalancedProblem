package kbalance.constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CRandom implements Constructive<KBInstance, KBSolution> {
    @Override
    public KBSolution constructSolution(KBInstance instance) {
        KBSolution sol = new KBSolution(instance);
        int m = instance.getM();
        List<Integer> candidates = new ArrayList<>(m);
        for (int f = 1; f <= m; f++) {
            candidates.add(f);
        }
        Collections.shuffle(candidates, RandomManager.getRandom());
        int idx = 0;
        while (!sol.isFeasible()) {
            sol.add(candidates.get(idx));
            idx++;
        }
        return sol;
    }
}
