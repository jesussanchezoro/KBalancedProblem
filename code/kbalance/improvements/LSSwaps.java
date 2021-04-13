package kbalance.improvements;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LSSwaps implements Improvement<KBSolution> {

    private final int ofTarget;
    private Random rnd;

    public LSSwaps(int ofTarget) {
        this.ofTarget = ofTarget;
    }

    @Override
    public void improve(KBSolution solution) {
        rnd = new Random(RandomManager.getRandom().nextInt());
        boolean improvement = true;
        while (improvement) {
            improvement = tryImprove(solution);
        }
    }

    private boolean tryImprove(KBSolution solution) {
        KBInstance instance = solution.getInstance();
        List<Integer> selected = solution.getSelectedCopy();
        Collections.shuffle(selected, rnd);
        int m = instance.getM();
        List<Integer> nonSelected = new ArrayList<>(m);
        for (int ns = 1; ns <= m; ns++) {
            if (!solution.contains(ns)) {
                nonSelected.add(ns);
            }
        }
        Collections.shuffle(nonSelected, rnd);
        int ofPrev = solution.getOF(ofTarget);
        for (int s : selected) {
            solution.remove(s);
            for (int ns : nonSelected) {
                solution.add(ns);
                Pareto.add(solution);
                if (solution.getOF(ofTarget) < ofPrev) {
                    return true;
                }
                solution.remove(ns);
            }
            solution.add(s);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+ofTarget+")";
    }
}
