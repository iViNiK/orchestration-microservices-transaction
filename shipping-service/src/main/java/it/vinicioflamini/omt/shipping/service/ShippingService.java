/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.shipping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.shipping.domain.ShippingFacade;

@Service
public class ShippingService {

	private static final Logger logger = LoggerFactory.getLogger(ShippingService.class);

	private Long shipmentId = null;

	@Autowired
	private ShippingFacade shippingFacade;

	public Long processShipment(Long orderId, Long itemId, Long customerId)
			throws JsonProcessingException {
		if (callShipmentService(orderId, itemId, customerId)) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Shipping completed successfully for order %d", orderId));
				logger.info(String.format("Going to send a \"ShipmentProcessedEvent\" for order %d", orderId));
			}
			shippingFacade.completeShipment(shipmentId, orderId, itemId, customerId);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Shipping failed for order id: %d", orderId));
				logger.info(String.format("Going to send a \"ShipmentFailedEvent\" for order %d", orderId));
			}
			shippingFacade.rejectShipment(shipmentId, orderId, itemId, customerId);
		}

		return shipmentId;
	}

	/**/

	private boolean callShipmentService(Long orderId, Long itemId, Long customerId) {
		/* TODO: place the shipment and set the ID */
		if (Math.random() < 0.5) {
			shipmentId = 1234L;
			return true;
		}

		return false;
	}

}
