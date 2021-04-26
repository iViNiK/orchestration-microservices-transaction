/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.common.rest.payload.OrderResponse;
import it.vinicioflamini.omt.order.kafka.source.OrderEventSource;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderEventSource orderEventSource;

	public OrderResponse createOrder(OrderRequest request) {
		Order order = new Order();

		order.setItemId(request.getItemId());

		/*
		 * TODO: order service should call inventory service to get item name by item id
		 */
		order.setItemName("item-xyz");

		order.setCustomerId(request.getCustomerId());

		/*
		 * TODO: order service should call customer service to get customer name by id
		 */
		order.setCustomerName("customer-abc");

		/* TODO: order service should save order and assign a valid order id */
		order.setId(234L);
		// orderRepository.save(order)

		OrderResponse response = new OrderResponse();
		response.setMessage(String.format("Order %d placed successfully", order.getId()));
		response.setOrderId(order.getId());
		response.setItemId(order.getItemId());
		response.setCustomerId(order.getCustomerId());

		/* Publish OrderProcessedEvent */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Order %d placed successfully", order.getId()));
			logger.info(String.format("Going to send an \"OrderPlacedEvent\" for order %d", order.getId()));
		}
		orderEventSource.publishOrderEvent(order, true);

		return response;
	}

	public void compensateOrder(Long orderId) {
		/* TODO: delete record for given order id */
		// orderRepository.delete(orderId)

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Order %d was DELETED", orderId));
		}
	}

}
