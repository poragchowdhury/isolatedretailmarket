package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE9 extends Agent{
	
	DQAgentL8_2 dqAgentL8_2;
	
	public SMNE9() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE9";
		dqAgentL8_2 = new DQAgentL8_2();
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
		double prDQL1 = (1/26)*100;
		int prDQL1Int = (int) prDQL1;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDQL1Int) {
			dqAgentL8_2.publishTariff(ob);
		}
		else {
			softMajority(ob);
		}
	}
	
}
