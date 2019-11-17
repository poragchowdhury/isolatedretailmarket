package Agents;

import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingEpochEndEvent;
import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingEvent;
import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingListener;

public class Listener implements SyncTrainingListener {

	@Override
	public ListenerResponse onTrainingStart(SyncTrainingEvent event) {
		// TODO Auto-generated method stub
		return ListenerResponse.CONTINUE;
	}

	@Override
	public void onTrainingEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ListenerResponse onEpochStart(SyncTrainingEvent event) {
		// TODO Auto-generated method stub
		return ListenerResponse.CONTINUE;
	}

	@Override
	public ListenerResponse onEpochEnd(SyncTrainingEpochEndEvent event) {
		// if(event.getLearning().getNeuralNet().getLatestScore() < 0.0000000001) {
		// System.out.println("NN Loss is below threshold, stopping learning");
		// return ListenerResponse.STOP;
		// }
		return ListenerResponse.CONTINUE;
	}

}
