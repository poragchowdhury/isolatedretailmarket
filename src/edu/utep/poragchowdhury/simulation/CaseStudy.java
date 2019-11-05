package edu.utep.poragchowdhury.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.utep.poragchowdhury.agents.Grim;
import edu.utep.poragchowdhury.agents.NaiveProber;
import edu.utep.poragchowdhury.agents.Rand;
import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;

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

    public CaseStudy(List<Agent> pool1, List<Agent> pool2) {
        this.pool1 = pool1;
        this.pool2 = pool2;
    }

    public void addP1Strats(Agent... strats) {
        addP1Strats(Arrays.asList(strats));
    }

    public void addP1Strats(Collection<Agent> strats) {
        pool1.addAll(strats);
    }

    public void addP2Strats(Agent... strats) {
        addP2Strats(Arrays.asList(strats));
    }

    public void addP2Strats(Collection<Agent> strats) {
        pool2.addAll(strats);
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

        CaseStudy case0 = new CaseStudy();
        case0.addP1Strats(new Rand(), new Grim());
        case0.addP2Strats(new NaiveProber());
        caseStudies.add(0, case0);
    }
}