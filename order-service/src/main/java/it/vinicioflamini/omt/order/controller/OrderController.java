/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.common.rest.payload.OrderResponse;
import it.vinicioflamini.omt.order.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@PostMapping()
	public OrderResponse placeOrder(@RequestBody OrderRequest request) {
		return orderService.createOrder(request);
	}
	
	@PostMapping("/compensate")
	public OrderResponse compensateOrder(@RequestBody OrderRequest req) {
		 orderService.compensateOrder(req.getOrderId());
		 OrderResponse response = new OrderResponse();
		 response.setMessage("Order Compensate request has placed");
		 return response;
	}

}
