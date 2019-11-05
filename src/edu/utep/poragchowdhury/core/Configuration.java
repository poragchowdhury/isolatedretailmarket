package edu.utep.poragchowdhury.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
    public static int TEST_ROUNDS = 1;
    public static int TRAINING_ROUNDS = 1;

    public static double LEARNING_RATE = 0.9;
    public static double DISCOUNT_FACTOR = 1;

    public static String DB_NAME_TRAINING = "RL0";
    public static double MAX_TARIFF_PRICE = 0.5;
    public static boolean RL_TRAINING = false;
    public static int PPTS_DISCRTZD = 10;

    public static boolean GET_NASH_EQ = false;
    public static boolean MANUAL_NASH_EQ_SELECTION = false;
    public static int MAX_DQ_AGENTS_ALLOWED = 3;
    public static int TOTAL_PUBLICATIONS_IN_A_GAME = 1;
    public static boolean RUN_ONE_ITERATION = true;

    public static boolean VISUALIZE_MODEL_UI = false;
    public static boolean USE_ACTOR_CRITIC = false;

    public static String toStringRepresentation() {
        String p = "********************Configurations**********************\n";

        for (Field f : Configuration.class.getDeclaredFields()) {
            try {
                p += String.format("[%s = %s]\n", f.getName(), f.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {

            }
        }

        p += "********************Configurations**********************\n";

        return p;
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
            TEST_ROUNDS = Integer.parseInt(prop.getProperty("test-rounds"));
            TRAINING_ROUNDS = Integer.parseInt(prop.getProperty("dq-training-rounds"));
            LEARNING_RATE = Double.parseDouble(prop.getProperty("learningrate"));
            DISCOUNT_FACTOR = Double.parseDouble(prop.getProperty("discountfactor"));
            DB_NAME_TRAINING = prop.getProperty("db-name-training");
            MAX_TARIFF_PRICE = Double.parseDouble(prop.getProperty("maxtariff"));
            RL_TRAINING = Boolean.parseBoolean(prop.getProperty("rl-training"));
            PPTS_DISCRTZD = Integer.parseInt(prop.getProperty("ppts_dscretzd"));
            GET_NASH_EQ = Boolean.parseBoolean(prop.getProperty("get-nash-eq"));
            MANUAL_NASH_EQ_SELECTION = Boolean.parseBoolean(prop.getProperty("manual-nash-eq-selection"));
            MAX_DQ_AGENTS_ALLOWED = Integer.parseInt(prop.getProperty("max-dq-agents-allowed"));
            RUN_ONE_ITERATION = Boolean.parseBoolean(prop.getProperty("run-one-iteration"));
            // System.out.println("TOTAL_TIME_SLOTS " + TOTAL_TIME_SLOTS);
            // System.out.println("PUBLICATION_CYCLE " + PUBLICATION_CYCLE);
            TOTAL_PUBLICATIONS_IN_A_GAME = TOTAL_TIME_SLOTS / PUBLICATION_CYCLE;
            VISUALIZE_MODEL_UI = Boolean.parseBoolean(prop.getProperty("visualize-model-ui"));
            USE_ACTOR_CRITIC = Boolean.parseBoolean(prop.getProperty("use-actor-critic"));
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
