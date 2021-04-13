package kbalance.experiments;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Experiment;
import kbalance.algorithms.AlgConstLS_MO;
import kbalance.algorithms.AlgConstNObj;
import kbalance.algorithms.parallel.Parallel_AlgConstLS;
import kbalance.algorithms.parallel.Parallel_AlgConstNObj;
import kbalance.algorithms.parallel.Parallel_AlgConstSingleObj;
import kbalance.algorithms.parallel.Parallel_SO;
import kbalance.constructives.C1;
import kbalance.constructives.CBalance;
import kbalance.constructives.CDistance;
import kbalance.constructives.CHybrid;
import kbalance.improvements.*;
import kbalance.structure.KBInstance;
import kbalance.structure.KBInstanceFactory;

import java.io.File;
import java.util.Calendar;

/**
 * Created by jesussanchezoro on 14/09/2017.
 */
public class ExperimentAlgorithm {

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String date = String.format("%04d-%02d-%02d", year, month, day);

        KBInstanceFactory factory = new KBInstanceFactory();

        String instanceSet = (args.length>0)?args[0]:"pmed";
        String dir = ((args.length>0)?args[1]:"../instancias/kbcl/")+instanceSet;
        String outDir = "./experiments/"+date;
        File outDirCreator = new File(outDir);
        outDirCreator.mkdirs();
        String[] extensions = new String[]{".txt"};

        int constructions = 33;

        Algorithm<KBInstance>[] execution = new Algorithm[] {
                // PRELIMINAR - CONSTRUCTIVE
//                new Parallel_AlgConstSingleObj(new C1(0, 0.25f), 100, 0),
//                new Parallel_AlgConstSingleObj(new C1(0, 0.50f), 100, 0),
//                new Parallel_AlgConstSingleObj(new C1(0, 0.75f), 100, 0),
//                new Parallel_AlgConstSingleObj(new C1(0, -1.00f), 100, 0),
//                new Parallel_AlgConstSingleObj(new C1(1, 0.25f), 100, 1),
//                new Parallel_AlgConstSingleObj(new C1(1, 0.50f), 100, 1),
//                new Parallel_AlgConstSingleObj(new C1(1, 0.75f), 100, 1),
//                new Parallel_AlgConstSingleObj(new C1(1, -1.00f), 100, 1),
//                new Parallel_AlgConstSingleObj(new C1(2, 0.25f), 100, 2),
//                new Parallel_AlgConstSingleObj(new C1(2, 0.50f), 100, 2),
//                new Parallel_AlgConstSingleObj(new C1(2, 0.75f), 100, 2),
//                new Parallel_AlgConstSingleObj(new C1(2, -1.00f), 100, 2),

                // PRELIMINAR - BETA VALUE
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(1), new LSF2(1)},
//                        0.1f,
//                        constructions
//                ),
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(1), new LSF2(1)},
//                        0.2f,
//                        constructions
//                ),
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(1), new LSF2(1)},
//                        0.3f,
//                        constructions
//                ),
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(1), new LSF2(1)},
//                        0.3f,
//                        constructions
//                ),
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(1), new LSF2(1)},
//                        0.4f,
//                        constructions
//                ),
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(1), new LSF2(1)},
//                        0.5f,
//                        constructions
//                ),

                // PRELIMINARY - LS EXPLORATION
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(0.25f), new LSF2(0.25f)},
//                        0.1f,
//                        constructions
//                ),
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(0.50f), new LSF2(0.50f)},
//                        0.1f,
//                        constructions
//                ),
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1f), new C1(2, -1f)},
//                        new Improvement[]{new LSF1(0.75f), new LSF2(0.75f)},
//                        0.1f,
//                        constructions
//                ),

                // FINAL
//                new Parallel_SO(
//                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1), new C1(2, -1)},
//                        new Improvement[]{new LSF1(0.5f), new LSF2(0.5f)},
//                        0.1f,
//                        constructions
//                ),
                new Parallel_SO(
                        new Constructive[]{new C1(0, 0.5f), new C1(1, -1), new C1(2, -1)},
                        new Improvement[]{new LSF1(1), new LSF2(1)},
                        0.1f,
                        constructions
                ),


        };

        for (Algorithm<KBInstance> algorithm : execution) {
            new File(outDir).mkdirs();
            String outputFile = outDir + "/" + algorithm.toString() + ".xlsx";
            Experiment<KBInstance, KBInstanceFactory> experiment = new Experiment<>(algorithm, factory);
            experiment.launch(dir, outputFile, extensions);
        }

    }


}
