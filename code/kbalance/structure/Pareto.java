package kbalance.structure;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pareto {

    private static List<KBSolution> front;
    private static boolean[] modifiedSinceLastAsk;

    public synchronized static void reset(int nFlags) {
        front = new ArrayList<>(1000);
        modifiedSinceLastAsk = new boolean[nFlags];
    }

    public synchronized static List<KBSolution> getFront() {
        return front;
    }

    public synchronized static boolean add(KBSolution newSol) {
        List<Integer> dominated = new ArrayList<>();
        boolean enter = true;
        int idx = 0;
        for (KBSolution frontSol : front) {
            int c0 = newSol.getOF(0) - frontSol.getOF(0);
            int c1 = newSol.getOF(1) - frontSol.getOF(1);
//            int c2 = newSol.getOF(2) - frontSol.getOF(2);
//            if (c0 >= 0 && c1 >= 0 && c2 >= 0) {
            if (c0 >= 0 && c1 >= 0) {
                // newSol esta dominada por una ya incluida en el frente
                enter = false;
                break;
//            } else if (c0 <= 0 && c1 <= 0 && c2 <= 0) {
            } else if (c0 <= 0 && c1 <= 0) {
                // newSol domina a la incluida
                dominated.add(idx);
            }
            idx++;
        }
        int removed = 0;
        for (int idRem : dominated) {
            front.remove(idRem-removed);
            removed++;
        }
        if (enter) {
            front.add(new KBSolution(newSol));
            Arrays.fill(modifiedSinceLastAsk, true);
        }

        return enter;
    }

    public static synchronized boolean isModifiedSinceLastAsk(int flag) {
        boolean ret = modifiedSinceLastAsk[flag];
        modifiedSinceLastAsk[flag] = false;
        return ret;
    }

    public static String toText() {
        StringBuilder stb = new StringBuilder();
        for (KBSolution sol : front) {
//            stb.append(sol.getOF(0)).append("\t").append(sol.getOF(1)).append("\t").append(sol.getOF(2)).append("\n");
            stb.append(sol.getOF(0)).append("\t").append(sol.getOF(1)).append("\n");
        }
        return stb.toString();
    }

    public static void saveToFile(String path) {
        if (path.lastIndexOf('/') > 0) {
            File folder = new File(path.substring(0, path.lastIndexOf('/')));
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        try {
            PrintWriter pw = new PrintWriter(path);
            pw.print(toText());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
