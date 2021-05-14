/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.common.rest.payload.OrderResponse;
import it.vinicioflamini.omt.order.domain.OrderFacade;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderFacade orderFacade;

	@Transactional
	public OrderResponse createOrder(OrderRequest request) throws JsonProcessingException {
		Order order = orderFacade.placeOrder(request);

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Order %d was CREATED", order.getId()));
		}

		OrderResponse response = new OrderResponse();
		response.setMessage(String.format("Order %d placed successfully", order.getId()));
		response.setOrderId(order.getId());
		response.setItemId(order.getItemId());
		response.setCustomerId(order.getCustomerId());

		return response;
	}

	public void compensateOrder(Long orderId) {
		orderFacade.rejectOrder(orderId);

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Order %d was REJECTED", orderId));
		}
	}

}
