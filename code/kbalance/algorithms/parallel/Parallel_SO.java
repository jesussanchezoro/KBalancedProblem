package kbalance.algorithms.parallel;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Combiner;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import kbalance.combination.PR2;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;
import org.apache.poi.ss.formula.functions.Count;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parallel_SO implements Algorithm<KBInstance> {

    private final Constructive<KBInstance, KBSolution> c[];
    private final Improvement<KBSolution> ls[];
    private final PR2 pr;
    private final int iters;
    private final float beta;

    private Random rnd;

    public Parallel_SO(Constructive<KBInstance, KBSolution> c[], Improvement<KBSolution> ls[], float beta, int iters) {
        this.c = c;
        this.ls = ls;
        this.iters = iters;
        this.pr = new PR2();
        this.beta = beta;
    }

    @Override
    public Result execute(KBInstance instance) {
        rnd = new Random(RandomManager.getRandom().nextInt());
        Pareto.reset(1);
        CountDownLatch latch = new CountDownLatch(iters * c.length);
        System.out.print(instance.getName()+"\t"+instance.getN()+"\t"+instance.getM()+"\t"+instance.getK()+"\t");
        Result result = new Result(instance.getName());
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Timer.initTimer(1800 * 1000);
        // Construccion del frente inicial
        for (int obj = 0; obj < c.length; obj++) {
            for (int i = 0; i < iters; i++) {
                int finalObj = obj;
//                int finalI = i;
                pool.submit(() -> {
                    try {
                        KBSolution sol = c[finalObj].constructSolution(instance); // C incluye la solucion en el frente
                        if (!Timer.timeReached()) {
                            for (Improvement<KBSolution> l : ls) {
                                KBSolution solJ = new KBSolution(sol);
                                l.improve(solJ); // La BL ya incluye todas las soluciones en el frente
                            }
                        }
                        //                    System.out.println("Obj "+finalObj+", sol "+finalI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("Initial Front: "+Timer.getTime()/1000.0);
        // Incremento / Decremento de las soluciones
        List<KBSolution> increase = new ArrayList<>(instance.getN());
        List<KBSolution> decrease = new ArrayList<>(instance.getN());
        for (KBSolution sol : Pareto.getFront()) {
            for (int i = 0; i < 3; i++) {
                increase.add(increase(sol, i));
                decrease.add(decrease(sol, i));
            }
        }
        CountDownLatch latchpr = new CountDownLatch(increase.size() * decrease.size());
//        System.out.println("Time Preparing: "+Timer.getTime()/1000.0);
        // PR
        for (KBSolution sInc : increase) {
            for (KBSolution sDec : decrease) {
                pool.submit(() -> {
                    try {
                        List<KBSolution> sols = null;
                        if (!Timer.timeReached()) {
                            sols = pr.combine(sInc, sDec);
                            for (KBSolution sol : sols) {
                                Pareto.add(sol);
                                for (Improvement<KBSolution> l : ls) {
                                    if (!Timer.timeReached()) {
                                        KBSolution lsSol = new KBSolution(sol);
                                        l.improve(lsSol);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latchpr.countDown();
                    }
                });
            }
        }
        try {
            latchpr.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("Time PR: "+Timer.getTime()/1000.0);
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

    private int selectBestToAdd(KBSolution sol, int ofTarget) {
        KBInstance instance = sol.getInstance();
        int m = instance.getM();
        int bestF = -1;
        int minCost = 0x3f3f3f;
        for (int f = 1; f <= m; f++) {
            if (!sol.contains(f)) {
                sol.add(f);
                int of = sol.getOF(ofTarget);
                if (of < minCost) {
                    bestF = f;
                    minCost = of;
                }
                sol.remove(f);
            }
        }
        return bestF;
    }

    private KBSolution increase(KBSolution sol, int ofTarget) {
        KBSolution sInc = new KBSolution(sol);
        int nInc = (int) Math.ceil(beta * sol.getInstance().getK());
        for (int i = 0; i < nInc; i++) {
            int next = selectBestToAdd(sInc, ofTarget);
            sInc.add(next);
        }
        return sInc;
    }

//    private KBSolution increase(KBSolution sol) {
//        KBSolution sInc = new KBSolution(sol);
//        int nInc = (int) Math.ceil(beta * sol.getInstance().getK());
//        int m = sol.getInstance().getM();
//        List<Integer> cand = new ArrayList<>(m);
//        for (int i = 1; i <= m; i++) {
//            if (!sol.contains(i)) {
//                cand.add(i);
//            }
//        }
//        Collections.shuffle(cand, rnd);
//        for (int i = 0; i < nInc; i++) {
//            sInc.add(cand.get(i));
//        }
//        return sInc;
//    }


    private int selectBestToRemove(KBSolution sol, int ofTarget) {
        KBInstance instance = sol.getInstance();
        int m = instance.getM();
        int bestF = -1;
        int minCost = 0x3f3f3f;
        List<Integer> selected = new ArrayList<>(sol.getSelected());
        for (int f : selected) {
            sol.remove(f);
            int of = sol.getOF(ofTarget);
            if (of < minCost) {
                bestF = f;
                minCost = of;
            }
            sol.add(f);
        }
        return bestF;
    }

    private KBSolution decrease(KBSolution sol, int ofTarget) {
        KBSolution sDec = new KBSolution(sol);
        int nDec = (int) Math.ceil(beta * sol.getInstance().getK());
        for (int i = 0; i < nDec; i++) {
            int next = selectBestToRemove(sDec, ofTarget);
            sDec.remove(next);
        }
        return sDec;
    }


//    private KBSolution decrease(KBSolution sol) {
//        KBSolution sDec = new KBSolution(sol);
//        int nDec = (int) Math.ceil(beta * sol.getInstance().getK());
//        List<Integer> cand = sol.getSelectedCopy();
//        Collections.shuffle(cand, RandomManager.getRandom());
//        for (int i = 0; i < nDec; i++) {
//            sDec.remove(cand.get(i));
//        }
//        return sDec;
//    }


    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (Constructive<KBInstance, KBSolution> constructive : c){
            stb.append(constructive).append(",");
        }
        for (Improvement<KBSolution> l : ls) {
            stb.append(l).append(",");
        }
        return this.getClass().getSimpleName()+"("+beta+","+stb.toString()+iters+")";
    }
}
