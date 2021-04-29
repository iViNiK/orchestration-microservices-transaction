package it.vinicioflamini.omt.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_items")
public class Item implements Serializable {
	private static final long serialVersionUID = -3459939362488939257L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	public Item(Long itemId, Long orderId) {
		super();
		this.itemId = itemId;
		this.orderId = orderId;
	}

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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
