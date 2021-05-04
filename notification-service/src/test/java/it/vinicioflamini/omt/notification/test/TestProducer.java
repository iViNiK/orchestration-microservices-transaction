package it.vinicioflamini.omt.notification.test;

import org.springframework.cloud.stream.annotation.EnableBinding;

import it.vinicioflamini.omt.notification.kafka.channel.NotificationChannel;

@EnableBinding(NotificationChannel.class)
public class TestProducer {

	private NotificationChannel mySource;

	public TestProducer(NotificationChannel mySource) {
			super();
			this.mySource = mySource;
		}

	public NotificationChannel getSource() {
		return mySource;
	}

	public void setSource(NotificationChannel mysource) {
		mySource = mysource;
	}

}
