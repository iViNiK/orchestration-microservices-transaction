package it.vinicioflamini.omt.inventory.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

	@Test
	public void testItemNotInStockThenPublishItemOutOfStockEventElsePublishItemFetchedEvent() throws JsonProcessingException {
		Item item = inventoryService.fetchItem(10L, 10L);
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setItemId(10L);
		if (item == null) {
			orderEvent.setAction(Action.ITEMOUTOFSTOCK);
			verify(outboxProxy, times(1)).requestMessage(10L, DomainObjects.ITEM, orderEvent);	
		} else {
			orderEvent.setOrderId(10L);
			orderEvent.setAction(Action.ITEMFETCHED);
			verify(outboxProxy, times(1)).requestMessage(10L, DomainObjects.ITEM, orderEvent);
		}
		
	}

}
