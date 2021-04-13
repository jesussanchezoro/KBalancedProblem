package kbalance.constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import kbalance.algorithms.parallel.Parallel_AlgConstLS;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;
import org.apache.poi.util.LittleEndianOutputStream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class C1 implements Constructive<KBInstance, KBSolution> {

    private class Candidate {
        int f;
        int cost;

        public Candidate(int f, int cost) {
            this.f = f;
            this.cost = cost;
        }
    }

    private final int ofTarget;
    private final float alpha;

    public C1(int ofTarget, float alpha) {
        this.ofTarget = ofTarget;
        this.alpha = alpha;
    }

    @Override
    public KBSolution constructSolution(KBInstance instance) {
        Random rnd = new Random(RandomManager.getRandom().nextInt());
        float realAlpha = (alpha < 0) ? rnd.nextFloat() : alpha;
        KBSolution sol = new KBSolution(instance);
        int m = instance.getM();
        int first = rnd.nextInt(m)+1;
        sol.add(first);
        List<Candidate> cl = createCL(sol, first);
        while (!sol.isFeasible()) {
            int gmin = cl.get(0).cost;
            int gmax = cl.get(cl.size()-1).cost;
            float mu = gmin + realAlpha * (gmax - gmin);
            int limit = 0;
            while (limit < cl.size() && cl.get(limit).cost <= mu) {
                limit++;
            }
            int idx = rnd.nextInt(limit);
            Candidate c = cl.remove(idx);
            sol.add(c.f);
            updateCL(sol, cl);
        }
        Pareto.add(sol);
        return sol;
    }

    private List<Candidate> createCL(KBSolution sol, int first) {
        KBInstance instance = sol.getInstance();
        int m = instance.getM();
        List<Candidate> cl = new ArrayList<>(m);
        for (int f = 1; f <= m; f++) {
            if (f == first) continue;
            sol.add(f);
            cl.add(new Candidate(f, sol.getOF(ofTarget)));
            sol.remove(f);
        }
        cl.sort(Comparator.comparingInt(c -> c.cost));
        return cl;
    }

    private void updateCL(KBSolution sol, List<Candidate> cl) {
        for (Candidate c : cl) {
            sol.add(c.f);
            c.cost = sol.getOF(ofTarget);
            sol.remove(c.f);
        }
        cl.sort(Comparator.comparingInt(c -> c.cost));
    }

    @Override
    public String toString() {
        String alphaSt = String.format("%.2f", alpha);
        return this.getClass().getSimpleName()+"("+ofTarget+","+alphaSt+")";
    }
}
