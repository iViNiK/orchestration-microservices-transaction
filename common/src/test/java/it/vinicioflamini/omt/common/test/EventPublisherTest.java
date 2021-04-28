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

import it.vinicioflamini.omt.common.domain.EventPublisher;
import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.repository.OutboxRepository;

@RunWith(SpringRunner.class)
public class EventPublisherTest {
	private class TestEntity {
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

	@MockBean
	private EventSource<TestEntity> eventSource;
	
	@MockBean
	private JpaRepository<Outbox, Long> outboxBaseRepository;
	
	@MockBean
	private OutboxRepository outboxRepository;
	
	@MockBean
	private JpaRepository<TestEntity, Long> domainObjectRepository;
	
	private EventPublisher<TestEntity> eventPublisher;
	
	@Before
	public void setUp() {
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository, domainObjectRepository); 
	}
	
	@Test
	public void testNoOutboxTransactionPending() {
		when(outboxRepository.pop()).thenReturn(null);
		eventPublisher.publish();
		verify(domainObjectRepository, times(0)).getOne(10L);
	}
	
	@Test
	public void testGetObjectOkPublishEventOk() {
		Outbox outbox = new Outbox();
		outbox.setId(100L);
		outbox.setDomainObjectId(1L);
		TestEntity testEntity = new TestEntity(1L);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
		when(eventSource.publishEvent(testEntity)).thenReturn(true);
		eventPublisher.publish();
		verify(outboxRepository, times(1)).save(outbox);
		verify(outboxRepository, times(1)).delete(outbox);
		assertTrue("Processing is FALSE", outbox.isProcessing());
	}

	@Test
	public void testGetObjectOkPublishEventKo() {
		Outbox outbox = new Outbox();
		outbox.setId(100L);
		outbox.setDomainObjectId(1L);
		TestEntity testEntity = new TestEntity(1L);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
		when(eventSource.publishEvent(testEntity)).thenReturn(false);
		eventPublisher.publish();
		verify(outboxRepository, times(2)).save(outbox);
		assertFalse("Processing is TRUE", outbox.isProcessing());
	}

	@Test
	public void testWhenGetObjectExceptionThenDeleteOutboxTransaction() {
		Outbox outbox = new Outbox();
		outbox.setId(100L);
		outbox.setDomainObjectId(1L);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenThrow(new EntityNotFoundException());
		eventPublisher.publish();
		verify(outboxRepository, times(1)).delete(outbox);
	}
	
}
