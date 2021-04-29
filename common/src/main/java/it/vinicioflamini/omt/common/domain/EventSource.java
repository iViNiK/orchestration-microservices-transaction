package it.vinicioflamini.omt.common.domain;

import it.vinicioflamini.omt.common.message.OrderEvent;

public interface EventSource<T> {

	public boolean publishEvent(T sourceObject, OrderEvent orderEvent);
	
}
