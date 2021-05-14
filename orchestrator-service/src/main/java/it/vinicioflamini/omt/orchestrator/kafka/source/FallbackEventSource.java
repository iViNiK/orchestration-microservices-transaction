package it.vinicioflamini.omt.orchestrator.kafka.source;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.repository.OutboxRepository;

@Component
public class FallbackEventSource {

	private static final Logger logger = LoggerFactory.getLogger(FallbackEventSource.class);

	@Autowired
	private OutboxRepository outboxRepository;
	
	@Autowired
	private MessageChannelFactory messageChannelFactory;

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

				ObjectMapper objectMapper = new ObjectMapper();
				OrderEvent orderEvent = objectMapper.readValue(o.getOrderEvent(), OrderEvent.class);

				MessageChannel messageChannel = messageChannelFactory.getMessageChannel(o.getChannelId());

				if (messageChannel != null && messageChannel.send(MessageBuilder.withPayload(orderEvent)
						.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build())) {
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
}
