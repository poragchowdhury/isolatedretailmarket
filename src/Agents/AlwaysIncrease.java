package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysIncrease extends Agent {
    public AlwaysIncrease() {
        // TODO Auto-generated constructor stub
        super("AlI", 13);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.I1;
    }
}
