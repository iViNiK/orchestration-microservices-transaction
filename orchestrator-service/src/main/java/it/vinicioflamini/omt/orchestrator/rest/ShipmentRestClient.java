package it.vinicioflamini.omt.orchestrator.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;

/* THIRD STEP: Payment was done. We have to issue shipment for the item*/
@FeignClient(value = "shipment", url = "${shipment.service.endpoint}", fallback = ShipmentRestClientFallback.class)
public interface ShipmentRestClient {
	
	@PostMapping(value = "/")
    public ResponseEntity<String> processShipment (@RequestBody OrderRequest req);
	
}
