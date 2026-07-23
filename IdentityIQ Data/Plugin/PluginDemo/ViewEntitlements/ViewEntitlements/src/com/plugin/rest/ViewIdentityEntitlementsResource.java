
package com.plugin.rest;

import java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.plugin.dto.Roles;
import com.plugin.dto.Entitlements;
import com.plugin.service.ViewIdentityEntitlementsService;

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

	@GET
	@Path("/getUserRoles")
	@AllowAll
	public Response getUserRoles(@QueryParam("roleName") String roleName) {
		Map<String, Object> response = null;
		try {
			List<Roles> list = getViewIdentityEntitlementsPluginService().getUsersRoles(roleName);

			if (list == null) {
				list = Collections.emptyList();
			}
			response = new HashMap<>();
			response.put("data", list);
			response.put("count", list.size());
		} catch (GeneralException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching roles: " + e.getMessage()).build();
		}
		return Response.ok(response).build();
	}

	@GET
	@Path("/getUserEntitlements")
	@AllowAll
	public Response getUserEntitlements(@QueryParam("application") String application, @QueryParam("entitlementName") String entitlementName) {
		Map<String, Object> response = null;
		try {
			List<Entitlements> list = getViewIdentityEntitlementsPluginService().getUsersEntitlements(application, entitlementName);
			if (list == null) {
				list = Collections.emptyList();
			}
			response = new HashMap<>();
			response.put("data", list);
			response.put("count", list.size());
		} catch (GeneralException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching entitlements: " + e.getMessage()).build();
		}
		return Response.ok(response).build();
	}

	private ViewIdentityEntitlementsService getViewIdentityEntitlementsPluginService() {
		return new ViewIdentityEntitlementsService((PluginContext) this, this.getContext());
	}
}
