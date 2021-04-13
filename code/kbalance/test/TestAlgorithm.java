package kbalance.test;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import kbalance.algorithms.SO;
import kbalance.algorithms.parallel.Parallel_AlgConstLS;
import kbalance.combination.PR2;
import kbalance.constructives.C1;
import kbalance.constructives.CDistance;
import kbalance.improvements.LSF1;
import kbalance.improvements.LSF2;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

public class TestAlgorithm {

    public static void main(String[] args) {
        Pareto.reset(1);
        RandomManager.setSeed(1309);
//        String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/kbcl/preliminary/WorkSpace 1000_50_15  (1500 in 1000).txt";
        String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/kbcl/preliminary_three_of/WorkSpace 1000_50_5.txt";
        KBInstance instance = new KBInstance(path);
//        Constructive<KBInstance, KBSolution> c = new CDistance(1f);
        Constructive<KBInstance, KBSolution> c = new C1(1,-1f);
        Improvement<KBSolution> ls = new LSF2(1.0f);
        for (int i = 0; i < 50; i++) {
            KBSolution sol = c.constructSolution(instance);
            System.out.print(sol.getOF(1)+"-");
            ls.improve(sol);
            System.out.println(sol.getOF(1)+" ");
        }
    }
}
