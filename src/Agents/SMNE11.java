package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE11 extends Agent{
	DQAgentL9 dqagentL9;
	public SMNE11() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE11";
		dqagentL9 = new DQAgentL9();
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
		double prDQL1 = (5/6)*100;
		int prDQL1Int = (int) prDQL1;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDQL1Int) {
			tf2t(ob);
		}
		else {
			dqagentL9.publishTariff(ob);
		}
	}
	
}
