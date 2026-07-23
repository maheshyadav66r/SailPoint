package com.plugin.dto;

public class Entitlements {

	private String accountName; // either from context or from identity table not sure.
	
	private String attribute; // name column in identityEntitlement table.

	private String entitlement; // had to get the displayname from the managed attribute table.

	private String applicationName; // application column in identityEntitlement table.

	private String classification;
	
	private String status;
	
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

	public void setEntitlement(Object entitlement) {
		this.entitlement = (String) entitlement;
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

}
