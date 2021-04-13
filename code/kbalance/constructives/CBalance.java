package kbalance.constructives;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CBalance implements Constructive<KBInstance, KBSolution> {

    private class Candidate {
        int f;
        int cost;

        public Candidate(int f, int cost) {
            this.f = f;
            this.cost = cost;
        }
    }

    private final float alpha;

    public CBalance(float alpha) {
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
            int gmax = cl.get(0).cost;
            int gmin = cl.get(cl.size()-1).cost;
            float mu = gmax - realAlpha * (gmax - gmin);
            int limit = 0;
            while (limit < cl.size() && cl.get(limit).cost >= mu) {
                limit++;
            }
            int idx = rnd.nextInt(limit);
            Candidate c = cl.remove(idx);
            sol.add(c.f);
            updateCL(sol, cl);
        }
        return sol;
    }


    /*
    The greedy function evaluates the number of demand points that
    the candidate facility is able to steal from the facility with
    the largest number of assigned demand points. This reduces the
    maximum number of assigned demand points.
     */
    private List<Candidate> createCL(KBSolution sol, int first) {
        KBInstance instance = sol.getInstance();
        int m = instance.getM();
        int n = instance.getN();
        List<Candidate> cl = new ArrayList<>(m);
        for (int f = 1; f <= m; f++) {
            if (f != first) {
                int cost = 0;
                for (int d = 1; d <= n; d++) {
                    int distToFirst = instance.getDistance(d, first);
                    int dist = instance.getDistance(d, f);
                    if (dist < distToFirst) {
                        cost++;
                    }
                }
                cl.add(new Candidate(f, cost));
            }
        }
        cl.sort(Comparator.comparingInt(c -> -c.cost));
        return cl;
    }

    /*
    The candidates are updated with the number of demand points that can be stolen
    from the facility with the largest numnber of assigned demand points to balance
    the load.
     */
    private void updateCL(KBSolution sol, List<Candidate> cl) {
        KBInstance instance = sol.getInstance();
        int n = instance.getN();
        int criticalF = sol.getCriticalFacilityF1();
        for (Candidate c : cl) {
            c.cost = 0;
            for (int d = 1; d <= n; d++) {
                int fAss = sol.getAssignedFacility(d);
                if (fAss == criticalF) { // If not, it is not in the objective function evaluation
                    int distToCritical = instance.getDistance(d, criticalF);
                    int distToCand = instance.getDistance(d, c.f);
                    if (distToCand < distToCritical) {
                        c.cost++;
                    }
                }
            }
        }
        cl.sort(Comparator.comparingInt(c -> -c.cost));
    }

    @Override
    public String toString() {
        String alphaSt = String.format("%.2f", alpha);
        return this.getClass().getSimpleName()+"("+alphaSt+")";
    }
}
