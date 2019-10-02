package Configuration;

import Observer.Observer;
import RetailMarketManager.RetailMarketManager;

import java.util.ArrayList;
import java.util.Collections;

import Agents.Agent;
import Agents.AlwaysDefect;
import Agents.AlwaysIncrease;
import Agents.AlwaysSame;
import Agents.DQAgent;
import Agents.DQAgentL1;
import Agents.DQAgentL10;
import Agents.DQAgentL11;
import Agents.DQAgentL12;
import Agents.DQAgentL13;
import Agents.DQAgentL14;
import Agents.DQAgentL15;
import Agents.DQAgentL16;
import Agents.DQAgentL17;
import Agents.DQAgentL18;
import Agents.DQAgentL19;
import Agents.DQAgentL2;
import Agents.DQAgentL20;
import Agents.DQAgentL21;
import Agents.DQAgentL22;
import Agents.DQAgentL23;
import Agents.DQAgentL2_2;
import Agents.DQAgentL3;
import Agents.DQAgentL4;
import Agents.DQAgentL5;
import Agents.DQAgentL6;
import Agents.DQAgentL7;
import Agents.DQAgentL8;
import Agents.DQAgentL8_2;
import Agents.DQAgentL9;
import Agents.DQAgentMDP;
import Agents.Grim;
import Agents.HardMajority;
import Agents.SMNE1;
import Agents.SMNE10;
import Agents.SMNE11;
import Agents.SMNE12;
import Agents.SMNE13;
import Agents.SMNE14;
import Agents.SMNE15;
import Agents.SMNE16;
import Agents.SMNE17;
import Agents.SMNE18;
import Agents.SMNE19;
import Agents.SMNE2;
import Agents.SMNE20;
import Agents.SMNE21;
import Agents.SMNE22;
import Agents.SMNE23;
import Agents.SMNE2_2;
import Agents.SMNE3;
import Agents.SMNE4;
import Agents.SMNE5;
import Agents.SMNE6;
import Agents.SMNE7;
import Agents.SMNE8;
import Agents.SMNE9;
import Agents.Rand;
import Agents.SoftMajority;
import Agents.NaiveProber;
import Agents.Pavlov;
import Agents.Prober;
import Agents.RL;
import Agents.RL1Fixed;
import Agents.RL2Fixed;
import Agents.RLTraining;
import Agents.TitForTat;
import Agents.TitForTatV2;
import Agents.TitForTwoTat;
import Agents.TwoTitForTat;
import Configuration.Configuration;

public class CaseStudy {
	
	public ArrayList<Agent> pool1 = new ArrayList<Agent>();
	public ArrayList<Agent> pool2 = new ArrayList<Agent>();
	
	public void configureSimulation(Observer ob, int case_study){
		
		if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.AlwaysCoopVSAlwaysCoop.getValue()){
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.AlwaysCoopVSAlwaysDefect.getValue()){
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysDefect());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.AlwaysDefectVSAlwaysCoop.getValue()){
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysSame());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.AlwaysDefectVSAlwaysDefect.getValue()){
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.AlwaysDefectVSTitForTat.getValue()){
			pool1.add(new AlwaysDefect());
			pool2.add(new TitForTat());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.AlwaysCoopVSTitForTat.getValue()){
			pool1.add(new AlwaysSame());
			pool2.add(new TitForTat());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.AlwaysDefectVSTitForTatV2.getValue()){
			pool1.add(new AlwaysDefect());
			pool2.add(new TitForTatV2());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RL1FixedVSAlD.getValue()){
			pool1.add(new RL1Fixed());
			pool2.add(new AlwaysDefect());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RL1FixedVSAlI.getValue()){
			pool1.add(new RL1Fixed());
			pool2.add(new AlwaysIncrease());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RLTrainingVSMNE1.getValue()){
			pool1.add(new RLTraining());
			pool2.add(new SMNE1());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RLTrainingVSMNE2.getValue()){
			pool1.add(new RLTraining());
			pool2.add(new SMNE2());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RL1FixedVSSMNE1.getValue()){
			pool1.add(new RL1Fixed());
			pool2.add(new SMNE1());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RL2FixedVSSMNE2.getValue()){
			pool1.add(new RL2Fixed());
			pool2.add(new SMNE2());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_AlI_NP_AlD_DQL1.getValue()) {
			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());

			pool1.add(new NaiveProber());
			pool2.add(new NaiveProber());
			
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());
			
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_AlI_NP_AlD_DQL1_Prbr.getValue()) {
			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());

			pool1.add(new NaiveProber());
			pool2.add(new NaiveProber());
			
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());
			
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_AlI_NP_AlD_DQL1_Prbr_Pavlov.getValue()) {
			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());

			pool1.add(new NaiveProber());
			pool2.add(new NaiveProber());
			
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());
			
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_AlI_NP_AlD_DQL1_Prbr_Pavlov_AlS.getValue()) {
			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());

			pool1.add(new NaiveProber());
			pool2.add(new NaiveProber());
			
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());
			
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_AlI_NP_AlD_DQL1_Prbr_Pavlov_AlS_SM.getValue()) {
			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());

			pool1.add(new NaiveProber());
			pool2.add(new NaiveProber());
			
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());
			
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_Prbr_Pavlov_AlS_SM.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL2());
			pool2.add(new DQAgentL2());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE2.getValue()) {
			pool1.add(new SMNE2());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE2_2.getValue()) {
			pool1.add(new SMNE2_2());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE3.getValue()) {
			pool1.add(new SMNE3());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2_2.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL2_2());
			pool2.add(new DQAgentL2_2());
		}
		
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2_2_DQ3.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL2_2());
			pool2.add(new DQAgentL2_2());
			
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE4.getValue()) {
			pool1.add(new SMNE4());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_Prbr_Pavlov_AlS_SM_DQL2_2_DQ3_DQ4.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL2_2());
			pool2.add(new DQAgentL2_2());
			
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL4());
			pool2.add(new DQAgentL4());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_SM_DQ3_DQ4.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL4());
			pool2.add(new DQAgentL4());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE5.getValue()) {
			pool1.add(new SMNE5());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_SM_DQ3_DQ4_DQ5.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL4());
			pool2.add(new DQAgentL4());

			pool1.add(new DQAgentL5());
			pool2.add(new DQAgentL5());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_SM_DQ4_DQ5.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL4());
			pool2.add(new DQAgentL4());

			pool1.add(new DQAgentL5());
			pool2.add(new DQAgentL5());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE6.getValue()) {
			pool1.add(new SMNE6());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_SM_DQ4_DQ5_DQ6.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL4());
			pool2.add(new DQAgentL4());

			pool1.add(new DQAgentL5());
			pool2.add(new DQAgentL5());
			
			pool1.add(new DQAgentL6());
			pool2.add(new DQAgentL6());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQL1_SM_DQ4_DQ6.getValue()) {
			pool1.add(new DQAgentL1());
			pool2.add(new DQAgentL1());
		
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL4());
			pool2.add(new DQAgentL4());

			pool1.add(new DQAgentL6());
			pool2.add(new DQAgentL6());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE7.getValue()) {
			pool1.add(new SMNE7());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_DQ7.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL7());
			pool2.add(new DQAgentL7());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_DQ7_GRIM.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL7());
			pool2.add(new DQAgentL7());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE8.getValue()) {
			pool1.add(new SMNE8());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_DQ7_GRIM_DQ8.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL7());
			pool2.add(new DQAgentL7());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8());
			pool2.add(new DQAgentL8());
			
			//pool1.add(new HardMajority());
			//pool2.add(new HardMajority());
		
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_DQ7_GRIM_DQ8_2.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL7());
			pool2.add(new DQAgentL7());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			//pool1.add(new HardMajority());
			//pool2.add(new HardMajority());
		
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE9.getValue()) {
			pool1.add(new SMNE9());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_DQ7_GRIM_DQ8_2_DQ9.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new DQAgentL7());
			pool2.add(new DQAgentL7());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_GRIM_DQ8_2_DQ9.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_GRIM_DQ8_2_DQ9_Rand.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());
			
			pool1.add(new Rand());
			pool2.add(new Rand());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE10.getValue()) {
			pool1.add(new SMNE10());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_GRIM_DQ8_2_DQ9_Rand_DQ10.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());
			
			pool1.add(new Rand());
			pool2.add(new Rand());
			
			pool1.add(new DQAgentL10());
			pool2.add(new DQAgentL10());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_GRIM_DQ8_2_DQ9_Rand_DQ10_TF2T.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());
			
			pool1.add(new Rand());
			pool2.add(new Rand());
			
			pool1.add(new DQAgentL10());
			pool2.add(new DQAgentL10());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_GRIM_DQ8_2_DQ9_DQ10_TF2T.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());
		
			pool1.add(new DQAgentL10());
			pool2.add(new DQAgentL10());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_GRIM_DQ8_2_DQ9_TF2T.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE11.getValue()) {
			pool1.add(new SMNE11());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ3_GRIM_DQ8_2_DQ9_TF2T_DQ11.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL3());
			pool2.add(new DQAgentL3());
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new DQAgentL11());
			pool2.add(new DQAgentL11());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_GRIM_DQ8_2_DQ9_TF2T_DQ11.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new DQAgentL11());
			pool2.add(new DQAgentL11());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE12.getValue()) {
			pool1.add(new SMNE12());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_GRIM_DQ8_2_DQ9_TF2T_DQ11_DQ12.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new DQAgentL11());
			pool2.add(new DQAgentL11());
			
			pool1.add(new DQAgentL12());
			pool2.add(new DQAgentL12());
		
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_GRIM_DQ8_2_DQ9_TF2T.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE13.getValue()) {
			pool1.add(new SMNE13());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_GRIM_DQ8_2_DQ9_TF2T_DQ13.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new DQAgentL13());
			pool2.add(new DQAgentL13());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_GRIM_DQ8_2_DQ9_TF2T_TFT.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE14.getValue()) {
			pool1.add(new SMNE14());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_GRIM_DQ8_2_DQ9_TF2T_TFT_DQ14.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new DQAgentL9());
			pool2.add(new DQAgentL9());

			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL14());
			pool2.add(new DQAgentL14());
			
		}
		
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_GRIM_DQ8_2_TF2T_TFT.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
		}
		
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ8_2_TF2T_TFT.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE15.getValue()) {
			pool1.add(new SMNE15());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ8_2_TF2T_TFT_DQ15.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE16.getValue()) {
			pool1.add(new SMNE16());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ8_2_TF2T_TFT_DQ15_DQ16.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new DQAgentL16());
			pool2.add(new DQAgentL16());
			
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE17.getValue()) {
			pool1.add(new SMNE17());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ8_2_TF2T_TFT_DQ15_DQ16_DQ17.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new DQAgentL16());
			pool2.add(new DQAgentL16());

			pool1.add(new DQAgentL17());
			pool2.add(new DQAgentL17());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ8_2_TF2T_TFT_DQ15_2TFT.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE18.getValue()) {
			pool1.add(new SMNE18());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_SM_DQ8_2_TF2T_TFT_DQ15_2TFT_DQ18.getValue()) {
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
		
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL18());
			pool2.add(new DQAgentL18());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ8_2_TF2T_TFT_DQ15_2TFT_DQ18.getValue()) {
			
			pool1.add(new DQAgentL8_2());
			pool2.add(new DQAgentL8_2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL18());
			pool2.add(new DQAgentL18());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_TF2T_TFT_DQ15_2TFT_DQ18.getValue()) {
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL18());
			pool2.add(new DQAgentL18());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE19.getValue()) {
			pool1.add(new SMNE19());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_TF2T_TFT_DQ15_2TFT_DQ18_DQ19.getValue()) {
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL18());
			pool2.add(new DQAgentL18());
			
			pool1.add(new DQAgentL19());
			pool2.add(new DQAgentL19());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_TF2T_TFT_DQ15_2TFT_DQ18_DQ19_TFTV2.getValue()) {
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL18());
			pool2.add(new DQAgentL18());
			
			pool1.add(new DQAgentL19());
			pool2.add(new DQAgentL19());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_TFT_DQ15_2TFT_DQ18_DQ19_TFTV2.getValue()) {
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL18());
			pool2.add(new DQAgentL18());
			
			pool1.add(new DQAgentL19());
			pool2.add(new DQAgentL19());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_TFT_DQ15_2TFT_DQ19_TFTV2.getValue()) {
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL19());
			pool2.add(new DQAgentL19());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_DQ19_TFTV2.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new DQAgentL19());
			pool2.add(new DQAgentL19());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());

		}	
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_TFTV2.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());

		}	
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE20.getValue()) {
			pool1.add(new SMNE20());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_TFTV2_DQ20.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new DQAgentL20());
			pool2.add(new DQAgentL20());

		}	
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_TFTV2_DQ20_HM.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new DQAgentL20());
			pool2.add(new DQAgentL20());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());

		}	
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_TFTV2_HM.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());

		}	
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE21.getValue()) {
			pool1.add(new SMNE21());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_TFTV2_HM_DQ21.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());
			
			pool1.add(new DQAgentL21());
			pool2.add(new DQAgentL21());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE22.getValue()) {
			pool1.add(new SMNE22());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_TFTV2_HM_DQ21_DQ22.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());
			
			pool1.add(new DQAgentL21());
			pool2.add(new DQAgentL21());

			pool1.add(new DQAgentL22());
			pool2.add(new DQAgentL22());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTrainingVSSMNE23.getValue()) {
			pool1.add(new SMNE23());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_DQ15_2TFT_TFTV2_HM_DQ23.getValue()) {
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());
			
			pool1.add(new DQAgentL23());
			pool2.add(new DQAgentL23());

		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_2TFT_TFTV2_HM.getValue()) {
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());
			
		}
		//else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RoundRobin.getValue()) {
		//}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RoundRobin.getValue()){
			
			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());
			
			pool1.add(new NaiveProber());
			pool2.add(new NaiveProber());
			
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());

			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new Rand());
			pool2.add(new Rand());
		
			pool1.add(new Prober());
			pool2.add(new Prober());
			
			
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
			
			pool1.add(new TitForTat());
			pool2.add(new TitForTat());
			
			pool1.add(new TitForTatV2());
			pool2.add(new TitForTatV2());
			
			pool1.add(new TitForTwoTat());
			pool2.add(new TitForTwoTat());			
			
			pool1.add(new TwoTitForTat());
			pool2.add(new TwoTitForTat());
			
			pool1.add(new Pavlov());
			pool2.add(new Pavlov());
			
			pool1.add(new DQAgentL15());
			pool2.add(new DQAgentL15());
			
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTraining.getValue()){
	        pool1.add(new SMNE1());
		    //pool2.add(new AlwaysDefect());
		    DQAgentMDP.trainDQAgent(pool1);
		    //pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQFixed.getValue()){
		    pool1.add(new SMNE1());
	        pool2.add(new DQAgentL1());
		}
		
		RetailMarketManager.numberofagents = pool1.size();		
		//Collections.shuffle(ob.agentPool);
	}
}
