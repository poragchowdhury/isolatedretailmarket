package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysIncrease extends Agent {
    public AlwaysIncrease() {
        // TODO Auto-generated constructor stub
        super("AlzIncz");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.I1;
    }
}
