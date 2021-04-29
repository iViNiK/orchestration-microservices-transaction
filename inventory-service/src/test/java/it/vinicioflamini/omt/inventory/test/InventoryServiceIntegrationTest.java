package it.vinicioflamini.omt.inventory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.inventory.domain.ItemFacade;
import it.vinicioflamini.omt.inventory.repository.InventoryRepository;
import it.vinicioflamini.omt.inventory.service.InventoryService;

@RunWith(SpringRunner.class)
public class InventoryServiceIntegrationTest {
	@TestConfiguration
	static class InventoryServiceIntegrationTestContextConfiguration {

		@Bean
		public InventoryService inventoryService() {
			return new InventoryService();
		}

		@Bean
		public ItemFacade itemFacade() {
			return new ItemFacade();
		}

	}

	@Autowired
	private InventoryService inventoryService;

	@MockBean
	private OutboxProxy outboxProxy;

	@MockBean
	private InventoryRepository inventoryRepository;

	@Test
	public void testItemNotInInventoryThenPublishItemOutOfStockEvent() throws JsonProcessingException {
		when(inventoryRepository.getOne(10L)).thenThrow(new EntityNotFoundException());
		Item item = inventoryService.fetchItem(10L, 10L);
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setItemId(10L);
		orderEvent.setAction(Action.ITEMOUTOFSTOCK);
		assertNull("ITEM NOT NULL", item);
		verify(outboxProxy, times(1)).requestMessage(10L, DomainObjects.ITEM, orderEvent);
	}

	@Test
	public void testItemNotInStockThenPublishItemOutOfStockEvent() throws JsonProcessingException {
		Item item = new Item(10L, "Item Name");
		item.setQuantity(0);
		when(inventoryRepository.getOne(10L)).thenReturn(item);
		item = inventoryService.fetchItem(10L, 10L);
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setItemId(10L);
		orderEvent.setAction(Action.ITEMOUTOFSTOCK);
		assertNull("ITEM NOT NULL", item);
		verify(outboxProxy, times(1)).requestMessage(10L, DomainObjects.ITEM, orderEvent);
	}

	@Test
	public void testItemInStockThenPublishItemFetchedEvent() throws JsonProcessingException {
		Item item = new Item(10L, "Item Name");
		item.setQuantity(10);
		when(inventoryRepository.getOne(10L)).thenReturn(item);
		item = inventoryService.fetchItem(10L, 10L);
		assertEquals(item.getQuantity(), new Integer("9"));
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setItemId(10L);
		orderEvent.setOrderId(10L);
		orderEvent.setAction(Action.ITEMFETCHED);
		assertNotNull("ITEM NULL", item);
		verify(outboxProxy, times(1)).requestMessage(10L, DomainObjects.ITEM, orderEvent);
	}

	@Test
	public void testCompensateItemThenPublishItemOutOfStockEvent() throws JsonProcessingException {
		Item item = new Item(10L, "Item Name");
		item.setQuantity(10);
		when(inventoryRepository.getOne(10L)).thenReturn(item);
		item = inventoryService.compensateItem(10L, 10L);
		assertEquals(item.getQuantity(), new Integer("11"));
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setItemId(10L);
		orderEvent.setOrderId(10L);
		orderEvent.setAction(Action.ITEMOUTOFSTOCK);
		assertNotNull("ITEM NULL", item);
		verify(outboxProxy, times(1)).requestMessage(10L, DomainObjects.ITEM, orderEvent);
	}

}
