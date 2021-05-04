package it.vinicioflamini.omt.order.domain;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OrderStatus;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.order.repository.OrderRepository;

@Component
public class OrderFacade {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OutboxProxy outboundProxy;

	@Transactional
	public Order placeOrder(OrderRequest request) throws JsonProcessingException {
		Order order = new Order();
		order.setItemId(request.getItemId());
		/* TODO: order service should call inventory service to get item name by item id */
		order.setItemName("item-xyz");
		order.setCustomerId(request.getCustomerId());
		/* TODO: order service should call customer service to get customer name by id */
		order.setCustomerName("customer-abc");
		order.setStatus(OrderStatus.PLACED);
		
		orderRepository.save(order);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(order.getId());
		orderEvent.setItemId(order.getItemId());
		orderEvent.setCustomerId(order.getCustomerId());
		orderEvent.setAction(Action.ORDERPLACED);
		outboundProxy.requestMessage(order.getId(), DomainObjects.ORDER, orderEvent);
		
		return order;
	}
	
	@Transactional
	public void rejectOrder(Long orderId) {
		Order order = orderRepository.getOne(orderId);
		order.setStatus(OrderStatus.NOTPLACED);
		
		orderRepository.save(order);
	}
	
	
}
