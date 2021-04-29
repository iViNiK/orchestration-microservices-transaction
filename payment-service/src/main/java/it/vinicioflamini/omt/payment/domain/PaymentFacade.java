package it.vinicioflamini.omt.payment.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.message.OrderEvent;

@Component
public class PaymentFacade {

	@Autowired
	private OutboxProxy outboundProxy;
	
	public void completePayment(Long orderId, Long itemId, Long paymentId, Long customerId) throws JsonProcessingException {
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setPaymentId(paymentId);
		orderEvent.setAction(Action.PAYMENTRECEIVED);
		
		outboundProxy.requestMessage(paymentId, DomainObjects.PAYMENT, orderEvent);
	}

	public void rejectPayment(Long orderId, Long itemId, Long paymentId, Long customerId) throws JsonProcessingException {
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setPaymentId(paymentId);
		orderEvent.setAction(Action.PAYMENTFAILED);
		
		outboundProxy.requestMessage(paymentId, DomainObjects.PAYMENT, orderEvent);
	}

}
