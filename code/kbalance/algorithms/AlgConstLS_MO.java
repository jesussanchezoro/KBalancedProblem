package kbalance.algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

import java.io.File;

public class AlgConstLS_MO implements Algorithm<KBInstance> {

    private final Constructive<KBInstance, KBSolution> c[];
    private final Improvement<KBSolution> ls[];
    private final int iters;

    public AlgConstLS_MO(Constructive<KBInstance, KBSolution> c[], Improvement<KBSolution> ls[], int iters) {
        this.c = c;
        this.ls = ls;
        this.iters = iters;
    }

    @Override
    public Result execute(KBInstance instance) {
        Pareto.reset(1);
        System.out.print(instance.getName()+"\t"+instance.getN()+"\t"+instance.getM()+"\t"+instance.getK()+"\t");
        Result result = new Result(instance.getName());
        Timer.initTimer();
        for (int obj = 0; obj < c.length; obj++) {
            for (int i = 0; i < iters; i++) {
                KBSolution sol = c[obj].constructSolution(instance);
                Pareto.add(sol);
                for (int j = 0; j < ls.length; j++) {
                    KBSolution solJ = new KBSolution(sol);
                    ls[j].improve(solJ); // La BL ya incluye todas las soluciones en el frente
                }
            }
        }

        double secs = Timer.getTime()/1000.0;
        result.add("n", instance.getN());
        result.add("m", instance.getM());
        result.add("k", instance.getK());
        result.add("Time (s)", secs);
        System.out.println(secs);
        String paretoPath = "pareto/"+this.toString();
        new File(paretoPath).mkdirs();
        Pareto.saveToFile(paretoPath+"/"+instance.getName());
        return result;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (Constructive<KBInstance, KBSolution> constructive : c){
            stb.append(constructive+",");
        }
        for (Improvement<KBSolution> l : ls) {
            stb.append(l+",");
        }
        return this.getClass().getSimpleName()+"("+stb.toString()+iters+")";
    }
}
