package Agents;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.util.Constants;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NeuralNet {
    public static final double LEARNING_RATE = 0.0005;
    public static final double L2_RATE = 0.001;

    public static final int[] INPUT_SHAPE = DQAgentMDP.OBSERVATION_SPACE.getShape();
    public static final int INPUT_NUM = DQAgentMDP.NUM_OBSERVATIONS;
    public static final int OUTPUT_NUM = DQAgentMDP.NUM_ACTIONS;

    private static final int LAYER0_NEURONS = 13;
    private static final int LAYER1_NEURONS = 13;
    private static final int LAYER2_NEURONS = 13;

    private static DenseLayer layer0 = new DenseLayer.Builder()
            .nIn(INPUT_NUM)
            .nOut(LAYER0_NEURONS)
//            .dropOut(0.8)
            .activation(Activation.RELU).build();

    private static DenseLayer layer1 = new DenseLayer.Builder()
            .nIn(layer0.getNOut())
            .nOut(LAYER1_NEURONS)
            .dropOut(0.8)
            .activation(Activation.RELU).build();
    
    private static DenseLayer layer2 = new DenseLayer.Builder()
            .nIn(layer1.getNOut())
            .nOut(LAYER2_NEURONS)
            .dropOut(0.8)
            .activation(Activation.RELU).build();

    private static OutputLayer layerOutput = new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
            .nIn(layer0.getNOut())
            .nOut(OUTPUT_NUM)
            .activation(Activation.IDENTITY)
            .build();

    public static final MultiLayerConfiguration NEURAL_NET_CONFIG = new NeuralNetConfiguration.Builder()
            .seed(Constants.NEURAL_NET_SEED)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            // .updater(Updater.NESTEROVS).momentum(0.9)
            // .updater(Updater.RMSPROP).rho(conf.getRmsDecay())//.rmsDecay(conf.getRmsDecay())
            .updater(new Adam(LEARNING_RATE))
            .weightInit(WeightInit.XAVIER)
            .l2(L2_RATE)
            .list()
            /*
             * Network Layers
             * Can also use LSTM.Builder() or RnnOutputLayer.Builder()
             * More details in ActorCriticFactorySeparateStdDense
             */
//            .layer(0, layer0)
//            .layer(1, layer1)
            .layer(0, layer0)
            .layer(1, layerOutput)
            .build();

    public static ActorCriticFactorySeparateStdDense.Configuration A3C_NET_FACTORY_CONFIG = ActorCriticFactorySeparateStdDense.Configuration.builder()
            .l2(0.0001)
            .updater(new Adam(0.0005))
            .numHiddenNodes(16)
            .numLayer(3)
            .build();

    public static MultiLayerNetwork createMultiLayerNetwork() {
        MultiLayerNetwork model = new MultiLayerNetwork(NEURAL_NET_CONFIG);

        // Set-up UI browser model visualization
        // https://deeplearning4j.org/docs/latest/deeplearning4j-nn-visualization
		// if (Configuration.VISUALIZE_MODEL_UI) {
		// System.setProperty("org.deeplearning4j.ui.port", "8080");
		// UIServer uiServer = UIServer.getInstance();
		// StatsStorage statsStorage = new InMemoryStatsStorage();
		// uiServer.attach(statsStorage);
		//
		// StatsListener statsListener = new StatsListener(statsStorage);
		// model.addListeners(statsListener);
		// }
        // Score listener provided by deeplearning4j. Prints the model score every N iterations
         int numIterations = 10;
         ScoreIterationListener scoreListener = new ScoreIterationListener(numIterations);
         model.addListeners(scoreListener);

        model.init();
        return model;
    }
}