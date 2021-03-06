package it.vinicioflamini.omt.common.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "outbox")
public class Outbox {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "domain_object_id", nullable = false)
	private Long domainObjectId;
	
	@Column(name = "domain_object_code", nullable = false, length = 10)
	private String domainObjectCode;

	@Column(name = "channel_id", nullable = true, length = 100)
	private String channelId;

	@Column(name = "order_event", nullable = true, length = 1000)
	private String orderEvent; 
	
	@Column(name = "date_time", nullable = false)
	private Timestamp dateTime;
	
	@Column(name = "processing", nullable = false)
	private boolean processing;

	public Outbox() {
		super();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDomainObjectId() {
		return domainObjectId;
	}

	public void setDomainObjectId(Long domainObjectId) {
		this.domainObjectId = domainObjectId;
	}

	public String getDomainObjectCode() {
		return domainObjectCode;
	}

	public void setDomainObjectCode(String domainObjectCode) {
		this.domainObjectCode = domainObjectCode;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getOrderEvent() {
		return orderEvent;
	}

	public void setOrderEvent(String orderEvent) {
		this.orderEvent = orderEvent;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public boolean isProcessing() {
		return processing;
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}
	
}
