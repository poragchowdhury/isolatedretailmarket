package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;
import org.jetbrains.annotations.NotNull;

public class TitForTat extends Agent {
    public int numTitsPerTat = 1;
    public int numTatsPerTit = 1;

    private int defectCounter = 0;
    private int punishCounter = 0;

    public TitForTat(int numTitsPerTat, int numTatsPerTit) {
        super("TFT_Temp", AgentID.TitForTat);

        this.name = numTitsPerTat + "TF" + numTatsPerTit + "T";
        this.numTitsPerTat = numTitsPerTat;
        this.numTatsPerTit = numTatsPerTit;
    }

    @NotNull
    @Override
    public TariffAction makeAction(@NotNull Observer ob) {
        // Coop in the first move
        if (ob.timeslot == 1) {
            return TariffAction.NC;

        } else if (punishCounter > 0) {
            punishCounter--;
            return TariffAction.D1;

        } else if (defectCounter == numTatsPerTit) {
            defectCounter = 0;
            punishCounter = numTitsPerTat;
            return TariffAction.D1;
        }
        // other agent is defecting, so defect
        else if (rivalActHistory[ob.timeslot - 1] == TariffAction.D1.index ||
                rivalActHistory[ob.timeslot - 1] == TariffAction.D2.index) {
            defectCounter++;
            return TariffAction.D1;
        }
        // other agent is increasing price
        else if (rivalActHistory[ob.timeslot - 1] == TariffAction.I1.index ||
                rivalActHistory[ob.timeslot - 1] == TariffAction.I2.index) {
            return TariffAction.I1;
        }
        return TariffAction.NC;
    }
}