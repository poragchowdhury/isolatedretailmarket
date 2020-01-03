package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysDefect extends Agent {
    public AlwaysDefect() {
        super("AlD", 0);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.D1;
    }
}
