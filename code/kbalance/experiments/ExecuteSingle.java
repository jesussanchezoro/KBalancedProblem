package kbalance.experiments;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import kbalance.algorithms.parallel.Parallel_SO;
import kbalance.constructives.C1;
import kbalance.improvements.LSF1;
import kbalance.improvements.LSF2;
import kbalance.structure.KBInstance;

public class ExecuteSingle {

    public static void main(String[] args) {
        RandomManager.setSeed(13);
        Algorithm<KBInstance> algorithm = new Parallel_SO(
                new Constructive[]{new C1(0, 0.5f), new C1(1, -1), new C1(2, -1)},
                new Improvement[]{new LSF1(1), new LSF2(1)},
                0.1f,
                33
        );
        KBInstance instance = new KBInstance(args[0]);
        algorithm.execute(instance);
    }
}
