package Agents;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;

public class RL2Fixed extends RL{
	public double maxppts = (Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION) / Configuration.PPTS_DISCRTZD;
	public int mynextaction = 0;
	public RL2Fixed() {
		super("rl2");
		this.name = "rl2";
	}
	
	@Override
	public void publishTariff(Observer ob) {
		try {
			publishTariffByActionId(ob, getNextActionId());
			//System.out.println("RL logic: action :"+ TariffActions.action.values()[mynextaction].toString() + " TariffPrice " + tariffPrice);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
}
