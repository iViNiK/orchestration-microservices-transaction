package it.vinicioflamini.omt.common.domain;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.repository.OutboxRepository;

public class EventPublisher<T> {

	private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

	private EventSource<T> eventSource;
	private OutboxRepository outboxRepository;
	private JpaRepository<T, Long> domainObjectRepository;

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

	@Scheduled(fixedDelay = 1000)
	@Transactional
	public void publish() {
		Outbox o = outboxRepository.pop();

		if (o != null) {
			o.setProcessing(true);
			outboxRepository.save(o);

			T domainObject = domainObjectRepository.getOne(o.getId());

			if (eventSource.publishEvent(domainObject)) {
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
		}
	}

}
