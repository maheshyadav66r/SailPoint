package com.plugin.dto;
import java.util.List;

import sailpoint.object.Link;

public class OrphanIdentities {

	private String name;

	private int linksCount;

	private String linksNames;
	
	@Override
	public String toString() {
		return "OrphanIdentities [name=" + name + ", linksCount=" + linksCount + ", linksNames=" + linksNames + ", links=" + links + "]";
	}

	public List<LinkDTO> getLinks() {
		return links;
	}

	public void setLinks(List<LinkDTO> links) {
		this.links = links;
	}

	private List<LinkDTO> links;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLinksCount() {
		return linksCount;
	}

	public void setLinksCount(int linksCount) {
		this.linksCount = linksCount;
	}

	public String getLinksNames() {
		return linksNames;
	}

	public void setLinksNames(String linksNames) {
		this.linksNames = linksNames;
	}

}
