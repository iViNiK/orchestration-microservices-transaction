package it.vinicioflamini.omt.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "items")
public class Item implements Serializable {
	private static final long serialVersionUID = -3459939362488939257L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "item_name", nullable = false, length = 300)
	private String itemName;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	public Item(Long id, String itemName) {
		super();
		this.id = id;
		this.itemName = itemName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


}
