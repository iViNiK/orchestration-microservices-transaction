package it.vinicioflamini.omt.orchestrator.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;

/* SECOND STEP: Inventory was updated. Customer has to make payment*/
@FeignClient(value = "payment", url = "${payment.service.endpoint}", fallback = PaymentRestClientFallback.class)
public interface PaymentRestClient {
	
	@PostMapping(value = "/")
    public ResponseEntity<String> doPayment (@RequestBody OrderRequest req);

}
