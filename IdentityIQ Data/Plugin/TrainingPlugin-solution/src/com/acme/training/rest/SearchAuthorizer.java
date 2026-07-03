package com.acme.training.rest;

import sailpoint.authorization.Authorizer;
import sailpoint.authorization.UnauthorizedAccessException;
import sailpoint.tools.GeneralException;
import sailpoint.web.UserContext;

/**
 * Authorizer which checks to see if the currently logged in user
 * has in effect admin rights to the Search Plugin.
 *
 * @author 
 * 
 */

public class SearchAuthorizer implements Authorizer {
	  // Insert code below this line

	   /** 
	    * Constructor. 
	    * 
	    */  
	   public SearchAuthorizer() {   
	   } 

	    /**
	     * {@inheritDoc}
	     */
	    @Override
	    public void authorize(UserContext userContext) throws GeneralException {
	        if (!(userContext.getLoggedInUser().getCapabilityManager().hasCapability("SystemAdministrator") || 
	              userContext.getLoggedInUser().getCapabilityManager().hasCapability("TrainingPluginAdmin"))) {  
	           throw new UnauthorizedAccessException("User does not have required access to the Training Plugin");  
	        } 
		}

	
	
	  // Insert code above this line
}
