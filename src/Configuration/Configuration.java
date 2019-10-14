package Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    public static int TOTAL_TIME_SLOTS = 0;
    public static int PUBLICATION_CYCLE = 0;
    public static double DEFAULT_TARIFF_PRICE = 1;
    public static int CASE_STUDY_NO = 0;
    public static double INERTIA = 0;
    public static double RATIONALITY = 0;
    public static int POPULATION = 10;
    public static double ACT_CHANGE_PERC = 0.2;
    public static String LOGFILENAME;
    public static double DAYMULT = 0;
    public static double DMNDMULT = 0;
    public static double INITCOST = 0;
    public static boolean DLOGGING = true;
    public static int ROUND = 1;

    public static double LEARNING_RATE = 0.9;
    public static double DISCOUNT_FACTOR = 1;

    public static String DB_NAME_TRAINING = "RL0";
    public static double MAX_TARIFF_PRICE = 0.5;
    public static boolean RL_TRAINING = false;
    public static int PPTS_DISCRTZD = 10;

    public static boolean GET_NASH_EQ = false;
    public static boolean MANUAL_NASH_EQ_SELECTION = false;
    public static int MAX_DQ_AGENTS_ALLOWED = 3;

    public static String print() {
        return "\n********************Configurations**********************\n" + "TOTAL_TIME_SLOTS : " + TOTAL_TIME_SLOTS + "\n" + "PUBLICATION_CYCLE : " + PUBLICATION_CYCLE + "\n" + "DEFAULT_TARIFF_PRICE : " + DEFAULT_TARIFF_PRICE + "\n" + "CASE_STUDY_NO : " + CASE_STUDY_NO + "\n" + "INERTIA : " + INERTIA + "\n" + "RATIONALITY : " + RATIONALITY + "\n" + "ACT_CHANGE_PERC : " + ACT_CHANGE_PERC + "\n" + "POPULATION : " + POPULATION + "\n" + "LOGFILENAME : " + LOGFILENAME + "\n" + "DAYMULT : "
                + DAYMULT + "\n" + "DMNDMULT : " + DMNDMULT + "\n" + "INITCOST : " + INITCOST + "\n" + "DLOGGING : " + DLOGGING + "\n" + "ROUND : " + ROUND + "\n" + "LEARNING_RATE : " + LEARNING_RATE + "\n" + "DISCOUNT_FACTOR : " + DISCOUNT_FACTOR + "\n" + "DB_NAME_TRAINING : " + DB_NAME_TRAINING + "\n" + "MAX_TARIFF_PRICE : " + MAX_TARIFF_PRICE + "\n" + "RL_TRAINING : " + RL_TRAINING + "\n" + "PPTS_DISCRTZD : " + PPTS_DISCRTZD + "\n" + "GET_NASH_EQ : " + GET_NASH_EQ
                + "MANUAL_NASH_EQ_SELECTION : " + MANUAL_NASH_EQ_SELECTION + "\n" + "MAX_DQ_AGENTS_ALLOWED : " + MAX_DQ_AGENTS_ALLOWED + "\n";
    }

    public Configuration() {
        Properties prop = new Properties();

        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            TOTAL_TIME_SLOTS = Integer.parseInt(prop.getProperty("total-timeslots"));
            PUBLICATION_CYCLE = Integer.parseInt(prop.getProperty("publication-cycle"));
            DEFAULT_TARIFF_PRICE = Double.parseDouble(prop.getProperty("default-tariff-price"));
            CASE_STUDY_NO = Integer.parseInt(prop.getProperty("case-study"));
            INERTIA = Double.parseDouble(prop.getProperty("inertia"));
            RATIONALITY = Double.parseDouble(prop.getProperty("rationality"));
            ACT_CHANGE_PERC = Double.parseDouble(prop.getProperty("act-change-percentage"));
            POPULATION = Integer.parseInt(prop.getProperty("population"));
            LOGFILENAME = prop.getProperty("logfilename");
            DAYMULT = Double.parseDouble(prop.getProperty("daymult"));
            DMNDMULT = Double.parseDouble(prop.getProperty("demandmult"));
            INITCOST = Double.parseDouble(prop.getProperty("initcost"));
            DLOGGING = Boolean.parseBoolean(prop.getProperty("detailedlogging"));
            ROUND = Integer.parseInt(prop.getProperty("round"));
            LEARNING_RATE = Double.parseDouble(prop.getProperty("learningrate"));
            DISCOUNT_FACTOR = Double.parseDouble(prop.getProperty("discountfactor"));
            DB_NAME_TRAINING = prop.getProperty("db-name-training");
            MAX_TARIFF_PRICE = Double.parseDouble(prop.getProperty("maxtariff"));
            RL_TRAINING = Boolean.parseBoolean(prop.getProperty("rl-training"));
            PPTS_DISCRTZD = Integer.parseInt(prop.getProperty("ppts_dscretzd"));
            GET_NASH_EQ = Boolean.parseBoolean(prop.getProperty("get-nash-eq"));
            MANUAL_NASH_EQ_SELECTION = Boolean.parseBoolean(prop.getProperty("manual-nash-eq-selection"));
            MAX_DQ_AGENTS_ALLOWED = Integer.parseInt(prop.getProperty("max-dq-agents-allowed"));
            // System.out.println("TOTAL_TIME_SLOTS " + TOTAL_TIME_SLOTS);
            // System.out.println("PUBLICATION_CYCLE " + PUBLICATION_CYCLE);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
