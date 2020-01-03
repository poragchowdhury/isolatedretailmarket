/**
 * Author: Jose G. Perez
 * Deep Q-Learning MDP for use with deeplearning4j to train
 */
package Agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.nn.api.NeuralNetwork;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscreteDense;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete.A3CConfiguration;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.Box;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.json.JSONObject;
import org.nd4j.linalg.learning.config.Adam;

import Configuration.Configuration;
import RetailMarketManager.RetailMarketManager;
import Tariff.TariffAction;

public class DQAgentMDP implements MDP<DQAgentState, Integer, DiscreteSpace> {
	public static QLearning.QLConfiguration QLConfig = QLearning.QLConfiguration.builder().seed(123)
			.maxEpochStep(Configuration.MaxEpochStep).maxStep(Configuration.MaxStep)
			.expRepMaxSize(Configuration.ExpRepMaxSize).batchSize(Configuration.BatchSize)
			.targetDqnUpdateFreq(Configuration.TargetDqnUpdateFreq).updateStart(0)
			.rewardFactor(Configuration.RewardFactor).gamma(Configuration.DISCOUNT_FACTOR).errorClamp(10.0) //
			.minEpsilon(Configuration.MinEpsilon).epsilonNbStep(Configuration.EpsilonNbStep).doubleDQN(true).build();

	public static A3CConfiguration QLConfig2 = A3CConfiguration.builder().seed(123).maxEpochStep(168 / 6)
			.maxStep(168 / 6).build();

	private static A3CDiscrete.A3CConfiguration A3C = new A3CDiscrete.A3CConfiguration(123, // Random seed
			(int) Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE, // 6
			(int) (Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS, // 500
			8, // Number of threads
			5, // t_max
			0, // num step noop warmup
			1, // reward scaling
			0.99, // gamma
			Double.MAX_VALUE // td-error clipping
	);
	public static AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration ASYNC_QLConfig = new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
			123, // Random seed
			Configuration.TOTAL_PUBLICATIONS_IN_A_GAME, // Max step By epoch
			Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * Configuration.TRAINING_ROUNDS, // Max step
			8, // Number of threads
			5, // t_max
			Configuration.TOTAL_PUBLICATIONS_IN_A_GAME, // target update (hard)
			0, // num step noop warmup
			1, // reward scaling
			0.99, // gamma
			Double.MAX_VALUE, // td-error clipping
			0.1f, // min epsilon
			Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * (Configuration.TRAINING_ROUNDS / 2) // num step for eps greedy
																								// anneal
	);

	public static DQNFactoryStdDense.Configuration QLNet = DQNFactoryStdDense.Configuration.builder().l2(0.001)
			.updater(new Adam(0.0005)).numHiddenNodes(Configuration.NUMBER_OF_NEURONS)
			.numLayer(Configuration.NUM_OF_HIDDEN_LAYER).build();

	public static ActorCriticFactorySeparateStdDense.Configuration QLNet2 = ActorCriticFactorySeparateStdDense.Configuration
			.builder().l2(0.0001).updater(new Adam(0.0005)).numHiddenNodes(16).numLayer(3).build();

	public static final int NUM_ACTIONS = 5;
	public static final int NUM_OBSERVATIONS = new DQAgentState().toArray().length;

	public static DiscreteSpace ACTION_SPACE = new DiscreteSpace(NUM_ACTIONS);
	public static ObservationSpace<DQAgentState> OBSERVATION_SPACE = new ArrayObservationSpace<DQAgentState>(
			new int[] { NUM_OBSERVATIONS });

	private static Logger log = Logger.getLogger("retailmarketmanager");

	public static void log(String message, Object... args) {
		Logger.getAnonymousLogger().info(String.format(message, args));
	}

	private RetailMarketManager retailManager;
	private DQAgent agent;
	private List<Agent> opponentPool;
	private String H = "";

	public DQAgentMDP(List<Agent> opponentPool) {
		this.opponentPool = opponentPool;
		this.reset();
	}

	public static void trainDQAgent(List<Agent> opponentPool, String policyFilename, String sourcePolicy) {
		log("Setting up DeepQ training");
		DQAgentMDP mdp = new DQAgentMDP(opponentPool);
		try {
			// record the training data in rl4j-data in a new folder
			DataManager manager = new DataManager(true);
			
			
			QLearningDiscreteDense<DQAgentState> dql;
			if(sourcePolicy == null) {
				DQN model = new DQN(NeuralNet.createMultiLayerNetwork());
				dql = new QLearningDiscreteDense<DQAgentState>(mdp, model, QLConfig, manager);
			}
			else {
				IDQN sourceQLNet = DQNPolicy.load(sourcePolicy).getNeuralNet();
				dql = new QLearningDiscreteDense<DQAgentState>(mdp, sourceQLNet, QLConfig, manager);
			}
		
			
//			try {
//				
//				DQNPolicy<DQAgentState> pol = DQNPolicy.load("main.pol");
//				IDQN oldNet = pol.getNeuralNet();
//				dql = new QLearningDiscreteDense<DQAgentState>(mdp, oldNet, QLConfig, manager);
//			} catch (Exception ex) {
//				dql = new QLearningDiscreteDense<DQAgentState>(mdp, QLNet, QLConfig, manager);
//			}

			//A3CDiscreteDense<DQAgentState> dqc = new A3CDiscreteDense<>(mdp, QLNet2, A3C, manager);
			//AsyncNStepQLearningDiscreteDense<DQAgentState> dql_asyn = new AsyncNStepQLearningDiscreteDense<DQAgentState>(mdp, QLNet, ASYNC_QLConfig,
			// manager);
			// define the training
			// dql.addListener(new Listener());

			// for (NeuralNetwork net : dql.getNeuralNet().getNeuralNetworks()) {
			// MultiLayerNetwork mln = (MultiLayerNetwork) net;
			// System.setProperty("org.deeplearning4j.ui.port", "8080");
			// UIServer uiServer = UIServer.getInstance();
			// StatsStorage statsStorage = new InMemoryStatsStorage();
			// uiServer.attach(statsStorage);
			//
			// StatsListener statsListener = new StatsListener(statsStorage);
			// mln.addListeners(statsListener);
			// }

			log("Training DeepQ");
			dql.addListener(new NeuralNetListener());
			dql.train();
			DQNPolicy<DQAgentState> pol = dql.getPolicy();

			/*
			Map<String, Stat> sorted = sortByValue(mdp.stats);
			for (String key : sorted.keySet()) {
				Stat s = sorted.get(key);
				log.info(key + ": Avg Reward =" + s.V() + ", " + s.N);
			}
			*/

			// log("QLCONFIG MAXSTEP" + QLConfig.getMaxStep());
			// dql_asyn.train();
			// DQNPolicy<DQAgentState> pol = (DQNPolicy<DQAgentState>) dql_asyn.getPolicy();

			//dqc.train();
			//ACPolicy<DQAgentState> pol = dqc.getPolicy();

			log("Saving DeepQ policy");
			//pol.save("pol\\", "pol1");
			pol.save(policyFilename);
//			pol.save("main.pol");

		} catch (IOException e) {
			e.printStackTrace();
		}
		mdp.close();
	}

	@Override
	public void close() {

	}

	@Override
	public DiscreteSpace getActionSpace() {
		return ACTION_SPACE;
	}

	@Override
	public ObservationSpace<DQAgentState> getObservationSpace() {
		return OBSERVATION_SPACE;
	}

	@Override
	public boolean isDone() {
		if (retailManager.ob.timeslot >= Configuration.TOTAL_TIME_SLOTS) {
			// simulation is ending
			// log.info(agent.getAllHistoryActions());
			return true;
		}

		return false;
	}

	@Override
	public MDP<DQAgentState, Integer, DiscreteSpace> newInstance() {
		DQAgentMDP mdp = new DQAgentMDP(this.opponentPool);
		return mdp;
	}

	@Override
	public DQAgentState reset() {
		// Properly reset agent variables between training epochs.
		for (Agent ag : opponentPool)
			ag.reset();

		this.agent = new DQAgent();
		this.retailManager = new RetailMarketManager();

		retailManager.ob.agentPool.add(agent);
		retailManager.ob.agentPool.addAll(opponentPool);
		retailManager.ob.timeslot = 0;
		H = "";
		cumReward = 0;
		
		Agent opponent = opponentPool.get(0);
		opponent.opponentID = agent.id;
		agent.opponentID = opponent.id;

		return new DQAgentState(agent, retailManager.ob);
	}

	public double cumReward = 0;
	public Map<String, Stat> stats = new HashMap<>();

	@Override
	public StepReply<DQAgentState> step(Integer actionInt) {

		// Special case to simulate the 0th timeslot
		// Where both of the agent has same tariff
		if (retailManager.ob.timeslot == 0) {
			retailManager.ob.updateAgentUnitCost();
			retailManager.customerTariffEvaluation();
			retailManager.updateAgentAccountings();
			retailManager.ob.timeslot++;
		}

		TariffAction action = TariffAction.valueOf(actionInt);

		double before = agent.profit;
		agent.playAction(retailManager.ob, action);

		// Perform other agent policies
		for (Agent ag : this.opponentPool) {
			try {
				ag.publishTariff(retailManager.ob);
			} catch (Exception ex) {
				System.out.printf("[Agent:%s raised an exception while publishing a tariff]\n");
				ex.printStackTrace();
			}
		}
		// Run the market evaluation based on the previous action
		// then run the rest of the timeslots so the next call to
		// this function will be a publication cycle
		for (int i = 0; i < Configuration.PUBLICATION_CYCLE; i++) {
			retailManager.ob.updateAgentUnitCost();
			retailManager.customerTariffEvaluation();
			retailManager.updateAgentAccountings();
			retailManager.ob.timeslot++;
		}
		double after = agent.profit;
		double reward = after;// / (Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION * retailManager.ob.fcc.maxUsage);
//		double reward = (after - before);
//				/ (Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION * retailManager.ob.fcc.maxUsage);
		// System.out.println("Reward " + reward);
		/*
		 * cumReward += reward; H += action.toString().substring(0, 2) + "_"; if
		 * (H.length() == (Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * 3)) { if
		 * (stats.containsKey(H)) { Stat s = stats.get(H); s.cummulative += cumReward;
		 * s.N++; stats.put(H, s); log.info("ActionHistory" + H + " reward " +
		 * s.cummulative); log.info("ActionHistory" + agent.getAllHistoryActions() +
		 * " profit " + agent.profit); log.info("MarketHistory" +
		 * agent.getHistoryByPubCyc(agent.marketShareHistory) + " profit " +
		 * agent.profit); } else { Stat s = new Stat(); s.cummulative = cumReward; s.N =
		 * 1; stats.put(H, s); } }
		 */
		// log.info("Reward: " + reward);
		DQAgentState nextState = new DQAgentState(agent, retailManager.ob);

		JSONObject info = new JSONObject("{}");
		return new StepReply<DQAgentState>(nextState, reward, this.isDone(), info);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	class Stat implements Comparable<Stat> {
		public double cummulative = 0;
		public double N = 0;

		public double V() {
			return cummulative / N;
		}

		@Override
		public int compareTo(Stat arg0) {
			return Double.compare(this.V(), arg0.V());
		}

	}
}
