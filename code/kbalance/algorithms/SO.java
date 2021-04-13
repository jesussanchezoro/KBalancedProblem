package kbalance.algorithms;

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

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SO implements Algorithm<KBInstance> {

    private final Constructive<KBInstance, KBSolution> c[];
    private final Improvement<KBSolution> ls[];
    private final PR2 pr;
    private final int iters;
    private final float beta;

    public SO(Constructive<KBInstance, KBSolution> c[], Improvement<KBSolution> ls[], PR2 pr, float beta, int iters) {
        this.c = c;
        this.ls = ls;
        this.iters = iters;
        this.pr = pr;
        this.beta = beta;
    }

    @Override
    public Result execute(KBInstance instance) {
        Pareto.reset(1);
        System.out.print(instance.getName()+"\t"+instance.getN()+"\t"+instance.getM()+"\t"+instance.getK()+"\t");
        Result result = new Result(instance.getName());
        Timer.initTimer();
        // Construccion del frente inicial
        for (Constructive<KBInstance, KBSolution> ci : c) {
            for (int i = 0; i < iters; i++) {
                KBSolution sol = ci.constructSolution(instance);
                Pareto.add(sol);
                for (Improvement<KBSolution> lsi : ls) {
                    KBSolution solJ = new KBSolution(sol);
                    lsi.improve(solJ); // La BL ya incluye todas las soluciones en el frente
                }
            }
        }
        int prIters = 1;
//        while (Pareto.isGlobalModified()) {
        // TODO: Cuidado que esto no se cumple siempre!!
        while (Pareto.isModifiedSinceLastAsk(0)) {
            prIters++;
            // Incremento / Decremento de las soluciones
            List<KBSolution> source = new ArrayList<>(instance.getN());
            List<KBSolution> guiding = new ArrayList<>(instance.getN());
            for (KBSolution sol : Pareto.getFront()) {
                source.add(increase(sol));
                source.add(decrease(sol));
                guiding.add(sol);
            }

            // PR
            for (KBSolution sSource : source) {
                for (KBSolution sGuide : guiding) {
                    List<KBSolution> sols = pr.combine(sSource, sGuide);
                    for (KBSolution sol : sols) {
                        for (Improvement<KBSolution> l : ls) {
                            KBSolution lsSol = new KBSolution(sol);
                            l.improve(lsSol);
                        }
                    }
                }
            }
        }


        double secs = Timer.getTime()/1000.0;
        result.add("n", instance.getN());
        result.add("m", instance.getM());
        result.add("k", instance.getK());
        result.add("PR_IT", prIters);
        result.add("Time (s)", secs);
        System.out.println(prIters+"\t"+secs);
        String paretoPath = "pareto/"+this.toString();
        new File(paretoPath).mkdirs();
        Pareto.saveToFile(paretoPath+"/"+instance.getName());
        return result;
    }

    private KBSolution increase(KBSolution sol) {
        KBSolution sInc = new KBSolution(sol);
        int nInc = (int) Math.ceil(beta * sol.getInstance().getK());
        int m = sol.getInstance().getM();
        List<Integer> cand = new ArrayList<>(m);
        for (int i = 1; i <= m; i++) {
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

    private KBSolution decrease(KBSolution sol) {
        KBSolution sDec = new KBSolution(sol);
        int nDec = (int) Math.ceil(beta * sol.getInstance().getK());
        List<Integer> cand = sol.getSelectedCopy();
        Collections.shuffle(cand, RandomManager.getRandom());
        for (int i = 0; i < nDec; i++) {
            sDec.remove(cand.get(i));
        }
        return sDec;
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
