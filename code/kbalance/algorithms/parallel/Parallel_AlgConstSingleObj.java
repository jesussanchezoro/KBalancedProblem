package kbalance.algorithms.parallel;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.Timer;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Parallel_AlgConstSingleObj implements Algorithm<KBInstance> {

    private final Constructive<KBInstance, KBSolution> c;
    private final int iters;
    private final int ofTarget;
    private int bestSol;

    public Parallel_AlgConstSingleObj(Constructive<KBInstance, KBSolution> c, int iters, int ofTarget) {
        this.c = c;
        this.iters = iters;
        this.ofTarget = ofTarget;
    }

    @Override
    public Result execute(KBInstance instance) {
        Pareto.reset(1);
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        ExecutorService pool = Executors.newCachedThreadPool();
        System.out.print(instance.getName()+"\t"+instance.getN()+"\t"+instance.getM()+"\t"+instance.getK()+"\t");
        Lock lock = new ReentrantLock();
        Result result = new Result(instance.getName());
        Timer.initTimer();
        bestSol = 0x3f3f3f;
        for (int i = 0; i < iters; i++) {
            pool.submit(() -> {
                KBSolution sol = c.constructSolution(instance);
                lock.lock();
                bestSol = Math.min(bestSol, sol.getOF(ofTarget));
                lock.unlock();
            });
        }
        pool.shutdown();
        try {
            pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double secs = Timer.getTime()/1000.0;
        System.out.println(bestSol+"\t"+secs);
        result.add("OF", bestSol);
        result.add("Time (s)", secs);
        return result;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+c+","+iters+","+ofTarget+")";
    }
}
