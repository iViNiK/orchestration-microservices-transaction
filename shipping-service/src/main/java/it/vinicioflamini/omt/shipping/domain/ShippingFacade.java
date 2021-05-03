package it.vinicioflamini.omt.shipping.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Shipment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.shipping.repository.ShipmentRepository;

@Component
public class ShippingFacade {
	
	@Autowired
	private OutboxProxy outboundProxy;
	
	@Autowired ShipmentRepository shipmentRepository;
	
	public void completeShipment(Long shipmentId, Long orderId, Long itemId, Long customerId) throws JsonProcessingException {
		Shipment shipment = new Shipment(shipmentId, itemId, orderId, customerId);
		shipment.setProcessed(Boolean.TRUE);
		
		shipmentRepository.save(shipment);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setShipmentId(shipment.getId());
		orderEvent.setAction(Action.SHIPMENTPROCESSED);
		
		outboundProxy.requestMessage(shipment.getId(), DomainObjects.SHIPMENT, orderEvent);
	}

	public void rejectShipment(Long shipmentId, Long orderId, Long itemId, Long customerId) throws JsonProcessingException {
		Shipment shipment = new Shipment(shipmentId, itemId, orderId, customerId);
		shipment.setProcessed(Boolean.FALSE);
		
		shipmentRepository.save(shipment);

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setShipmentId(shipment.getId());
		orderEvent.setAction(Action.SHIPMENTFAILED);
		
		outboundProxy.requestMessage(shipment.getId(), DomainObjects.SHIPMENT, orderEvent);
	}

}
