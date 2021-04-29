package it.vinicioflamini.omt.order.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Order;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.order.domain.OrderFacade;
import it.vinicioflamini.omt.order.repository.OrderRepository;
import it.vinicioflamini.omt.order.service.OrderService;

@RunWith(SpringRunner.class)
public class OrderServiceIntegrationTest {
	@TestConfiguration
	static class InventoryServiceIntegrationTestContextConfiguration {

		@Bean
		public OrderService orderService() {
			return new OrderService();
		}

		@Bean
		public OrderFacade orderFacade() {
			return new OrderFacade();
		}

	}

	@Autowired
	private OrderService orderService;

	@MockBean
	private OutboxProxy outboxProxy;

	@MockBean
	private OrderRepository orderRepository;

	@Test
	public void testCreateOrderException() throws JsonProcessingException {
		OrderRequest orderRequest = new OrderRequest();
		when(orderRepository.save(new Order())).thenThrow(new EntityNotFoundException());
		orderService.createOrder(orderRequest);
		verify(outboxProxy, times(0)).requestMessage(10L, DomainObjects.ITEM, new OrderEvent());
	}

	@Test
	public void testCreateOrderOkThenPublishOrderPlacedEvent() throws JsonProcessingException {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setOrderId(10L);
		
		Order order = new Order();
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(10L);
		orderEvent.setAction(Action.ORDERPLACED);
		
		when(orderRepository.save(order)).thenReturn(order);
		orderService.createOrder(orderRequest);
		
		verify(outboxProxy, times(1)).requestMessage(null, DomainObjects.ORDER, orderEvent);
	}

	@Test
	public void testCompensateOrderException() throws JsonProcessingException {
		OrderRequest orderRequest = new OrderRequest();
		doThrow(new EntityNotFoundException()).when(orderRepository).deleteById(10L);
		orderService.createOrder(orderRequest);
		verify(outboxProxy, times(0)).requestMessage(10L, DomainObjects.ITEM, new OrderEvent());
	}

	@Test
	public void testCompensateOrderOk() throws JsonProcessingException {
		OrderRequest orderRequest = new OrderRequest();
		doNothing().when(orderRepository).deleteById(10L);
		orderService.createOrder(orderRequest);
		verify(outboxProxy, times(0)).requestMessage(10L, DomainObjects.ITEM, new OrderEvent());
	}

}
