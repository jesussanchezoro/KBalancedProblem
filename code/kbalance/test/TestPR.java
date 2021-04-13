package kbalance.test;

import grafo.optilib.metaheuristics.Combiner;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import kbalance.combination.PR2;
import kbalance.constructives.CRandom;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestPR {

    public static void main(String[] args) {
        RandomManager.setSeed(1309);
        String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/kbcl/toy.txt";
        KBInstance instance = new KBInstance(path);
        Constructive<KBInstance, KBSolution> c = new CRandom();
        KBSolution sol1 = new KBSolution(instance);
        sol1.add(1); sol1.add(2); sol1.add(3);
        KBSolution sol2 = new KBSolution(instance);
        sol2.add(4); sol2.add(5); sol2.add(3);
        KBSolution sPR1 = increase(sol1, 0.3f);
        KBSolution sPR2 = decrease(sol2, 0.3f);
        PR2 pr = new PR2();
        pr.combine(sPR1, sol2);
    }

    private static KBSolution increase(KBSolution sol, float beta) {
        KBSolution sInc = new KBSolution(sol);
        int nInc = (int) Math.ceil(beta * sol.getInstance().getK());
        int n = sol.getInstance().getN();
        List<Integer> cand = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            if (!sol.contains(i)) {
                cand.add(i);
            }
        }
        Collections.shuffle(cand, RandomManager.getRandom());
        for (int i = 0; i < nInc; i++) {
            sInc.add(cand.get(i));
        }
        return sInc;
    }

    private static KBSolution decrease(KBSolution sol, float beta) {
        KBSolution sDec = new KBSolution(sol);
        int nDec = (int) Math.ceil(beta * sol.getInstance().getK());
        int n = sol.getInstance().getN();
        List<Integer> cand = sol.getSelectedCopy();
        Collections.shuffle(cand, RandomManager.getRandom());
        for (int i = 0; i < nDec; i++) {
            sDec.remove(cand.get(i));
        }
        return sDec;
    }
}
