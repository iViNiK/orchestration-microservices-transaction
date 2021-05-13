/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.inventory.service.InventoryService;

@Controller
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;

	@PostMapping("/")
	public ResponseEntity<String> fetchItem(@RequestBody OrderRequest request) {
		try {
			Item item = inventoryService.fetchItem(request.getOrderId(), request.getItemId());
			String message;
			if (item != null) {
				message = String.format("Request placed for item %d fetching", item.getId());
			} else {
				message = "Could not place request. Item not found";
			}
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	@PostMapping("/compensate")
	public ResponseEntity<String> compensateOrder(@RequestBody OrderRequest request) {
		try {
			Item item = inventoryService.compensateItem(request.getOrderId(), request.getItemId());
			return new ResponseEntity<>(String.format("Request placed for item %d compensation", item.getId()),
					HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

}
