package it.vinicioflamini.omt.common.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;

@Entity
public class Outbox {

	private Long id;
	private Long domainObjectId;
	private String domainObjectCode;
	private Timestamp dateTime;
	private boolean processing;

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
