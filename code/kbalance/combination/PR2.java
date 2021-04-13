package kbalance.combination;

import grafo.optilib.metaheuristics.Combiner;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PR2 {

    /*
    s1 es la solucion que tiene mas nodos de lo normal
    s2 es la solucion con menos nodos de lo normal
     */
    public List<KBSolution> combine(KBSolution s1, KBSolution s2) {
        if (s1.getSelected().size() < s2.getSelected().size()) {
            return combineDecrease(s1, s2);
        } else {
            return combineIncrease(s1, s2);
        }
    }

    public List<KBSolution> combineDecrease(KBSolution s1, KBSolution s2) {
        List<KBSolution> solutions = new ArrayList<>(s1.getInstance().getN());
        KBInstance instance = s1.getInstance();
        List<Integer> s1NotInS2 = new ArrayList<>(instance.getM());
        List<Integer> s2NotInS1 = new ArrayList<>(instance.getM());
        for (int f1 : s1.getSelected()) {
            if (!s2.contains(f1)) {
                s1NotInS2.add(f1);
            }
        }
        for (int f2 : s2.getSelected()) {
            if (!s1.contains(f2)) {
                s2NotInS1.add(f2);
            }
        }
        Random rnd = RandomManager.getRandom();
        KBSolution sPR = new KBSolution(s1);
        while (!sPR.isFeasible()) {
            int f2 = s2NotInS1.remove(rnd.nextInt(s2NotInS1.size()));
            sPR.add(f2);
        }
        // Solution sPR is now feasible
        Pareto.add(sPR);
        solutions.add(sPR);
        while (!s1NotInS2.isEmpty() && !s2NotInS1.isEmpty()) {
            int f1 = s1NotInS2.remove(rnd.nextInt(s1NotInS2.size()));
            int f2 = s2NotInS1.remove(rnd.nextInt(s2NotInS1.size()));
            sPR.remove(f1);
            sPR.add(f2);
            Pareto.add(sPR);
            solutions.add(sPR);
        }
        return solutions;
    }

    public List<KBSolution> combineIncrease(KBSolution s1, KBSolution s2) {
        List<KBSolution> solutions = new ArrayList<>(s1.getInstance().getN());
        KBInstance instance = s1.getInstance();
        List<Integer> s1NotInS2 = new ArrayList<>(instance.getM());
        List<Integer> s2NotInS1 = new ArrayList<>(instance.getM());
        for (int f1 : s1.getSelected()) {
            if (!s2.contains(f1)) {
                s1NotInS2.add(f1);
            }
        }
        for (int f2 : s2.getSelected()) {
            if (!s1.contains(f2)) {
                s2NotInS1.add(f2);
            }
        }
        Random rnd = new Random(RandomManager.getRandom().nextInt());
        KBSolution sPR = new KBSolution(s1);
        while (!sPR.isFeasible()) {
            int f1 = s1NotInS2.remove(rnd.nextInt(s1NotInS2.size()));
            sPR.remove(f1);
        }
        // Solution sPR is now feasible
        Pareto.add(sPR);
        solutions.add(sPR);
        while (!s1NotInS2.isEmpty() && !s2NotInS1.isEmpty()) {
            int f1 = s1NotInS2.remove(rnd.nextInt(s1NotInS2.size()));
            int f2 = s2NotInS1.remove(rnd.nextInt(s2NotInS1.size()));
            sPR.remove(f1);
            sPR.add(f2);
            Pareto.add(sPR);
            solutions.add(sPR);
        }
        return solutions;
    }
}
