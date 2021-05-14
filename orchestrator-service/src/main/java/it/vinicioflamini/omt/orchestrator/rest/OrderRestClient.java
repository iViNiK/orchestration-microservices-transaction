package it.vinicioflamini.omt.orchestrator.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;

@FeignClient(value = "order", url = "${order.service.endpoint}", fallback = OrderRestClientFallback.class)
public interface OrderRestClient {
	
	@PostMapping(value = "/compensate")
    public ResponseEntity<String> compensateOrder (@RequestBody OrderRequest req);

}
