package com.demo;

import java.util.ArrayList;
import java.util.Collections;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.demo.AllService;

import sailpoint.api.SailPointContext;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.RequiredRight;
import sailpoint.tools.GeneralException;

@Path("demo")
//@Produces({"application/json"})
//@Consumes({"application/json"})
public class AllResources extends BasePluginResource {

	// === unimplemented method, must implement

	public String getPluginName() {
		return "PluginDemo";
	}

	// ===== get List of Managers ======

	@GET
	@Path("managers")
	@Produces({ "application/json" })
	// @AllowAll
	// @Deferred
	@RequiredRight(value = "SystemAdministrator")
	public ResponseEntity<ArrayList<ManagerEntity>> getManagers() throws GeneralException, JSONException {
		SailPointContext context = getContext();
		ArrayList<ManagerEntity> valueString = getAllManagers(context).getManagersList();
		System.out.println("==== " + valueString.toString());
		// return valueString.toString();
		return new ResponseEntity<ArrayList<ManagerEntity>>(valueString, HttpStatus.OK);
	}

	private AllService getAllManagers(SailPointContext context) {
		return new AllService(context);
	}

	// ============= Get SubOrdinates ===============

	@GET
	@Path("subordinates")
	// @AllowAll
	// @Deferred
	@RequiredRight(value = "SystemAdministrator")
	public ArrayList<String> getSubOrdinates(@QueryParam("managerName") String managerName) throws GeneralException {
		SailPointContext context = getContext();
		ArrayList<String> valueString = getSubOrdinates(context).getSubordinates(managerName);
		Collections.sort(valueString);
		return valueString;
	}

	private AllService getSubOrdinates(SailPointContext context) {
		return new AllService(context);
	}

}