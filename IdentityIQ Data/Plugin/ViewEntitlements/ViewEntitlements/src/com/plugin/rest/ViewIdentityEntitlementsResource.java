package com.plugin.rest;

import com.plugin.dto.DataObjects;
import com.plugin.dto.Entitlements;
import com.plugin.service.ViewIdentityEntitlementsService;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import sailpoint.integration.ListResult;
import sailpoint.plugin.PluginContext;
import sailpoint.rest.plugin.AllowAll;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.tools.GeneralException;

@Path("viewMyEntitlements")
@Produces({ "application/json" })
@AllowAll
public class ViewIdentityEntitlementsResource extends BasePluginResource {

	@Override
	public String getPluginName() {
		return "ViewMyEntitlements";
	}

	// ===================== ROLES =====================

	@GET
	@Path("/getUserRoles")
	@AllowAll
	public ListResult getUserRoles(@QueryParam("roleName") String roleName) throws GeneralException {
		System.out.println("===getUserRoles_Method_Start===");		
		List<DataObjects> list = getViewIdentityEntitlementsPluginService().getUsersRoles(roleName);
		System.out.println("===getUserRoles_Method_End==="+"\n"+list);
		return new ListResult(list, list.size());
	}

	// ===================== ENTITLEMENTS =====================

	@GET
	@Path("/getUserEntitlements")
	@AllowAll
	public ListResult getUserEntitlements(@QueryParam("application") String application, @QueryParam("attribute") String attribute) throws GeneralException {
		System.out.println("===getUserEntitlements_Method_Start===");
		List<Entitlements> list = getViewIdentityEntitlementsPluginService().getUsersEntitlements(application, attribute);
		System.out.println("===getUserEntitlements_Method_End==="+"\n"+list);
		return new ListResult(list, list.size());
	}

	// ===================== SERVICE =====================

	private ViewIdentityEntitlementsService getViewIdentityEntitlementsPluginService() {
		System.out.println("getViewIdentityEntitlementsPluginService method in rest layer");
		return new ViewIdentityEntitlementsService((PluginContext) this, this.getContext());
	}
}