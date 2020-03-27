package BranchWork;

import Agents.Agent;
import Agents.DQAgent;
import Configuration.CaseStudy;
import RetailMarketManager.RetailMarketManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Gambit {
    private static final String TEMP_FILENAME = "gambit.nfg";
    private static final String GAMBIT_PROCESS = "gambit-enummixed";
    private static Logger log = Logger.getLogger("retailmarketmanager");

    /**
     * Creates a human readable representation of the nash equilibrium strategies
     * list for the current case study
     *
     * @param cs               Current case study being simulated
     * @param nashEqStrategies The strategies which will be printed
     * @return A string representation of the nash equilibrium table
     */
    public static String nashEqToString(CaseStudy cs, List<double[]> nashEqStrategies) {
        StringBuilder result = new StringBuilder();

        // Table header
        StringBuilder header = new StringBuilder();
        for (Agent agent : cs.pool1) {
            if (agent instanceof DQAgent)
                header.append(((DQAgent) agent).getSimpleName()).append(", ");
            else
                header.append(agent.name).append(", ");
        }
        result.append(header.substring(0, header.length() - 2));

        // Table
        for (int strategyIDX = 0; strategyIDX < nashEqStrategies.size(); strategyIDX++) {
            double[] nashEq = nashEqStrategies.get(strategyIDX);
            StringBuilder output = new StringBuilder(strategyIDX + " [");
            for (double d : nashEq)
                output.append(String.format("%.3f, ", d));
            result.append(output.substring(0, output.length() - 2)).append("]");
        }

        return result.toString();
    }

    /**
     * Computes the Nash Equilibrium strategies of the given case study simulation
     * Calls gambit-enummixed internally
     *
     * @param rm
     * @param cs
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<double[]> getNashEqStrategies(RetailMarketManager rm, CaseStudy cs) throws IOException, InterruptedException {
        createGambitFile(rm, cs);
        List<double[]> nashEq = callGambit(cs);
        removeGambitFile();
        return nashEq;
    }

    private static List<double[]> callGambit(CaseStudy cs) throws IOException, InterruptedException {
        List<double[]> nashEqStrategies = new ArrayList<>();
        log.info("Sending file to command-line tool");

        // Gambit process call
        ProcessBuilder pb = new ProcessBuilder(GAMBIT_PROCESS, TEMP_FILENAME, "-q");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        // Gambit output parsing
        log.info("Reading from command-line output");
        while ((line = reader.readLine()) != null) {
            String[] nashEqRaw = line.split(",");
            // String nashEqName = nashEqRaw[0];

            double[] nashEqStrategy = new double[cs.pool1.size()];
            for (int i = 1; i <= cs.pool1.size(); i++) {
                double n = Utilities.parseFractionString(nashEqRaw[i]);
                nashEqStrategy[i - 1] = n;
            }

            nashEqStrategies.add(nashEqStrategy);
        }
        process.waitFor();
        return nashEqStrategies;
    }

    private static void createGambitFile(RetailMarketManager rm, CaseStudy cs) throws IOException {
        log.info("Creating Gambit file");
        FileWriter fw = new FileWriter(TEMP_FILENAME);
        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
        pw.println("NFG 1 R \"IPD NFG\" { \"Player 1\" \"Player 2\" } " + "{ " + cs.pool1.size() + " " + cs.pool2.size() + " }");
        for (int k = 0; k < cs.pool1.size(); k++)
            for (int i = 0; i < cs.pool2.size(); i++)
                pw.print(rm.gameMatrix[i][k].value1 + " " + rm.gameMatrix[i][k].value2 + " ");
        pw.close();
        fw.close();
    }

    private static void removeGambitFile() {
        new File(TEMP_FILENAME).delete();
    }

    public static List<double[]> getPureStrategies(List<double[]> nashStrats) {
        List<double[]> result = new ArrayList<>();
        for (double[] strat : nashStrats) {
            boolean isPure = true;
            // If all the numbers are integers, then it's a pure strategy
            for (double n : strat) {
                boolean isInteger = (n % 1 == 0);
                if (!isInteger) {
                    isPure = false;
                    break;
                }
            }
            if (isPure)
                result.add(strat);
        }
        return result;
    }

    public static List<double[]> getMixedStrategies(List<double[]> nashStrats) {
        List<double[]> result = new ArrayList<>();
        for (double[] strat : nashStrats) {
            boolean isPure = true;
            // If all the numbers are integers, then it's a pure strategy
            for (double n : strat) {
                boolean isInteger = (n % 1 == 0);
                if (!isInteger) {
                    isPure = false;
                    break;
                }
            }
            if (!isPure)
                result.add(strat);
        }
        return result;
    }
}
