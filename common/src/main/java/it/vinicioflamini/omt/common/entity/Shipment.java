package it.vinicioflamini.omt.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class Shipment implements Serializable {
	private static final long serialVersionUID = -4675409206865751111L;

	@Id
	@GeneratedValue
	private Long id;

	public Long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}

	@Column(name = "shipment_id", nullable = false)
	private Long shipmentId;

	@Column(name = "payment_id", nullable = false)
	private Long paymentId;

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Column(name = "processed", nullable = false)
	private boolean processed;
	
	public Shipment(Long paymentId, Long itemId, Long orderId, Long customerId) {
		super();
		this.paymentId = paymentId;
		this.itemId = itemId;
		this.orderId = orderId;
		this.customerId = customerId;
		this.processed = false;
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

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

}
