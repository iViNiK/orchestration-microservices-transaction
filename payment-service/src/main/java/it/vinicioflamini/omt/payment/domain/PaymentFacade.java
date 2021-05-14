package it.vinicioflamini.omt.payment.domain;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Payment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.payment.repository.PaymentRepository;

@Component
public class PaymentFacade {

	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private OutboxProxy outboundProxy;
	
	@Transactional
	public void completePayment(Long orderId, Long itemId, Long paymentId, Long customerId) throws JsonProcessingException {
		Payment payment = new Payment(paymentId, itemId, orderId, customerId);
		payment.setApproved(Boolean.TRUE);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setPaymentId(payment.getId());
		orderEvent.setAction(Action.PAYMENTRECEIVED);
		
		paymentRepository.save(payment);
		outboundProxy.requestMessage(payment.getId(), DomainObjects.PAYMENT, orderEvent);
	}

	@Transactional
	public void rejectPayment(Long orderId, Long itemId, Long customerId) throws JsonProcessingException {
		Payment payment = new Payment(null, itemId, orderId, customerId);
		payment.setApproved(Boolean.FALSE);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(payment.getOrderId());
		orderEvent.setItemId(payment.getItemId());
		orderEvent.setCustomerId(payment.getCustomerId());
		orderEvent.setPaymentId(payment.getId());
		orderEvent.setAction(Action.PAYMENTFAILED);
		
		paymentRepository.save(payment);
		outboundProxy.requestMessage(payment.getId(), DomainObjects.PAYMENT, orderEvent);
	}

}
