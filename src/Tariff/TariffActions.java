package Tariff;

import Configuration.Configuration;

public class TariffActions {
	public static double [] a = {0, -0.01, 0.01};
	public static enum action {NOCHANGE, DEFECT, INCREASE};
	//public static double INCREASE = 0.025;
	//public static double COOP = 0;
	//public static double DEFECT = -0.025;		
	public TariffActions() {
		// TODO Auto-generated constructor stub
		//INCREASE = (Configuration.DEFAULT_TARIFF_PRICE*Configuration.ACT_CHANGE_PERC)/100;
		//COOP = 0;
		//DEFECT = (-(Configuration.DEFAULT_TARIFF_PRICE*Configuration.ACT_CHANGE_PERC))/100;
	}
}
