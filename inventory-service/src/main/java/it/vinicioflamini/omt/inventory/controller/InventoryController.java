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
import it.vinicioflamini.omt.common.rest.payload.OrderResponse;
import it.vinicioflamini.omt.inventory.service.InventoryService;

@Controller
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;

	@PostMapping("/")
	public ResponseEntity<OrderResponse> fetchItem(@RequestBody OrderRequest request) {
		try {
			OrderResponse response = new OrderResponse();
			response.setOrderId(request.getOrderId());
			response.setItemId(request.getItemId());
			response.setCustomerId(request.getCustomerId());
			
			Item item = inventoryService.fetchItem(request.getOrderId(), request.getItemId(), request.getCustomerId());
			
			if (item != null) {
				response.setMessage(String.format("Request placed for item %d fetching", item.getId()));
			} else {
				response.setMessage("Could not place request. Item not found");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	@PostMapping("/compensate")
	public ResponseEntity<OrderResponse> compensateOrder(@RequestBody OrderRequest request) {
		OrderResponse response = new OrderResponse();
		response.setOrderId(request.getOrderId());
		response.setItemId(request.getItemId());
		response.setCustomerId(request.getCustomerId());
		
		try {
			Item item = inventoryService.compensateItem(request.getOrderId(), request.getItemId(), request.getCustomerId());
			response.setMessage(String.format("Request placed for item %d compensation", item.getId()));
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

}
