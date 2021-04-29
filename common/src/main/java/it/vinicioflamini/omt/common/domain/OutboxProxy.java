package it.vinicioflamini.omt.common.domain;

import java.sql.Timestamp;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.repository.OutboxRepository;

@Component
public class OutboxProxy {
	
	@Autowired
	OutboxRepository outboxRepository;
	
	@Transactional
	public Outbox requestMessage(Long objectId, DomainObjects object, OrderEvent orderEvent) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		Outbox outbox = new Outbox();
		outbox.setDomainObjectId(objectId);
		outbox.setDomainObjectCode(object.getCode());
		outbox.setOrderEvent(objectMapper.writeValueAsString(orderEvent));
		outbox.setDateTime(new Timestamp(System.currentTimeMillis()));
		outbox.setProcessing(false);
		
		return outboxRepository.save(outbox);
	}

}
