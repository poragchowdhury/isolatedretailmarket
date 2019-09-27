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
import Agents.DQAgentMDP;
import Agents.Grim;
import Agents.HardMajority;
import Agents.SMNE1;
import Agents.SMNE2;
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
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_AlD_AlI_AS.getValue()) {
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());

			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RR_AlD_AlI_AS_RL1.getValue()) {
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());

			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());
			
			pool1.add(new AlwaysSame());
			pool2.add(new AlwaysSame());
			
			pool1.add(new RL1Fixed());
			pool2.add(new RL1Fixed());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RoundRobin.getValue()) {
			
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.RoundRobin.getValue()){
			
			/*
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new SoftMajority());
			pool2.add(new SoftMajority());
			
			pool1.add(new Rand());
			pool2.add(new Rand());
		
			pool1.add(new L4());
			pool2.add(new L4());
			*/
			
			//pool1.add(new RL1Fixed());
			//pool2.add(new RL1Fixed());
			
			pool1.add(new AlwaysDefect());
			pool2.add(new AlwaysDefect());

			pool1.add(new AlwaysIncrease());
			pool2.add(new AlwaysIncrease());
			
			/*
			pool1.add(new Grim());
			pool2.add(new Grim());
			
			pool1.add(new HardMajority());
			pool2.add(new HardMajority());
			
			pool1.add(new NaiveProber());
			pool2.add(new NaiveProber());
			
			pool1.add(new Prober());
			pool2.add(new Prober());
			
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
			*/
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQTraining.getValue()){
	            pool1.add(new AlwaysDefect());
		    pool2.add(new AlwaysDefect());
		    
	            DQAgentMDP.trainDQAgent(pool1);
		    pool1.add(new DQAgent());
		    pool2.add(new DQAgent());
		}
		else if(Configuration.CASE_STUDY_NO == Configuration.CASE_STUDY.DQFixed.getValue()){
		    pool1.add(new AlwaysDefect());
	            pool2.add(new AlwaysDefect());
	            
	            pool1.add(new DQAgent());
	            pool2.add(new DQAgent());
		}
		
		RetailMarketManager.numberofagents = pool1.size();		
		//Collections.shuffle(ob.agentPool);
	}
}
