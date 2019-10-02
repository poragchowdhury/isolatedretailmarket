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
	
	public static String DQLEARNING_POLICY_FILENAME = "dq.pol";
	public static boolean GET_NASH_EQ = false;
	
	public static enum CASE_STUDY{
		RoundRobin(0),
		AlwaysCoopVSAlwaysCoop(1),
		AlwaysCoopVSAlwaysDefect(2),
		AlwaysDefectVSAlwaysCoop(3),
		AlwaysDefectVSAlwaysDefect(4),
		AlwaysDefectVSTitForTat(5),
		AlwaysCoopVSTitForTat(6),
		AlwaysDefectVSTitForTatV2(7),
		RL1FixedVSAlD(8),
		RL1FixedVSAlI(9),
		RLTrainingVSMNE1(10),
		RLTrainingVSMNE2(11),
		RR_AlI_NP_AlD_DQL1(12),
		RR_AlI_NP_AlD_DQL1_Prbr(13),
		RL1FixedVSSMNE1(14),
		RL2FixedVSSMNE2(15),
		DQTraining(16),
		DQFixed(17),
		RR_AlI_NP_AlD_DQL1_Prbr_Pavlov(18),
		RR_AlI_NP_AlD_DQL1_Prbr_Pavlov_AlS(19),
		RR_AlI_NP_AlD_DQL1_Prbr_Pavlov_AlS_SM(20),
		RR_DQL1_Prbr_Pavlov_AlS_SM(21),
		DQTrainingVSSMNE2(22),
		RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2(23),
		DQTrainingVSSMNE2_2(24),
		RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2_2(25),
		DQTrainingVSSMNE3(26),
		RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2_2_DQ3(27),
		DQTrainingVSSMNE4(28),
		RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2_2_DQ3_DQ4(29),
		RR_DQL1_SM_DQ3_DQ4(30),
		DQTrainingVSSMNE5(31),
		RR_DQL1_SM_DQ3_DQ4_DQ5(32),
		RR_DQL1_SM_DQ4_DQ5(33),
		DQTrainingVSSMNE6(34),
		RR_DQL1_SM_DQ4_DQ5_DQ6(35),
		RR_DQL1_SM_DQ4_DQ6(36),
		RR_SM_DQ3(37),
		DQTrainingVSSMNE7(38),
		RR_SM_DQ3_DQ7(39),
		RR_SM_DQ3_DQ7_GRIM(40),
		DQTrainingVSSMNE8(41),
		RR_SM_DQ3_DQ7_GRIM_DQ8(42),
		RR_SM_DQ3_DQ7_GRIM_DQ8_2(43),
		DQTrainingVSSMNE9(44),
		RR_SM_DQ3_DQ7_GRIM_DQ8_2_DQ9(45),
		RR_SM_DQ3_GRIM_DQ8_2_DQ9(46),
		RR_SM_DQ3_GRIM_DQ8_2_DQ9_Rand(47),
		DQTrainingVSSMNE10(48),
		RR_SM_DQ3_GRIM_DQ8_2_DQ9_Rand_DQ10(49),
		RR_SM_DQ3_GRIM_DQ8_2_DQ9_Rand_DQ10_TF2T(50),
		RR_SM_DQ3_GRIM_DQ8_2_DQ9_DQ10_TF2T(51),
		RR_SM_DQ3_GRIM_DQ8_2_DQ9_TF2T(52),
		DQTrainingVSSMNE11(53),
		RR_SM_DQ3_GRIM_DQ8_2_DQ9_TF2T_DQ11(54),
		RR_SM_GRIM_DQ8_2_DQ9_TF2T_DQ11(55),
		DQTrainingVSSMNE12(56),
		RR_SM_GRIM_DQ8_2_DQ9_TF2T_DQ11_DQ12(57),
		RR_SM_GRIM_DQ8_2_DQ9_TF2T(58),
		DQTrainingVSSMNE13(59),
		RR_SM_GRIM_DQ8_2_DQ9_TF2T_DQ13(60),
		RR_SM_GRIM_DQ8_2_DQ9_TF2T_TFT(61),
		DQTrainingVSSMNE14(62),
		RR_SM_GRIM_DQ8_2_DQ9_TF2T_TFT_DQ14(63),
		RR_SM_GRIM_DQ8_2_TF2T_TFT(64),
		RR_SM_DQ8_2_TF2T_TFT(65),
		DQTrainingVSSMNE15(66),
		RR_SM_DQ8_2_TF2T_TFT_DQ15(67),
		DQTrainingVSSMNE16(68),
		RR_SM_DQ8_2_TF2T_TFT_DQ15_DQ16(69),
		DQTrainingVSSMNE17(70),
		RR_SM_DQ8_2_TF2T_TFT_DQ15_DQ16_DQ17(71),
		RR_SM_DQ8_2_TF2T_TFT_DQ15_2TFT(72),
		DQTrainingVSSMNE18(73),
		RR_SM_DQ8_2_TF2T_TFT_DQ15_2TFT_DQ18(74),
		RR_DQ8_2_TF2T_TFT_DQ15_2TFT_DQ18(75),
		RR_TF2T_TFT_DQ15_2TFT_DQ18(76),
		DQTrainingVSSMNE19(77),
		RR_TF2T_TFT_DQ15_2TFT_DQ18_DQ19(78),
		RR_TF2T_TFT_DQ15_2TFT_DQ18_DQ19_TFTV2(79),
		RR_TFT_DQ15_2TFT_DQ18_DQ19_TFTV2(80),
		RR_TFT_DQ15_2TFT_DQ19_TFTV2(81),
		RR_DQ15_2TFT_DQ19_TFTV2(82),
		RR_DQ15_2TFT_TFTV2(83),
		DQTrainingVSSMNE20(84),
		RR_DQ15_2TFT_TFTV2_DQ20(85),
		RR_DQ15_2TFT_TFTV2_DQ20_HM(86),
		RR_DQ15_2TFT_TFTV2_HM(87),
		DQTrainingVSSMNE21(88),
		RR_DQ15_2TFT_TFTV2_HM_DQ21(89),
		DQTrainingVSSMNE22(90),
		RR_DQ15_2TFT_TFTV2_HM_DQ21_DQ22(91),
		DQTrainingVSSMNE23(92),
		RR_DQ15_2TFT_TFTV2_HM_DQ23(93),
		RR_2TFT_TFTV2_HM(94);
		private final int id;
		CASE_STUDY(int id) { this.id = id; }
	    public int getValue() { return id; }
	}
	
	public static String print() {
		return "\n********************Configurations**********************\n" +
				"TOTAL_TIME_SLOTS : " + TOTAL_TIME_SLOTS + "\n" +
				"PUBLICATION_CYCLE : " + PUBLICATION_CYCLE + "\n" +
				"DEFAULT_TARIFF_PRICE : " + DEFAULT_TARIFF_PRICE + "\n" +
				"CASE_STUDY_NO : " + CASE_STUDY_NO + "\n" +
				"INERTIA : " + INERTIA + "\n" +
				"RATIONALITY : " + RATIONALITY + "\n" +
				"ACT_CHANGE_PERC : " + ACT_CHANGE_PERC + "\n" +
				"POPULATION : " + POPULATION + "\n" +
				"LOGFILENAME : " + LOGFILENAME + "\n" +
				"DAYMULT : " + DAYMULT + "\n" +
				"DMNDMULT : " + DMNDMULT  + "\n" +
				"INITCOST : " + INITCOST  + "\n" +
				"DLOGGING : " + DLOGGING  + "\n" +		
				"ROUND : " + ROUND  + "\n" +
				"LEARNING_RATE : " + LEARNING_RATE  + "\n" +
				"DISCOUNT_FACTOR : " + DISCOUNT_FACTOR  + "\n" +
				"DB_NAME_TRAINING : " + DB_NAME_TRAINING  + "\n" +
				"MAX_TARIFF_PRICE : " + MAX_TARIFF_PRICE  + "\n" +
				"RL_TRAINING : " + RL_TRAINING  + "\n" +
				"PPTS_DISCRTZD : " + PPTS_DISCRTZD  + "\n" +
				"DQLEARNING_POLICY_FILENAME : " + DQLEARNING_POLICY_FILENAME + "\n" +
				"GET_NASH_EQ : " + GET_NASH_EQ;
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
			DQLEARNING_POLICY_FILENAME = prop.getProperty("dqlearning-policy-filename");
			GET_NASH_EQ = Boolean.parseBoolean(prop.getProperty("get-nash-eq"));
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
