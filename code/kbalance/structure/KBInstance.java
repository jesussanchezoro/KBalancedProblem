package kbalance.structure;

import grafo.optilib.structure.Instance;
import jmetal.encodings.variable.Int;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KBInstance implements Instance {

    private int n;
    private int m;
    private int k;
    private int[][] coordinatesDemand;
    private int[][] coordinatesFacility;
    private float[][] distance;
    private String name;
    private List<Integer>[] facilityPerDemandSorted;

    private float maxDistance;

    public KBInstance(String p) {
        if (p.contains("pmed")) {
            readPMedInstance(p);
        } else {
            readInstance(p);
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public String getName() {
        return name;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public int getDistance(int d, int f) {
        return (int) distance[d][f];
    }

    private void evaluateDistances() {
        distance = new float[n+1][m+1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                float d = euclideanDist(coordinatesDemand[i], coordinatesFacility[j]);
                distance[i][j] = d;
                maxDistance = Math.max(d, maxDistance);
            }
        }
    }

    private float euclideanDist(int[] c1, int[] c2) {
        return (float) Math.sqrt(Math.pow(c1[0]-c2[0],2) + Math.pow(c1[1]-c2[1],2));
    }

    public int[] getCoordinatesDemand(int i) {
        return coordinatesDemand[i];
    }

    public int[] getCoordinatesFacility(int i) {
        return coordinatesFacility[i];
    }

    public int getIndexOfCoordDemand(int[] coord) {
        for (int i = 1; i <= n; i++) {
            if (coord[0] == coordinatesDemand[i][0] && coord[1] == coordinatesDemand[i][1]) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOfCoordFacility(int[] coord) {
        for (int i = 1; i <= m; i++) {
            if (coord[0] == coordinatesFacility[i][0] && coord[1] == coordinatesFacility[i][1]) {
                return i;
            }
        }
        return -1;
    }

    public List<Integer> getClosestFacilities(int d) {
        return facilityPerDemandSorted[d];
    }

    private void sortFacilityPerDemand() {
        for (int i = 1; i <= n; i++) {
            int finalI = i;
            facilityPerDemandSorted[i].sort((f1, f2) -> Float.compare(distance[finalI][f1], distance[finalI][f2]));
        }
    }

    @Override
    public void readInstance(String s) {
        try (BufferedReader bf = new BufferedReader(new FileReader(s))) {
            name = s.substring(s.lastIndexOf("/"));
            String[] tokens = bf.readLine().split("\\s+");
            n = Integer.parseInt(tokens[0]);
            m = Integer.parseInt(tokens[1]);
            facilityPerDemandSorted = new ArrayList[n+1];
            k = Integer.parseInt(tokens[2]);
            coordinatesDemand = new int[n+1][2];
            coordinatesFacility = new int[m+1][2];
            for (int i = 1; i <= n; i++) {
                tokens = bf.readLine().split("\\s+");
                coordinatesDemand[i][0] = Integer.parseInt(tokens[0]);
                coordinatesDemand[i][1] = Integer.parseInt(tokens[1]);
                facilityPerDemandSorted[i] = new ArrayList<>(m);
                for (int j = 1; j <= m; j++) {
                    facilityPerDemandSorted[i].add(j);
                }
            }
            for (int i = 1; i <= m; i++) {
                tokens = bf.readLine().split("\\s+");
                coordinatesFacility[i][0] = Integer.parseInt(tokens[0]);
                coordinatesFacility[i][1] = Integer.parseInt(tokens[1]);
            }
            evaluateDistances();
            sortFacilityPerDemand();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readPMedInstance(String s) {
        try (BufferedReader bf = new BufferedReader(new FileReader(s))) {
            name = s.substring(s.lastIndexOf("/"));
            String[] tokens = bf.readLine().trim().split("\\s+");
            n = Integer.parseInt(tokens[0]);
            m = Integer.parseInt(tokens[0]);
            facilityPerDemandSorted = new ArrayList[n+1];
            k = Integer.parseInt(tokens[2]);
            coordinatesDemand = new int[n+1][2];
            coordinatesFacility = new int[m+1][2];
            distance = new float[n+1][m+1];
            for (int i = 1; i <= n; i++) {
                facilityPerDemandSorted[i] = new ArrayList<>(n+1);
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

            sortFacilityPerDemand();
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
}
