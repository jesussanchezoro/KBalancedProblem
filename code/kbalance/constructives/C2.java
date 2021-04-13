package kbalance.constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;

import java.awt.font.GlyphVector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class C2 implements Constructive<KBInstance, KBSolution> {

    private class Candidate {
        int f;
        int cost;

        public Candidate(int f, int cost) {
            this.f = f;
            this.cost = cost;
        }
    }

    private final float alpha;

    public C2(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public KBSolution constructSolution(KBInstance instance) {
        Random rnd = RandomManager.getRandom();
        float realAlpha = (alpha < 0) ? rnd.nextFloat() : alpha;
        KBSolution sol = new KBSolution(instance);
        int m = instance.getM();
        int first = rnd.nextInt(m)+1;
        sol.add(first);
        List<Candidate> cl = createCL(sol, first);
        while (!sol.isFeasible()) {
            int gmin = cl.get(0).cost;
            int gmax = cl.get(cl.size()-1).cost;
            float mu = gmax - realAlpha * (gmax - gmin);
            int limit = 0;
            while (limit < cl.size() && cl.get(limit).cost <= mu) {
                limit++;
            }
            int idx = rnd.nextInt(limit);
            Candidate c = cl.remove(idx);
            sol.add(c.f);
            updateCL(sol, cl);
        }
        return sol;
    }

    private List<Candidate> createCL(KBSolution sol, int first) {
        KBInstance instance = sol.getInstance();
        int m = instance.getM();
        List<Candidate> cl = new ArrayList<>(m);
        for (int f = 1; f <= m; f++) {
            if (f == first) continue;
            cl.add(new Candidate(f, greedyFunction(sol, f)));
        }
        cl.sort(Comparator.comparingInt(c -> -c.cost));
        return cl;
    }

    private int greedyFunction(KBSolution sol, int f) {
        KBInstance instance = sol.getInstance();
        int n = instance.getN();
        int gValue = 0;
        for (int d = 1; d <= n; d++) {
            int distToAF = instance.getDistance(d, sol.getAssignedFacility(d));
            int dist = instance.getDistance(d, f);
            if (dist < distToAF) {
                gValue++;
            }
        }
        return gValue;
    }

    private void updateCL(KBSolution sol, List<Candidate> cl) {
        for (Candidate c : cl) {
            c.cost = greedyFunction(sol, c.f);
        }
        cl.sort(Comparator.comparingInt(c -> -c.cost));
    }

    @Override
    public String toString() {
        String alphaSt = String.format("%.2f", alpha);
        return this.getClass().getSimpleName()+"("+alphaSt+")";
    }
}
