package edu.utep.poragchowdhury.agents.deepq;

import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete.A3CConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;

import edu.utep.poragchowdhury.core.Configuration;

public class QLearningConfig {

    /**
     * The configuration of the QLearning algorithm
     */
    public static QLearning.QLConfiguration REGULAR = QLearning.QLConfiguration.builder()
            .seed(123)
            .maxEpochStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME)
            .maxStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * Configuration.TRAINING_ROUNDS)
            .expRepMaxSize(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * 100)
            .batchSize(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * 10)
            .targetDqnUpdateFreq(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME / 4)
            .updateStart(0)
            .rewardFactor(1f)
            .gamma(0.99f)
            .errorClamp(Double.MAX_VALUE)
            .minEpsilon(0.01f)
            .epsilonNbStep((Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * (Configuration.TRAINING_ROUNDS)))
            .doubleDQN(true)
            .build();

    /**
     * The configuration of the Actor Critic algorithm
     */
    public static A3CConfiguration ACTOR_CRITIC = A3CConfiguration.builder()
            .seed(123)
            .maxEpochStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME)
            .maxStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * Configuration.TRAINING_ROUNDS)
            .numThread(8)
            .nstep(5)
            .updateStart(0)
            .rewardFactor(1)
            .gamma(0.99)
            .errorClamp(Double.MAX_VALUE)
            .build();
}