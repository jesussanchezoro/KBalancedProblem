package kbalance.algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

public class AlgConst implements Algorithm<KBInstance> {

    private final Constructive<KBInstance, KBSolution> c;
    private final int iters;
    private final int ofTarget;
    private KBSolution best;

    public AlgConst(Constructive<KBInstance, KBSolution> c, int iters, int ofTarget) {
        this.c = c;
        this.iters = iters;
        this.ofTarget = ofTarget;
    }

    @Override
    public Result execute(KBInstance instance) {
        Pareto.reset(1);
        best = null;
        System.out.print(instance.getName()+"\t"+instance.getN()+"\t"+instance.getM()+"\t"+instance.getK()+"\t");
        Result result = new Result(instance.getName());
        Timer.initTimer();
        for (int i = 0; i < iters; i++) {
            KBSolution sol = c.constructSolution(instance);
            Pareto.add(sol);
            if (best == null || sol.getOF(ofTarget) < best.getOF(ofTarget)) {
                best = sol;
            }
        }
        double secs = Timer.getTime()/1000.0;
        result.add("n", instance.getN());
        result.add("m", instance.getM());
        result.add("k", instance.getK());
        result.add("OF "+ofTarget, best.getOF(ofTarget));
//        result.add("OF 2", best.getOF(1));
//        result.add("OF 3", best.getOF(2));
        result.add("Time (s)", secs);
//        System.out.println(best.getOF(0)+"\t"+best.getOF(1)+"\t"+best.getOF(2)+"\t"+secs);
        System.out.println(best.getOF(ofTarget)+"\t"+secs+"\t"+Pareto.getFront().size());
        return result;
    }

    @Override
    public Solution getBestSolution() {
        return best;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+c+","+iters+")";
    }
}
