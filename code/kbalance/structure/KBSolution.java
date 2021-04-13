package kbalance.structure;

import grafo.optilib.structure.Solution;

import java.util.*;

public class KBSolution implements Solution {

    private KBInstance instance;

    private Map<Integer, Set<Integer>> demandsPerFacility;
    private int[] assignedFacility;
    private int[] of;
    private boolean updated;
    private int criticalFacilityF0;
    private int criticalFacilityF1;
    private int criticalDemandF0;

    public KBSolution(KBInstance instance) {
        this.instance = instance;
        demandsPerFacility = new HashMap<>();
        assignedFacility = new int[instance.getN()+1];
        of = new int[3];
    }

    public KBSolution(KBSolution sol) {
        copy(sol);
    }

    public void copy(KBSolution sol) {
        this.instance = sol.instance;
        this.demandsPerFacility = new HashMap<>(sol.demandsPerFacility.size());
        for (Map.Entry<Integer, Set<Integer>> entry : sol.demandsPerFacility.entrySet()) {
            this.demandsPerFacility.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        this.assignedFacility = sol.assignedFacility.clone();
        this.of = sol.of.clone();
        this.updated = sol.updated;
        this.criticalFacilityF0 = sol.criticalFacilityF0;
        this.criticalFacilityF1 = sol.criticalFacilityF1;
    }

    public void add(int f) {
        demandsPerFacility.put(f, new HashSet<>());
        assignFacilityToDemandsAdd(f);
        updated = false;
    }

    private void assignFacilityToDemandsAdd(int f) {
        int n = instance.getN();
        for (int d = 1; d <= n; d++) {
            if (assignedFacility[d] == 0) {
                // The demand point has no facility assigned (first facility added)
                assignedFacility[d] = f;
                demandsPerFacility.get(f).add(d);
            } else {
                int dist = instance.getDistance(d, f);
                int currDist = instance.getDistance(d, assignedFacility[d]);
                if (dist < currDist) {
                    // The new facility is closer
                    // In case of a tie it is assigned to the new one if it reduces the value of objective function 2
                    demandsPerFacility.get(assignedFacility[d]).remove(d);
                    demandsPerFacility.get(f).add(d);
                    assignedFacility[d] = f;
                }
            }
        }
    }

    public void remove(int f) {
        assignFacilityToDemandsRemove(f);
        demandsPerFacility.remove(f);
        updated = false;
    }

    private void assignFacilityToDemandsRemove(int fR) {
        for (int d : demandsPerFacility.get(fR)) {
            int minDist = Integer.MAX_VALUE;
            int bestF = -1;
            for (int f : demandsPerFacility.keySet()) {
                if (f == fR) continue;
                int dist = instance.getDistance(d, f);
                if (dist < minDist) {
                    // In case of a tie it is assigned to the new one if it reduces the value of objective function 2
                    minDist = dist;
                    bestF = f;
                }
            }
            if (bestF > 0) {
                assignedFacility[d] = bestF;
                demandsPerFacility.get(bestF).add(d);
            }
        }
    }

    public List<Integer> getSelectedCopy() {
        return new ArrayList<>(demandsPerFacility.keySet());
    }

    public Set<Integer> getSelected() {
        return demandsPerFacility.keySet();
    }

    public boolean contains(int f) {
        return demandsPerFacility.containsKey(f);
    }

    public float getMaxDist() {
        if (!updated) {
            evaluate();
            updated = true;
        }
        return of[0];
    }

    public int getMaxClientsPerFacility() {
        if (!updated) {
            evaluate();
            updated = true;
        }
        return of[1];
    }

    public int getMaxDiffClientsPerFacility() {
        if (!updated) {
            evaluate();
            updated = true;
        }
        return of[2];
    }

    public int getOF(int idx) {
        if (!updated) {
            evaluate();
            updated = true;
        }
        return (idx == 2) ? (of[1] - of[2]) : of[idx];
    }

    public float evalMixture(float beta) {
        if (!updated) {
            evaluate();
            updated = true;
        }
        float normOf0 = of[0] / (float) instance.getMaxDistance();
        float normOf1 = of[1] / (float) instance.getN();
        return beta * normOf0 + (1-beta) * normOf1;
    }

    public boolean isFeasible() {
        return demandsPerFacility.size() == instance.getK();
    }

    public void evaluate() {
        evaluateF0();
        evaluateF1();
    }

    private void evaluateF1() {
        of[1] = 0;
        of[2] = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Set<Integer>> entry : demandsPerFacility.entrySet()) {
            int size = entry.getValue().size();
            if (of[1] < size) {
                of[1] = size;
                criticalFacilityF1 = entry.getKey();
            }
            if (of[2] > size) {
                of[2] = size;
            }
        }
    }

    private void evaluateF0() {
        of[0] = 0;
        for (int d = 1; d < assignedFacility.length; d++) {
            int dist = instance.getDistance(d, assignedFacility[d]);
            if (dist > of[0]) {
                of[0] = dist;
                criticalFacilityF0 = assignedFacility[d];
                criticalDemandF0 = d;
            }
        }
    }

    public int getCriticalFacilityF0() {
        if (!updated) {
            evaluate();
        }
        return criticalFacilityF0;
    }

    public int getCriticalFacilityF1() {
        if (!updated) {
            evaluate();
        }
        return criticalFacilityF1;
    }

    public int getCriticalFacility(int of) {
        if (!updated) {
            evaluate();
        }
        return (of == 0) ? criticalFacilityF0 : criticalFacilityF1;
    }

    public int getCriticalDemandF0() {
        return criticalDemandF0;
    }

    public boolean validateOFNaive() {
        int[] naive = evaluateNaive();
        boolean ok = true;
        if (of[0] != naive[0]) {
            System.out.println("F0: "+of[0] + " vs "+naive[0]);
            ok = false;
        }
        if (of[1] != naive[1]) {
            System.out.println("F1: "+of[1] + " vs "+naive[1]);
            ok = false;
        }
        if (of[2] != naive[2]) {
            System.out.println("F2: "+of[2] + " vs "+naive[2]);
            ok = false;
        }
        return ok;
    }

    public int[] evaluateNaive() {
        int n = instance.getN();
        int[] of = new int[3];
        Map<Integer, Integer> demandsPerFacility = new HashMap<>();
        for (int f : this.demandsPerFacility.keySet()) {
            demandsPerFacility.put(f, 0);
        }
        System.out.println("NAIVE");
        for (int d = 1; d <= n; d++) {
            int assignedFacility = -1;
            int closestDistance = Integer.MAX_VALUE;
            for (int f : demandsPerFacility.keySet()) {
                int dist = instance.getDistance(d, f);
//                if (dist < closestDistance ||
//                        (dist == closestDistance && assignedFacility > 0 && demandsPerFacility.get(f) < demandsPerFacility.get(assignedFacility))) {
                if (dist < closestDistance) {
                    assignedFacility = f;
                    closestDistance = dist;
                }
            }
            System.out.println(d+" -> "+assignedFacility);
            demandsPerFacility.put(assignedFacility, demandsPerFacility.get(assignedFacility)+1);
            int dpf = demandsPerFacility.get(assignedFacility);
            if (closestDistance > of[0]) {
                of[0] = closestDistance;
            }
            if (dpf > of[1]) {
                of[1] = dpf;
            }
        }
//      EVALUATION OF F3 IS VERY COSTLY, IF IT IS NOT CONSIDERED IN OPTIMIZATION, EVALUATE WHEN REPORTING RESULTS ONLY
        for (Map.Entry<Integer, Integer> f1 : demandsPerFacility.entrySet()) {
            for (Map.Entry<Integer, Integer> f2 : demandsPerFacility.entrySet()) {
                if (f1.getKey() < f2.getKey()) {
                    int diff = Math.abs(f1.getValue()-f2.getValue());
                    if (diff > of[2]) {
                        of[2] = diff;
                    }
                }
            }
        }
        return of;
    }

    public int getAssignedFacility(int d) {
        if (!updated) {
            evaluate();
        }
        return assignedFacility[d];
    }

    public KBInstance getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (int f : demandsPerFacility.keySet()) {
            stb.append(f).append(" ");
        }
        return stb.toString();
    }
}
