package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysDefect extends Agent {
    public AlwaysDefect() {
        super("AlD");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.DEFECT;
    }
}
