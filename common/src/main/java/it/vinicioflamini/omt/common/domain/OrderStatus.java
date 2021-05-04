package it.vinicioflamini.omt.common.domain;

import org.springframework.lang.Nullable;

public enum OrderStatus {

	PLACED("PLACED"),
	NOTPLACED("ORDERNOTPLACED");
	
	private final String name;
	  
	OrderStatus(String name) {
	      this.name = name;
	  }
		  
	  public String getName() {
	      return this.name;
	  }
	  
	  @Nullable
	  public static OrderStatus fromCode(String code) {
	    for (OrderStatus o : OrderStatus.values()) {
	      if (o.getName().equals(code)) {
	        return o;
	      }
	    }
	    return null;
	  }
}
