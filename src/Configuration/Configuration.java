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
	
	public static enum CASE_STUDY{
		RoundRobin(0),
		AlwaysCoopVSAlwaysCoop(1),
		AlwaysCoopVSAlwaysDefect(2),
		AlwaysDefectVSAlwaysCoop(3),
		AlwaysDefectVSAlwaysDefect(4),
		AlwaysDefectVSTitForTat(5),
		AlwaysCoopVSTitForTat(6),
		AlwaysDefectVSTitForTatV2(7),
		RLVSAlD(8),
		RLVSAlI(9),
		RLVSL1(10);
		private final int id;
		CASE_STUDY(int id) { this.id = id; }
	    public int getValue() { return id; }
	}
	
	public Configuration(){
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
			//System.out.println("TOTAL_TIME_SLOTS " + TOTAL_TIME_SLOTS);
			//System.out.println("PUBLICATION_CYCLE " + PUBLICATION_CYCLE);
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
