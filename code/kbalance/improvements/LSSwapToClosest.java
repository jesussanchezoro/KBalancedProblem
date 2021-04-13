package kbalance.improvements;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;
import kbalance.structure.Pareto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
Selects the facility responsible for the f0 objective function value,
and iteratively swaps with the closest facility to the demand that was
causing the objective function value
 */
public class LSSwapToClosest implements Improvement<KBSolution> {


    @Override
    public void improve(KBSolution solution) {
        boolean improvement = true;
        while (improvement) {
            improvement = tryImprove(solution);
        }
    }

    private boolean tryImprove(KBSolution sol) {
        KBInstance instance = sol.getInstance();
        int f = sol.getCriticalFacility(0);
        int d = sol.getCriticalDemandF0();
        int critDist = instance.getDistance(d, f);
        sol.remove(f);
        for (int fCand : instance.getClosestFacilities(d)) {
            int dist = instance.getDistance(d, fCand);
            if (dist > critDist) return false; // Any solution would be worst
            sol.add(fCand);
            Pareto.add(sol);
            if (sol.getOF(0) < critDist) {
                System.out.println(critDist + " -> "+sol.getOF(0));
                return true;
            }
            sol.remove(fCand);
        }
        sol.add(f);
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
