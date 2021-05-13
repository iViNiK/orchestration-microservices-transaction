/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.common.rest.payload.OrderResponse;
import it.vinicioflamini.omt.order.service.OrderService;

@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/")
	public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
		try {
			OrderResponse response = orderService.createOrder(request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	@PostMapping("/compensate")
	public ResponseEntity<OrderResponse> compensateOrder(@RequestBody OrderRequest request) {
		orderService.compensateOrder(request.getOrderId());

		OrderResponse response = new OrderResponse();
		response.setMessage(String.format("Order %d Compensate request has placed", request.getOrderId()));
		response.setOrderId(request.getOrderId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
