package it.vinicioflamini.omt.orchestrator.integration.test;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;
import it.vinicioflamini.omt.orchestrator.listener.OrderNotPlacedEventListener;
import it.vinicioflamini.omt.orchestrator.rest.OrderRestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { OrderNotPlacedEventListenerIntegrationTest.App.class, TestProducer.class, OrderNotPlacedEventListener.class })
@EnableBinding(OrchestratorChannel.class)
public class OrderNotPlacedEventListenerIntegrationTest {
	@SpringBootApplication(exclude = TestSupportBinderAutoConfiguration.class)
	static class App {

	}

	@Autowired
	private TestProducer producer;

	@Autowired
	private OrderNotPlacedEventListener consumer;

	private final static String TOPIC = OrchestratorChannel.INPUT_ORDER;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, TOPIC);
	
	@MockBean
	private OrderRestClient orderRestClient;

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
	public void testOrderNotPlacedThenDoCompensateOrder() {
		OrderEvent event = new OrderEvent();
		event.setOrderId(10L);
		event.setAction(Action.ORDERNOTPLACED);
		
		OrderRequest req = new OrderRequest();
		req.setOrderId(event.getOrderId());

		producer.getSource().inboundOrder().send(MessageBuilder.withPayload(event).setHeader("type", "string").build());

		waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			then(event).isEqualTo(consumer.getReceivedMessage());
		});
		
		assertEquals(Action.ORDERNOTPLACED, consumer.getReceivedMessage().getAction());
		
		verify(orderRestClient, times(1)).compensateOrder(req);
	}

}
