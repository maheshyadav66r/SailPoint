package com.demo.policy;

import java.util.ArrayList;
import java.util.List;

import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;
import sailpoint.object.Policy;
import sailpoint.object.PolicyViolation;
import sailpoint.policy.BasePluginPolicyExecutor;
import sailpoint.tools.GeneralException;

public class ManagerPolicy extends BasePluginPolicyExecutor{

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return "ActiveManagers";
	}

	//========== evaluate the identity for the policy =====
	
	public List<PolicyViolation> evaluate(SailPointContext context, Policy policy, Identity id)
		    throws GeneralException
		  {
		    List<PolicyViolation> violations = new ArrayList<PolicyViolation>();
		    
		    int atLeastManager = policy.getInt("atLeastManager");
		    Identity managerIdentity = id.getManager();
		    if (null != managerIdentity) {
		    	
		      return violations;
		    }
		    
		    
		    
		    if(null == managerIdentity)
		    {
		      int managerFound = 0;
		      violations.add(createViolation(context, policy, id, managerFound));
		    }
		    return violations;
		  }
	
	
	//============ create the PolicyViolation =======
	 private PolicyViolation createViolation(SailPointContext context, Policy policy, Identity identity, int managerFound)
	  {
	    PolicyViolation violation = new PolicyViolation();
	    violation.setStatus(PolicyViolation.Status.Open);
	    violation.setIdentity(identity);
	    violation.setPolicy(policy);
	    violation.setAlertable(true);
	    violation.setOwner(policy.getViolationOwnerForIdentity(context, identity));
	    violation.setConstraintName("No Manager Available for the Identity");
	    
	    violation.setArgument("managerFound", Integer.valueOf(managerFound));
	    
	    return formatViolation(context, identity, policy, null, violation);
	  }
}
