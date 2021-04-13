package kbalance.test;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import kbalance.constructives.C1;
import kbalance.constructives.CRandom;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

public class TestPareto {

    public static void main(String[] args) {
        RandomManager.setSeed(1309);
        String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/kbcl/toy.txt";
        KBInstance instance = new KBInstance(path);
        Constructive<KBInstance, KBSolution> c = new CRandom();
        Pareto.reset(1);
        for (int i = 0; i < 10; i++) {
            KBSolution sol = c.constructSolution(instance);
            Pareto.add(sol);
            System.out.println(sol.getOF(0)+"\t"+sol.getOF(1));
        }
        System.out.println("PARETO");
        System.out.println(Pareto.toText());
    }
}
