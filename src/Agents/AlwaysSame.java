package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysSame extends Agent {

    public AlwaysSame() {
        super("AlzSame");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.NC;
    }
}
