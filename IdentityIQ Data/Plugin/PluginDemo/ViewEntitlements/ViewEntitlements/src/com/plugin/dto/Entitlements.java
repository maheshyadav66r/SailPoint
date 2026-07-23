package com.plugin.dto;

public class Entitlements {

	private String accountName; 
	
	private String attribute; 

	private String entitlement; 

	private String applicationName; 

	private String classification;
	
	private String status;
	
	private String identityRequestId;
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getEntitlement() {
		return entitlement;
	}

	public void setEntitlement(String entitlement) {
		this.entitlement = entitlement;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	public String toString() {
		return "Entitlements [accountName=" + accountName + ", attribute=" + attribute + ", entitlement=" + entitlement + ", applicationName=" + applicationName +", classification=" + classification+ "]";
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdentityRequestId() {
		return identityRequestId;
	}

	public void setIdentityRequestId(String identityRequestId) {
		this.identityRequestId = identityRequestId;
	}

}
