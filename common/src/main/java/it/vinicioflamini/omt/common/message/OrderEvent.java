/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.common.message;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;

public class OrderEvent extends OrderRequest {

	private Action action;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof OrderEvent)) {
			return false;
		}

		OrderEvent c = (OrderEvent) o;

		return (c.getItemId() == null ? Boolean.TRUE : c.getItemId().equals(getItemId()))
				&& (c.getOrderId() == null ? Boolean.TRUE : c.getOrderId().equals(getOrderId()))
				&& (c.getCustomerId() == null ? Boolean.TRUE : c.getCustomerId().equals(getCustomerId()))
				&& (c.getPaymentId() == null ? Boolean.TRUE : c.getPaymentId().equals(getPaymentId()))
				&& (c.getShipmentId() == null ? Boolean.TRUE : c.getShipmentId().equals(getShipmentId()))
				&& (c.getAction() == null ? Boolean.TRUE : c.getAction().equals(getAction()));
	}

	@Override
	public int hashCode() {
		return ((getOrderId() == null) ? 0 : getOrderId().hashCode())
				+ ((getItemId() == null) ? 0 : getItemId().hashCode())
				+ ((getCustomerId() == null) ? 0 : getCustomerId().hashCode())
				+ ((getPaymentId() == null) ? 0 : getPaymentId().hashCode())
				+ ((getShipmentId() == null) ? 0 : getShipmentId().hashCode())
				+ ((getAction() == null) ? 0 : getAction().hashCode());
	}

}
