package it.vinicioflamini.omt.order.domain;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.order.repository.OrderRepository;

@Component
public class OrderFacade {
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OutboxProxy outboundProxy;

	@Transactional
	public Order saveAndRequestMessage(OrderRequest request) {
		Order order = new Order();
		order.setItemId(request.getItemId());
		/* TODO: order service should call inventory service to get item name by item id */
		order.setItemName("item-xyz");
		order.setCustomerId(request.getCustomerId());
		/* TODO: order service should call customer service to get customer name by id */
		order.setCustomerName("customer-abc");
		orderRepository.save(order);
		
		outboundProxy.requestMessage(order.getId(), DomainObjects.ORDER);
		
		return order;
	}
	
	@Transactional
	public void remove(Long orderId) {
		orderRepository.deleteById(orderId);
	}
	
	
}
