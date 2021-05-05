/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.common.rest.payload;

public class OrderRequest {

	private Long orderId;
	private Long itemId;
	private Long customerId;
	private Long paymentId;
	private Long shipmentId;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof OrderRequest)) {
			return false;
		}

		OrderRequest c = (OrderRequest) o;

		return (c.getItemId() == null ? Boolean.TRUE : c.getItemId().equals(getItemId()))
				&& (c.getOrderId() == null ? Boolean.TRUE : c.getOrderId().equals(getOrderId()))
				&& (c.getCustomerId() == null ? Boolean.TRUE : c.getCustomerId().equals(getCustomerId()))
				&& (c.getPaymentId() == null ? Boolean.TRUE : c.getPaymentId().equals(getPaymentId()))
				&& (c.getShipmentId() == null ? Boolean.TRUE : c.getShipmentId().equals(getShipmentId()));
	}

	@Override
	public int hashCode() {
		return ((getOrderId() == null) ? 0 : getOrderId().hashCode())
				+ ((getItemId() == null) ? 0 : getItemId().hashCode())
				+ ((getCustomerId() == null) ? 0 : getCustomerId().hashCode())
				+ ((getPaymentId() == null) ? 0 : getPaymentId().hashCode())
				+ ((getShipmentId() == null) ? 0 : getShipmentId().hashCode());
	}

	@Override
	public String toString() {
		return String.format("OrderEvent ** orderId: %d, itemId: %d, customerId: %d, paymentId: %d, shipmentId: %d ",
				getOrderId(), getItemId(), getCustomerId(), getPaymentId(), getShipmentId());
	}
}
