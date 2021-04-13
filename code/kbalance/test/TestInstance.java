package kbalance.test;

import kbalance.structure.KBInstance;

public class TestInstance {

    public static void main(String[] args) {
        String path = "/Users/jesus.sanchezoro/IdeaProjects/instancias/kbcl/all/A3_7500_150_75.txt";
        KBInstance instance = new KBInstance(path);
        int n = instance.getN();
        int m = instance.getM();
        for (int d = 1; d <= n; d++) {
            for (int f = 1; f <= m; f++) {
                int dist = instance.getDistance(d, f);

            }
        }
    }
}
