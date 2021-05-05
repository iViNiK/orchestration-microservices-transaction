package it.vinicioflamini.omt.orchestration.test;

import org.springframework.cloud.stream.annotation.EnableBinding;

import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;

@EnableBinding(OrchestratorChannel.class)
public class TestProducer {

	private OrchestratorChannel mySource;

	public TestProducer(OrchestratorChannel mySource) {
			super();
			this.mySource = mySource;
		}

	public OrchestratorChannel getSource() {
		return mySource;
	}

	public void setSource(OrchestratorChannel mysource) {
		mySource = mysource;
	}

}
