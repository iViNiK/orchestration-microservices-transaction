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

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.inventory.domain.ItemFacade;

@Service
public class InventoryService {

	private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

	@Autowired
	private ItemFacade itemFacade;

	public Item fetchItem(Long orderId, Long itemId) throws JsonProcessingException {
		if (itemFacade.isItemInStock(itemId)) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Item %d was fetched successfully for order %d", itemId, orderId));
			}
			return itemFacade.reserveItem(itemId, orderId);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Item %d is out of stock for order %d", itemId, orderId));
			}
			return null;
		}
	}

	public Item compensateItem(Long orderId, Long itemId) throws JsonProcessingException {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Order %d was not processed", orderId));
		}
		
		return itemFacade.releaseItem(itemId, orderId);
	}

}
