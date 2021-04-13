package kbalance.improvements;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import kbalance.structure.KBSolution;

public class LSMixture implements Improvement<KBSolution> {

    private float beta;

    public LSMixture(float beta) {
        this.beta = beta;
    }

    @Override
    public void improve(KBSolution sol) {
        float realBeta = (beta<0) ? RandomManager.getRandom().nextFloat() : beta;
        boolean improve = true;
        while (improve) {
            improve = false;

        }
    }
}
