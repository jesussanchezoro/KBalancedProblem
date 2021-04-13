package kbalance.improvements;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.Timer;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

import java.util.*;

public class LSF2 implements Improvement<KBSolution> {

    private class Candidate {
        int f;
        int cost;

        public Candidate(int f, int cost) {
            this.f = f;
            this.cost = cost;
        }
    }

    private float explorePercent;

    public LSF2(float explorePercent) {
        this.explorePercent = explorePercent;
    }

    @Override
    public void improve(KBSolution sol) {
        boolean improve = true;
        while (improve) {
            improve = tryImprove(sol);
            if (Timer.timeReached()) break;
        }
    }

    /*
    - Nodes in solution are sorted by number of assigned facilities
    - Nodes candidates are sorted by the number of facilities that they would steal to the most loaded
    facility
     */
    private boolean tryImprove(KBSolution sol) {
        KBInstance instance = sol.getInstance();
        int n = instance.getN();
        int m = instance.getM();
        int k = instance.getK();
        Map<Integer, Candidate> assigned = new HashMap<>(k);
        Map<Integer, Candidate> candEnter = new HashMap<>(k);
        for (int f = 1; f <= m; f++) {
            if (sol.contains(f)) {
                assigned.put(f, new Candidate(f, 0));
            } else {
                candEnter.put(f, new Candidate(f, 0));
            }
        }
        int criticalF = sol.getCriticalFacilityF1();
        for (int d = 1; d <= n; d++) {
            int fAss = sol.getAssignedFacility(d);
            assigned.get(fAss).cost++;
            if (fAss == criticalF) {
                int distToAssigned = instance.getDistance(d, fAss);
                for (Map.Entry<Integer, Candidate> entry : candEnter.entrySet()) {
                    int distToCand = instance.getDistance(d, entry.getKey());
                    if (distToCand < distToAssigned) {
                        entry.getValue().cost++;
                    }
                }
            }
        }
        List<Candidate> cRem = new ArrayList<>(assigned.values());
        cRem.sort(Comparator.comparingInt(c -> c.cost)); // From less to more assigned facilities
        List<Candidate> cAdd = new ArrayList<>(candEnter.values());
        cAdd.sort(Comparator.comparingInt(c -> -c.cost)); // From more to less number of stolen demand points
        int bestOF = sol.getOF(1);
        int limitCRem = (int) Math.ceil(cRem.size() * explorePercent);
        int limitCAdd = (int) Math.ceil(cAdd.size() * explorePercent);
        for (int i = 0; i < limitCRem; i++) {
            Candidate cR = cRem.get(i);
            sol.remove(cR.f);
            for (int j = 0; j < limitCAdd; j++) {
                Candidate cA = cAdd.get(j);
                sol.add(cA.f);
                Pareto.add(sol);
                if (sol.getOF(1) < bestOF) {
                    return true;
                }
                sol.remove(cA.f);
                if (Timer.timeReached()) break;
            }
            sol.add(cR.f);
            if (Timer.timeReached()) break;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+String.format("%.2f", explorePercent)+")";
    }
}
