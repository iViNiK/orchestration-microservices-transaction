package it.vinicioflamini.omt.orchestrator.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;

/* FIRST STEP: Order was received. We must check if the item is in stock and update the inventory*/
@FeignClient(value = "inventory", url = "${inventory.service.endpoint}", fallback = InventoryRestClientFallback.class)
public interface InventoryRestClient {
	
	@PostMapping(value = "/")
    public ResponseEntity<String> doInventory (@RequestBody OrderRequest req);
	
	@PostMapping(value = "/compensate")
    public ResponseEntity<String> compensateInventory (@RequestBody OrderRequest req);

}
