package it.vinicioflamini.omt.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment implements Serializable {
	private static final long serialVersionUID = -4675409206865751111L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "payment_id", nullable = true)
	private Long paymentId;

	@Column(name = "item_id", nullable = true)
	private Long itemId;

	@Column(name = "order_id", nullable = true)
	private Long orderId;

	@Column(name = "customer_id", nullable = true)
	private Long customerId;
	
	@Column(name = "approved", nullable = true)
	private boolean approved;
	
	public Payment() {
		super();
	}
	
	public Payment(Long paymentId, Long itemId, Long orderId, Long customerId) {
		super();
		this.paymentId = paymentId;
		this.itemId = itemId;
		this.orderId = orderId;
		this.customerId = customerId;
		this.approved = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
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

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

}
