package it.vinicioflamini.omt.common.domain;

import org.springframework.lang.Nullable;

public enum Action {

	ORDERPLACED("ORDERPLACED"),
	ORDERNOTPLACED("ORDERNOTPLACED"),
	ITEMFETCHED("ITEMFETCHED"),
	ITEMOUTOFSTOCK("ITEMOUTOFSTOCK"),
	ITEMNOTCOMPENSATED("ITEMNOTCOMPENSATED"),
	PAYMENTRECEIVED("PAYMENTRECEIVED"), 
	PAYMENTFAILED("PAYMENTFAILED"),
	SHIPMENTPROCESSED("SHIPMENTPROCESSED"), 
	SHIPMENTFAILED("SHIPMENTFAILED");
	
	private final String name;
	  
	Action(String name) {
	      this.name = name;
	  }
		  
	  public String getName() {
	      return this.name;
	  }
	  
	  @Nullable
	  public static Action fromCode(String code) {
	    for (Action o : Action.values()) {
	      if (o.getName().equals(code)) {
	        return o;
	      }
	    }
	    return null;
	  }
}
