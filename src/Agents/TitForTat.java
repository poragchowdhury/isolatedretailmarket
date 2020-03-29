package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class TitForTat extends Agent {
    public boolean isV2 = false;
    public int numTitsPerTat = 1;
    public int numTatsPerTit = 1;

    public TitForTat(int numTitsPerTat, int numTatsPerTit) {
        super("temp", 12);
        String tempName = numTitsPerTat + "TF" + numTatsPerTit + "T";
        if (isV2)
            tempName += "V2";

        this.name = tempName;
        this.numTitsPerTat = numTitsPerTat;
        this.numTatsPerTit = numTatsPerTit;
    }

   
    /*
     * Inlcuding D2, I2 actions */
    @Override
    public TariffAction makeAction(Observer ob) {
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
        else if (rivalActHistory[ob.timeslot-1] == TariffAction.D1.index || 
        		rivalActHistory[ob.timeslot-1] == TariffAction.D2.index) {
            defectCounter++;
            return TariffAction.D1;
        }
        // other agent is increasing price
        else if (rivalActHistory[ob.timeslot-1] == TariffAction.I1.index ||
        		rivalActHistory[ob.timeslot-1] == TariffAction.I2.index) {
            return TariffAction.I1;
        }
        return TariffAction.NC;
    }
}