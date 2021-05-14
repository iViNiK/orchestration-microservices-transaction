package it.vinicioflamini.omt.common.domain;

import org.springframework.lang.Nullable;

public enum DomainObjects {

	ORDER("ORD"),
	ITEM("ITM"),
	PAYMENT("PAY"),
	SHIPMENT("SHP");
	
	private final String shortCode;
	  
	DomainObjects(String code) {
	      this.shortCode = code;
	  }
		  
	  public String getCode() {
	      return this.shortCode;
	  }
	  
	  @Nullable
	  public static DomainObjects fromCode(String code) {
	    for (DomainObjects o : DomainObjects.values()) {
	      if (o.getCode().equals(code)) {
	        return o;
	      }
	    }
	    return null;
	  }
}
