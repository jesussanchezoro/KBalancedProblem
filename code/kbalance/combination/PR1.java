package kbalance.combination;

import grafo.optilib.metaheuristics.Combiner;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PR1 implements Combiner<KBSolution> {

    /*
    s1 es la solucion que tiene mas nodos de lo normal
    s2 es la solucion con menos nodos de lo normal
     */
    @Override
    public KBSolution combine(KBSolution s1, KBSolution s2) {
        KBSolution sPR = new KBSolution(s1);
        KBInstance instance = s1.getInstance();
        List<Integer> s1NotInS2 = new ArrayList<>(instance.getM());
        List<Integer> s2NotInS1 = new ArrayList<>(instance.getM());
        for (int f1 : sPR.getSelected()) {
            if (!s2.contains(f1)) {
                s1NotInS2.add(f1);
            }
        }
        for (int f2 : s2.getSelected()) {
            if (!sPR.contains(f2)) {
                s2NotInS1.add(f2);
            }
        }
        Random rnd = RandomManager.getRandom();
        while (!s2NotInS1.isEmpty()) {
            int f1 = s1NotInS2.remove(rnd.nextInt(s1NotInS2.size()));
            int f2 = s2NotInS1.remove(rnd.nextInt(s2NotInS1.size()));
            sPR.remove(f1);
            sPR.add(f2);
        }
        List<Integer> selected = sPR.getSelectedCopy();
        while (!sPR.isFeasible()) {
            int f = selected.remove(rnd.nextInt(selected.size()));
            sPR.remove(f);
        }
        return sPR;
    }
}
