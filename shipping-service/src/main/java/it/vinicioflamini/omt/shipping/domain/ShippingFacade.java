package it.vinicioflamini.omt.shipping.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.message.OrderEvent;

@Component
public class ShippingFacade {
	
	@Autowired
	private OutboxProxy outboundProxy;
	
	public void completeShipment(Long orderId, Long itemId, Long paymentId, Long customerId, Long shipmentId) throws JsonProcessingException {
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setPaymentId(paymentId);
		orderEvent.setShipmentId(shipmentId);
		orderEvent.setAction(Action.SHIPMENTPROCESSED);
		
		outboundProxy.requestMessage(paymentId, DomainObjects.SHIPMENT, orderEvent);
	}

	public void rejectShipment(Long orderId, Long itemId, Long paymentId, Long customerId, Long shipmentId) throws JsonProcessingException {
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setPaymentId(paymentId);
		orderEvent.setShipmentId(shipmentId);
		orderEvent.setAction(Action.SHIPMENTFAILED);
		
		outboundProxy.requestMessage(paymentId, DomainObjects.SHIPMENT, orderEvent);
	}

}
