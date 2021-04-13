package kbalance.test;

import kbalance.structure.KBInstance;
import kbalance.structure.KBSolution;

import java.util.ArrayList;
import java.util.Objects;

public class TestSolution {

    public static void main(String[] args) {
        int[] fac = new int[]{9, 26, 37, 40, 43, 55, 66, 67, 72, 79};
        String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/kbcl/two_of/S1_5000_100_10.txt";
        KBInstance instance = new KBInstance(path);
        KBSolution sol = new KBSolution(instance);
        for (int i = 0; i < fac.length; i++) {
            sol.add(fac[i]);
        }
        sol.evaluate();
        System.out.println(sol);
        System.out.println("F1 = "+sol.getMaxDist());
        System.out.println("F2 = "+sol.getMaxClientsPerFacility());
        System.out.println("F3 = "+sol.getMaxDiffClientsPerFacility());
    }

}
