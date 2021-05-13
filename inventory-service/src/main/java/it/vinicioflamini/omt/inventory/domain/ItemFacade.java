package it.vinicioflamini.omt.inventory.domain;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.inventory.repository.InventoryRepository;

@Component
public class ItemFacade {

	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private OutboxProxy outboundProxy;

	@Transactional
	public boolean isItemInStock(Long itemId, Long orderId, Long customerId) throws JsonProcessingException {
		try {
			Item item = inventoryRepository.getOne(itemId);
			boolean inStock = item.getQuantity() >= 1;
			if (!inStock) {
				throw new EntityNotFoundException();
			}
			return true;
		} catch (EntityNotFoundException e) {
			OrderEvent orderEvent = new OrderEvent();
			orderEvent.setOrderId(orderId);
			orderEvent.setItemId(itemId);
			orderEvent.setCustomerId(customerId);
			orderEvent.setAction(Action.ITEMOUTOFSTOCK);
			outboundProxy.requestMessage(itemId, DomainObjects.ITEM, orderEvent);
			
			return false;
		}
	}

	@Transactional
	public Item reserveItem(Long itemId, Long orderId, Long customerId) throws JsonProcessingException {
		Item item = inventoryRepository.getOne(itemId);
		item.setQuantity(item.getQuantity() - 1);
		inventoryRepository.save(item);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setAction(Action.ITEMFETCHED);

		outboundProxy.requestMessage(itemId, DomainObjects.ITEM, orderEvent);
		
		return item;
	}
	
	@Transactional
	public Item releaseItem(Long itemId, Long orderId, Long customerId) throws JsonProcessingException {
		Item item = inventoryRepository.getOne(itemId);
		item.setQuantity(item.getQuantity() + 1);
		inventoryRepository.save(item);

		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(orderId);
		orderEvent.setItemId(itemId);
		orderEvent.setCustomerId(customerId);
		orderEvent.setAction(Action.ITEMOUTOFSTOCK);

		outboundProxy.requestMessage(itemId, DomainObjects.ITEM, orderEvent);
		
		return item;
	}
	
}
