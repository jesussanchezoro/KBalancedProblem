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

public class LSPareto implements Improvement<KBSolution> {

    private Random rnd;

    @Override
    public void improve(KBSolution solution) {
        rnd = new Random(RandomManager.getRandom().nextInt());
        KBInstance instance = solution.getInstance();
        int m = instance.getM();
        List<Integer> nonSelected = new ArrayList<>(m);
        List<Integer> selected = new ArrayList<>(m);
        for (int ns = 1; ns <= m; ns++) {
            if (!solution.contains(ns)) {
                nonSelected.add(ns);
            } else {
                selected.add(ns);
            }
        }
        boolean improvement = true;
        while (improvement) {
            improvement = tryImprove(solution, selected, nonSelected);
        }
    }

    private boolean tryImprove(KBSolution solution, List<Integer> selected, List<Integer> nonSelected) {
        Collections.shuffle(nonSelected, rnd);
        Collections.shuffle(selected, rnd);
        for (int i = 0; i < selected.size(); i++) {
            int s = selected.get(i);
            solution.remove(s);
            for (int j = 0; j < nonSelected.size(); j++) {
                int ns = nonSelected.get(j);
                solution.add(ns);
                Pareto.add(solution);
                if (Pareto.isModifiedSinceLastAsk(0)) {
                    selected.remove(i);
                    nonSelected.remove(j);
                    selected.add(ns);
                    nonSelected.add(s);
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
        return this.getClass().getSimpleName();
    }
}
