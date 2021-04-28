/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order implements Serializable {
	private static final long serialVersionUID = 2865564420716791390L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "item_id", nullable = false)
	private Long itemId;
	
	@Column(name = "item_name", nullable = false, length = 300)
	private String itemName;
	
	@Column(name = "customer_id", nullable = false)
	private Long customerId;
	
	@Column(name = "customer_name", nullable = false, length = 300)
	private String customerName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

}
