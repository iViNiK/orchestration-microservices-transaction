package it.vinicioflamini.omt.common.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.EventPublisher;
import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.repository.OutboxRepository;

@RunWith(SpringRunner.class)
public class EventPublisherTest {
	public class TestEntity {
		private Long id;

		public TestEntity(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}

	public class TestEventSourceOk implements EventSource<TestEntity> {

		@Override
		public boolean publishEvent(TestEntity sourceObject, OrderEvent orderEvent) {
			return true;
		}
	}

	public class TestEventSourceKo implements EventSource<TestEntity> {

		@Override
		public boolean publishEvent(TestEntity sourceObject, OrderEvent orderEvent) {
			return false;
		}
	}

	@MockBean
	private JpaRepository<Outbox, Long> outboxBaseRepository;

	@MockBean
	private OutboxRepository outboxRepository;

	@MockBean
	private JpaRepository<TestEntity, Long> domainObjectRepository;

	private EventPublisher<TestEntity> eventPublisher;
	
	private Outbox outbox;

	@Test
	public void testNoOutboxTransactionPending() {
		eventPublisher = new EventPublisher<TestEntity>(new TestEventSourceOk(), outboxRepository,
				domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(null);
		eventPublisher.publish();
		verify(domainObjectRepository, times(0)).getOne(10L);
	}

	@Before
	public void setup() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setAction(Action.ITEMFETCHED);
		
		outbox = new Outbox();
		outbox.setId(100L);
		outbox.setDomainObjectId(1L);
		try {
			outbox.setOrderEvent(objectMapper.writeValueAsString(orderEvent));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetObjectOkPublishEventOk() {
		TestEntity testEntity = new TestEntity(1L);
		eventPublisher = new EventPublisher<TestEntity>(new TestEventSourceOk(), outboxRepository,
				domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
		eventPublisher.publish();
		verify(outboxRepository, times(1)).save(outbox);
		verify(outboxRepository, times(1)).delete(outbox);
		assertTrue("Processing is FALSE", outbox.isProcessing());
	}

	@Test
	public void testGetObjectOkPublishEventKo() {
		TestEntity testEntity = new TestEntity(1L);
		eventPublisher = new EventPublisher<TestEntity>(new TestEventSourceKo(), outboxRepository, domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
		eventPublisher.publish();
		verify(outboxRepository, times(2)).save(outbox);
		assertFalse("Processing is TRUE", outbox.isProcessing());
	}

	@Test
	public void testWhenGetObjectExceptionThenDeleteOutboxTransaction() {
		eventPublisher = new EventPublisher<TestEntity>(new TestEventSourceOk(), outboxRepository, domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenThrow(new EntityNotFoundException());
		eventPublisher.publish();
		verify(outboxRepository, times(1)).delete(outbox);
		assertTrue("Processing is FALSE", outbox.isProcessing());
	}

}
