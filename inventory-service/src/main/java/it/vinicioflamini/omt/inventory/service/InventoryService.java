/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.inventory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vinicioflamini.omt.inventory.domain.ItemProxy;
import it.vinicioflamini.omt.inventory.kafka.source.ItemFetchedEventSource;
import it.vinicioflamini.omt.inventory.kafka.source.ItemOutOfStockEventSource;

@Service
public class InventoryService {

	private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

	@Autowired
	private ItemFetchedEventSource itemFetchedEventSource;

	@Autowired
	private ItemOutOfStockEventSource itemOutOfStockEventSource;
	
	@Autowired
	private ItemProxy itemProxy;

	public void fetchItem(Long orderId, Long itemId) {
		if (itemProxy.isItemInStock(itemId)) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Item was fetched successfully for order %d", orderId));
				logger.info(String.format("Going to send an \"ItemFetchedEvent\" for order %d", orderId));
			}
			
			itemFetchedEventSource.publishItemFetchedEvent(orderId, itemId);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Item is out of stock for order %d", orderId));
				logger.info(String.format("Going to send an \"ItemOutOfStockEvent\" for order %d", orderId));
			}
			
			itemOutOfStockEventSource.publishItemOutOfStockEvent(orderId, itemId);
		}
	}

	public void compensateItem(Long orderId, Long itemId) {
		/* TODO: compensate the Inventory */

		/* publish ItemOutOfStockEvent */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Order %d was not processed", orderId));
			logger.info(String.format("Going to send an \"ItemOutOfStockEvent\" for order %d", orderId));
		}
		itemOutOfStockEventSource.publishItemOutOfStockEvent(orderId, itemId);
	}

}
