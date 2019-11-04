package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

public class Test extends Agent {

    public Test() {
        super("Dft_on_2");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	int pub_cyc_no = ob.timeslot/Configuration.PUBLICATION_CYCLE;
    	if(pub_cyc_no == 10) {
    		//System.out.println("TS " + ob.timeslot + " Test: DEFECT");
    		return TariffAction.DEFECT;
    	}
    	//System.out.println("TS " + ob.timeslot + " Test: NOCHANGE");
    	return TariffAction.NOCHANGE;
    }
}
