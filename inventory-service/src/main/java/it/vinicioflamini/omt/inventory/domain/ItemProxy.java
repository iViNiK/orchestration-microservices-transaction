package it.vinicioflamini.omt.inventory.domain;

import org.springframework.stereotype.Component;

@Component
public class ItemProxy {

	public boolean isItemInStock(Long itemId) {
		// TODO: query database to find if item exists and is in stock. If so, update
		// the inventory.
		return Math.random() < 0.5;
	}
}
