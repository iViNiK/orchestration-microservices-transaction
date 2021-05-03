/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.shipping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.shipping.service.ShippingService;

@RestController
public class ShippingController {

	@Autowired
	private ShippingService shippingService;

	@PostMapping()
	public ResponseEntity<String> processShippment(@RequestBody OrderRequest req) {
		try {
			Long shipmentId = shippingService.processShipment(req.getOrderId(), req.getItemId(), req.getCustomerId());
			return new ResponseEntity<>(String.format("Request placed for shippment %d", shipmentId), HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

}
