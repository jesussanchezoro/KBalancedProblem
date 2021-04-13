package kbalance.algorithms.parallel;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parallel_AlgConstLS implements Algorithm<KBInstance> {

    private final Constructive<KBInstance, KBSolution> c[];
    private final Improvement<KBSolution> ls[];
    private final int iters;

    public Parallel_AlgConstLS(Constructive<KBInstance, KBSolution> c[], Improvement<KBSolution> ls[], int iters) {
        this.c = c;
        this.ls = ls;
        this.iters = iters;
    }

    @Override
    public Result execute(KBInstance instance) {
        Pareto.reset(1);
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        ExecutorService pool = Executors.newCachedThreadPool();
        System.out.print(instance.getName()+"\t"+instance.getN()+"\t"+instance.getM()+"\t"+instance.getK()+"\t");
        Result result = new Result(instance.getName());
        Timer.initTimer();
        for (int obj = 0; obj < c.length; obj++) {
            for (int i = 0; i < iters; i++) {
                int finalObj = obj;
                int finalI = i;
                pool.submit(() -> {
                    KBSolution sol = c[finalObj].constructSolution(instance);
                    Pareto.add(sol);
                    for (Improvement<KBSolution> search : ls) {
                        KBSolution solJ = new KBSolution(sol);
                        search.improve(solJ); // La BL ya incluye todas las soluciones en el frente
                    }
                });
            }
        }
        pool.shutdown();
        try {
            pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        stb.append(this.getClass().getSimpleName()).append("(");
        for (Constructive<KBInstance, KBSolution> con : c) {
            stb.append(con).append(",");
        }
        for (Improvement<KBSolution> l : ls) {
            stb.append(l).append(",");
        }
        stb.append(iters).append(")");
        return stb.toString();
    }
}
