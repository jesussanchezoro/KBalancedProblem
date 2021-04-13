package kbalance.moeaframework;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KBalanceProblem2Obj implements Problem {

    private int n;
    private int m;
    private int k;
    private int[][] coordinatesDemand;
    private int[][] coordinatesFacility;
    private float[][] distance;
    private String name;

    public KBalanceProblem2Obj(String path) {
        super();
        load(path);
    }

    @Override
    public String getName() {
        return "KBALANCED";
    }

    @Override
    public int getNumberOfVariables() {
        return k;
    }

    @Override
    public int getNumberOfObjectives() {
        return 2;
    }

    @Override
    public int getNumberOfConstraints() {
        return 1;
    }

    @Override
    public void evaluate(Solution solution) {
        int[] facilities = EncodingUtils.getInt(solution);
        int repeated = repeatedFacilities(facilities);
        int[] of = evaluate(facilities);
        //System.out.println(Arrays.toString(of) + ", " + repeated + " -> " + Arrays.toString(facilities));
        solution.setObjective(0, of[0]);
        solution.setObjective(1, of[1]);
//        solution.setObjective(2, of[2]);
        solution.setConstraint(0, repeated);
    }

    private int[] distanceToClosest(int[] facilities, int d) {
        int minDist = Integer.MAX_VALUE;
        int fAss = -1;
        for (int fac : facilities) {
            int dist = (int) distance[d][fac];
            if (dist < minDist) {
                minDist = dist;
                fAss = fac;
            }
        }
        return new int[]{fAss, minDist};
    }

    private int[] evaluate(int[] facilities) {
        int of = 0;
        Map<Integer, Integer> assigned = new HashMap<>();
        for (int d = 1; d <= n; d++) {
            int[] fd = distanceToClosest(facilities, d);
            if (fd[1] > of) {
                of = fd[1];
            }
            assigned.putIfAbsent(fd[0], 0);
            assigned.put(fd[0], assigned.get(fd[0])+1);
        }
        int maxAss = 0;
        for (Map.Entry<Integer, Integer> entry : assigned.entrySet()) {
            maxAss = Math.max(entry.getValue(), maxAss);
        }
        return new int[]{of, maxAss};
    }

    private int repeatedFacilities(int[] facilities) {
        int repeated = 0;
        Set<Integer> selected = new HashSet<>();
        for (int f : facilities) {
            if (selected.contains(f)) {
                repeated++;
            }
            selected.add(f);
        }
        return repeated;
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(k, 2, 1);
        for (int i = 0; i < k; i++) {
            solution.setVariable(i, EncodingUtils.newInt(1, m));
        }
        return solution;
    }

    @Override
    public void close() {

    }

    public void load(String s) {
        if (s.contains("pmed")) {
            readPMedInstance(s);
        } else {
            loadSOTA(s);
        }
    }

    public void readPMedInstance(String s) {
        try (BufferedReader bf = new BufferedReader(new FileReader(s))) {
            name = s.substring(s.lastIndexOf("/"));
            String[] tokens = bf.readLine().trim().split("\\s+");
            n = Integer.parseInt(tokens[0]);
            m = Integer.parseInt(tokens[0]);
            k = Integer.parseInt(tokens[2]);
            coordinatesDemand = new int[n+1][2];
            coordinatesFacility = new int[m+1][2];
            distance = new float[n+1][m+1];
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    if (i == j) {
                        distance[i][j] = 0;
                    } else {
                        distance[i][j] = 0x3f3f3f;
                    }
                }
            }
            int lines = Integer.parseInt(tokens[1]);
            for (int i = 0; i < lines; i++) {
                tokens = bf.readLine().trim().split("\\s+");
                int u = Integer.parseInt(tokens[0]);
                int v = Integer.parseInt(tokens[1]);
                int d = Integer.parseInt(tokens[2]);
                distance[u][v] = d;
                distance[v][u] = d;
            }
            floydWarshall(distance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void floydWarshall(float[][] dist) {
        int n = dist.length-1;
        for (int k = 1; k <= n; k++) {
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    public void loadSOTA(String s) {
        try (BufferedReader bf = new BufferedReader(new FileReader(s))) {
            name = s.substring(s.lastIndexOf("/"));
            String[] tokens = bf.readLine().split("\\s+");
            n = Integer.parseInt(tokens[0]);
            m = Integer.parseInt(tokens[1]);
            k = Integer.parseInt(tokens[2]);
            coordinatesDemand = new int[n+1][2];
            coordinatesFacility = new int[m+1][2];
            for (int i = 1; i <= n; i++) {
                tokens = bf.readLine().split("\\s+");
                coordinatesDemand[i][0] = Integer.parseInt(tokens[0]);
                coordinatesDemand[i][1] = Integer.parseInt(tokens[1]);
            }
            for (int i = 1; i <= m; i++) {
                tokens = bf.readLine().split("\\s+");
                coordinatesFacility[i][0] = Integer.parseInt(tokens[0]);
                coordinatesFacility[i][1] = Integer.parseInt(tokens[1]);
            }
            evaluateDistances();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evaluateDistances() {
        distance = new float[n+1][m+1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                float d = euclideanDist(coordinatesDemand[i], coordinatesFacility[j]);
                distance[i][j] = d;
            }
        }
    }

    private float euclideanDist(int[] c1, int[] c2) {
        return (float) Math.sqrt(Math.pow(c1[0]-c2[0],2) + Math.pow(c1[1]-c2[1],2));
    }
}
