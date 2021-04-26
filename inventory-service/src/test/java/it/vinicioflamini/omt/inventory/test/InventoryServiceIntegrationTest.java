package it.vinicioflamini.omt.inventory.test;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import it.vinicioflamini.omt.inventory.domain.ItemProxy;
import it.vinicioflamini.omt.inventory.kafka.source.ItemFetchedEventSource;
import it.vinicioflamini.omt.inventory.kafka.source.ItemOutOfStockEventSource;
import it.vinicioflamini.omt.inventory.service.InventoryService;

@RunWith(SpringRunner.class)
public class InventoryServiceIntegrationTest {
	@TestConfiguration
	static class InventoryServiceIntegrationTestContextConfiguration {

		@Bean
		public InventoryService inventoryService() {
			return new InventoryService();
		}
	}

	@Autowired
	private InventoryService inventoryService;

	@MockBean
	private ItemFetchedEventSource itemFetchedEventSource;

	@MockBean
	private ItemOutOfStockEventSource itemOutOfStockEventSource;
	
	@MockBean
	private  ItemProxy itemProxy;

	@Before
	public void setUp() {
		lenient().doNothing().when(itemFetchedEventSource).publishItemFetchedEvent(10L, 10L);
		lenient().doNothing().when(itemOutOfStockEventSource).publishItemOutOfStockEvent(10L, 10L);
	}

	@Test
	public void testItemNotInStockThenPublishItemOutOfStockEvent() {
		when(itemProxy.isItemInStock(10L)).thenReturn(false);
		inventoryService.fetchItem(10L, 10L);
		verify(itemOutOfStockEventSource, times(1)).publishItemOutOfStockEvent(10L, 10L);
	}

	@Test
	public void testItemInStockThenPublishItemFetchedEvent() {
		when(itemProxy.isItemInStock(10L)).thenReturn(true);
		inventoryService.fetchItem(10L, 10L);
		verify(itemFetchedEventSource, times(1)).publishItemFetchedEvent(10L, 10L);
	}

}
