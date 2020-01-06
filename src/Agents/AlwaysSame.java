package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysSame extends Agent {

    public AlwaysSame() {
        super("AlS", 2);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.NC;
    }
}
