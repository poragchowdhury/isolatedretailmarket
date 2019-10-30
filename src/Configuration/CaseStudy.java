package Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Agents.Agent;
import Agents.AlwaysDefect;
import Agents.AlwaysIncrease;

public class CaseStudy {
    /**
     * The strategy pool of P1
     */
    public List<Agent> pool1;

    /**
     * The strategy pool of P2
     */
    public List<Agent> pool2;

    public CaseStudy() {
        pool1 = new ArrayList<>();
        pool2 = new ArrayList<>();
    }

    public CaseStudy addP1Strats(Agent... strats) {
        pool1.addAll(Arrays.asList(strats));
        return this;
    }

    public CaseStudy addP2Strats(Agent... strats) {
        pool2.addAll(Arrays.asList(strats));
        return this;
    }

    private static List<CaseStudy> caseStudies = null;

    public static CaseStudy getFromIndex(int idx) {
        if (caseStudies == null)
            initCaseStudies();

        if (idx < 0 || idx >= caseStudies.size()) {
            System.out.printf("Case Study %s is out of range, there are only %s case studies.\n", idx, caseStudies.size());
            return null;
        }

        return caseStudies.get(idx);
    }

    public static CaseStudy getFromConfiguration() {
        int idx = Configuration.CASE_STUDY_NO;
        return getFromIndex(idx);
    }

    public static void initCaseStudies() {
        caseStudies = new ArrayList<>();

        CaseStudy case0 = new CaseStudy().addP1Strats(new AlwaysDefect(), new AlwaysIncrease()); // , new NaiveProber())
        case0.addP2Strats(new AlwaysDefect(), new AlwaysIncrease()); //, new NaiveProber()
        caseStudies.add(0, case0);
    }
}