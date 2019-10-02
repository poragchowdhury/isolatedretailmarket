package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE8 extends Agent{
	
	DQAgentL3 dqAgentL3;
	DQAgentL7 dqAgentL7;
	
	public SMNE8() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE8";
		dqAgentL3 = new DQAgentL3();
		dqAgentL7 = new DQAgentL7();
	}

	@Override
	public void publishTariff(Observer ob) {
		try {
			strategySMNE2(ob);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		tariffCheck(ob); /* Tariff Check */
	}
	
	public void strategySMNE2(Observer ob) throws Exception {
		double prDQL1 = (1/2)*100;
		int prDQL1Int = (int) prDQL1;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDQL1Int) {
			dqAgentL3.publishTariff(ob);
		}
		else {
			dqAgentL7.publishTariff(ob);
		}
	}
	
}
