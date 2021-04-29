package it.vinicioflamini.omt.inventory.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.common.message.OrderEvent;

@Component
public class ItemFacade {

	@Autowired
	private OutboxProxy outboundProxy;

	public boolean isItemInStock(Long itemId) throws JsonProcessingException {
		// TODO: query database to find if item exists and is in stock. 
		boolean isItemInStock = Math.random() < 0.5;
		
		if (!isItemInStock) {
			OrderEvent orderEvent = new OrderEvent();
			orderEvent.setItemId(itemId);
			orderEvent.setAction(Action.ITEMOUTOFSTOCK);
			outboundProxy.requestMessage(itemId, DomainObjects.ITEM, orderEvent);
		}
		
		return isItemInStock;
	}

	public Item reserveItem(Long itemId, Long orderId) throws JsonProcessingException {
		Item item = new Item(itemId, orderId);
		/* TODO: update the inventory*/

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setAction(Action.ITEMFETCHED);

		outboundProxy.requestMessage(itemId, DomainObjects.ITEM, orderEvent);
		return item;
	}
	
	public Item releaseItem(Long itemId, Long orderId) throws JsonProcessingException {
		/* TODO: compensate the Inventory */
		Item item = new Item(itemId, orderId);

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setAction(Action.ITEMOUTOFSTOCK);

		outboundProxy.requestMessage(itemId, DomainObjects.ITEM, orderEvent);
		return item;
	}
	
}
