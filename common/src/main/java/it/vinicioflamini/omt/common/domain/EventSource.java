package it.vinicioflamini.omt.common.domain;

public interface EventSource<T> {

	public boolean publishEvent(T sourceObject);
	
}
