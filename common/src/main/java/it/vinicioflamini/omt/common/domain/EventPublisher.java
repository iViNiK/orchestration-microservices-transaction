package it.vinicioflamini.omt.common.domain;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.repository.OutboxRepository;

public class EventPublisher<T> {

	private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

	private EventSource<T> eventSource;
	private OutboxRepository outboxRepository;
	private JpaRepository<T, Long> domainObjectRepository;
	private T domainObject;

	public EventPublisher(EventSource<T> eventSource, OutboxRepository outboxRepository,
			JpaRepository<T, Long> domainObjectRepository) {
		super();
		Assert.notNull(eventSource, "EventSource must not be NULL");
		Assert.notNull(outboxRepository, "OutboxRepository must not be NULL");
		Assert.notNull(domainObjectRepository, "DomainObjectRepository must not be NULL");
		this.eventSource = eventSource;
		this.outboxRepository = outboxRepository;
		this.domainObjectRepository = domainObjectRepository;
	}

	public EventPublisher(EventSource<T> eventSource, OutboxRepository outboxRepository) {
		super();
		Assert.notNull(eventSource, "EventSource must not be NULL");
		Assert.notNull(outboxRepository, "OutboxRepository must not be NULL");
		this.eventSource = eventSource;
		this.outboxRepository = outboxRepository;
	}

	@Scheduled(cron = "${application.outbox.polling.cron}")
	@Transactional
	public void publish() {
		Outbox o = outboxRepository.pop();

		if (o != null) {
			try {
				o.setProcessing(true);
				o = outboxRepository.save(o);

				if (o.getOrderEvent() == null) {
					throw new EntityNotFoundException("OrderEvent is NULL");
				}

				if (o.getDomainObjectId() != null) {
					if (domainObjectRepository != null) {
						domainObject = domainObjectRepository.getOne(o.getDomainObjectId());
					}
				} else {
					throw new IOException("DomainObject is NULL");
				}

				ObjectMapper objectMapper = new ObjectMapper();
				OrderEvent orderEvent = objectMapper.readValue(o.getOrderEvent(), OrderEvent.class);

				if (eventSource.publishEvent(domainObject, orderEvent)) {
					if (logger.isInfoEnabled()) {
						logger.info(String.format("Event for object %s with id %d was published successfully",
								o.getDomainObjectCode(), o.getDomainObjectId()));
					}

					outboxRepository.delete(o);
				} else {
					if (logger.isInfoEnabled()) {
						logger.info(String.format(
								"Event for object %s with id %d was NOT published. Going to reset outbox transaction.",
								o.getDomainObjectCode(), o.getDomainObjectId()));
					}

					o.setProcessing(false);
					outboxRepository.save(o);
				}
			} catch (EntityNotFoundException e) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format(
							"Domain object %s with id %d was NOT FOUND. Going to delete outbox transaction.",
							o.getDomainObjectCode(), o.getDomainObjectId()));
				}

				outboxRepository.delete(o);
			} catch (PersistenceException | IOException e) {
				if (logger.isInfoEnabled()) {
					logger.info(
							String.format("Could not publish event %s.%nError: %s.%nGoing to retry outbox transaction.",
									o.getOrderEvent(), e.getLocalizedMessage()));
				}

				o.setProcessing(false);
			}
		}
	}

	public void setDomainObject(T domainObject) {
		this.domainObject = domainObject;
	}

}
