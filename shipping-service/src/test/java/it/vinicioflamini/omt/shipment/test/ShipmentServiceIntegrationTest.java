package it.vinicioflamini.omt.shipment.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Shipment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.shipping.domain.ShippingFacade;
import it.vinicioflamini.omt.shipping.repository.ShipmentRepository;
import it.vinicioflamini.omt.shipping.service.ShippingService;

@RunWith(SpringRunner.class)
public class ShipmentServiceIntegrationTest {
	@TestConfiguration
	static class InventoryServiceIntegrationTestContextConfiguration {

		@Bean
		public ShippingService shipmentService() {
			return new ShippingService();
		}

		@Bean
		public ShippingFacade shipmentFacade() {
			return new ShippingFacade();
		}

	}

	@Autowired
	private ShippingService shippingService;

	@MockBean
	private OutboxProxy outboxProxy;

	@MockBean
	private ShipmentRepository shipmentRepository;

	@Test
	public void testCreateShipmentException() throws JsonProcessingException {
		Shipment shipment = new Shipment(10L, 10L, 10L, 10L);
		
		when(shipmentRepository.save(shipment)).thenThrow(new EntityNotFoundException());
		
		Long shipmentId = shippingService.processShipment(10L, 10L, 10L);
		
		if (shipmentId != null) {
			verify(outboxProxy, times(0)).requestMessage(null, DomainObjects.SHIPMENT, new OrderEvent());
		} else {
			verify(outboxProxy, times(0)).requestMessage(null, DomainObjects.SHIPMENT, new OrderEvent());
		}
	}

	@Test
	public void testCreateShipmentOkThenPublishShipmentReceivedEventElsePublishShipmentFailedEvent() throws JsonProcessingException {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setOrderId(10L);
		
		Shipment shipment = new Shipment(10L, 10L, 10L, 10L);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(shipment.getOrderId());
		orderEvent.setItemId(shipment.getItemId());
		orderEvent.setCustomerId(shipment.getCustomerId());
		
		Long shipmentId = shippingService.processShipment(10L, 10L, 10L);
		
		if (shipmentId != null) {
			shipment.setShipmentId(shipmentId);
			shipment.setProcessed(Boolean.TRUE);
			orderEvent.setAction(Action.SHIPMENTPROCESSED);
		} else {
			shipment.setShipmentId(null);
			shipment.setProcessed(Boolean.FALSE);
			orderEvent.setAction(Action.SHIPMENTFAILED);
		}

		when(shipmentRepository.save(shipment)).thenReturn(shipment);
		orderEvent.setShipmentId(shipment.getId());
		verify(outboxProxy, times(1)).requestMessage(null, DomainObjects.SHIPMENT, orderEvent);	

	}

}
