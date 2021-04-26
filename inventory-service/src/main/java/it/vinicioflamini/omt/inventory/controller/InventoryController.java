/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.inventory.service.InventoryService;

@RestController
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;

	@PostMapping
	public ResponseEntity<String> fetchItem(@RequestBody OrderRequest request) {
		inventoryService.fetchItem(request.getOrderId(), request.getItemId());
		return new ResponseEntity<>("Request placed for item", HttpStatus.OK);
	}

	@PostMapping("/compensate")
	public ResponseEntity<String> compensateOrder(@RequestBody  OrderRequest request) {
		inventoryService.compensateItem(request.getOrderId(), request.getItemId());
		return new ResponseEntity<>("Request placed for item compensate", HttpStatus.OK);
	}

}
