package it.vinicioflamini.omt.notification.test;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.notification.kafka.channel.NotificationChannel;
import it.vinicioflamini.omt.notification.listener.GoodsShippedEventListener;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GoodsShippedEventListenerIntegrationTest.App.class, TestProducer.class, GoodsShippedEventListener.class })
@EnableBinding(NotificationChannel.class)
public class GoodsShippedEventListenerIntegrationTest {
	@SpringBootApplication(exclude = TestSupportBinderAutoConfiguration.class)
	static class App {

	}

	@Autowired
	private TestProducer producer;

	@Autowired
	private GoodsShippedEventListener consumer;

	private final static String TOPIC = NotificationChannel.INPUT_SHIPPING;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, TOPIC);

	@BeforeClass
	public static void setup() {
		System.setProperty("spring.cloud.stream.kafka.binder.brokers",
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
		System.setProperty("spring.cloud.stream.bindings.input.destination", TOPIC);
		System.setProperty("spring.cloud.stream.bindings.input.content-type", "text/plain");
		System.setProperty("spring.cloud.stream.bindings.input.group", "input-group-1");
		System.setProperty("spring.cloud.stream.bindings.output.destination", TOPIC);
		System.setProperty("spring.cloud.stream.bindings.output.content-type", "text/plain");
		System.setProperty("spring.cloud.stream.bindings.output.group", "output-group-1");
	}

	@Test
	public void testMessageSendShipmentProcessed() {
		OrderEvent event = new OrderEvent();
		event.setAction(Action.SHIPMENTPROCESSED);

		producer.getSource().inboundShipping().send(MessageBuilder.withPayload(event).setHeader("type", "string").build());

		waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			then(event).isEqualTo(consumer.getReceivedMessage());
		});
		
		assertEquals(Action.CUSTOMERNOTIFIEDSHIPMENTPROCESSED, consumer.getReceivedMessage().getAction());
	}
	
}
