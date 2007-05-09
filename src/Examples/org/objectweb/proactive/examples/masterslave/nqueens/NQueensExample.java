package org.objectweb.proactive.examples.masterslave.nqueens;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Vector;

import org.objectweb.proactive.examples.masterslave.nqueens.query.Query;
import org.objectweb.proactive.examples.masterslave.nqueens.query.QueryGenerator;
import org.objectweb.proactive.extra.masterslave.ProActiveMaster;
import org.objectweb.proactive.extra.masterslave.TaskException;


/**
 * This examples calculates the Nqueen
 * @author fviale
 *
 */
public class NQueensExample {
    public static final int DEFAULT_NQUEEN_BOARD = 20;
    public static final int DEFAULT_NQUEEN_ALG_DEPTH = 3;
    public static URL descriptor_url;
    public static String vn_name;
    public static int nqueen_board_size;
    public static int nqueen_algorithm_depth;

    /**
     * Initializing the example with command line arguments
     * @param args
     * @throws MalformedURLException
     */
    public static void init(String[] args) throws MalformedURLException {
        if (args.length == 2) {
            descriptor_url = (new File(args[0])).toURL();
            ;
            vn_name = args[1];
            nqueen_board_size = DEFAULT_NQUEEN_BOARD;
            nqueen_algorithm_depth = DEFAULT_NQUEEN_ALG_DEPTH;
        } else if (args.length == 4) {
            descriptor_url = (new File(args[0])).toURL();
            vn_name = args[1];
            nqueen_board_size = Integer.parseInt(args[2]);
            nqueen_algorithm_depth = Integer.parseInt(args[3]);
        } else {
            System.out.println(
                "Usage: <java_command> descriptor_path virtual_node_name [nqueen_board_size nqueen_algorithm_depth]");
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        // Getting command line parameters
        init(args);

        // Creating the Master
        ProActiveMaster master = new ProActiveMaster(descriptor_url, vn_name);

        // Generating the queries for the NQueens
        Vector<Query> queries = QueryGenerator.generateQueries(nqueen_board_size,
                nqueen_algorithm_depth);
        System.out.println("Launching NQUEENS solutions finder for n = " +
            nqueen_board_size + " with a depth of " + nqueen_algorithm_depth);
        master.solveAll(queries);
        try {
            long sumResults = 0;
            long begin = System.currentTimeMillis();

            // Waiting for the results
            Collection<Long> results = master.waitAllResults();
            for (long res : results) {
                sumResults += res;
            }
            long end = System.currentTimeMillis();
            int nbslaves = master.slavepoolSize();

            long hours = (end - begin) / 3600000;
            System.out.println("" + hours +
                String.format("h %1$tMm %1$tSs %1$tLms", end - begin));

            System.out.println("Total number of configurations found in " +
                hours + String.format("h %1$tMm %1$tSs %1$tLms", end - begin) +
                " for n = " + nqueen_board_size + " and with " + nbslaves +
                " slaves : " + sumResults);
        } catch (TaskException e) {
            // Exception in the algorithm
            e.printStackTrace();
        }
        master.terminate(true);
        System.exit(0);
    }
}
