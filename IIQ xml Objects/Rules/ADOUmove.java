<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE Rule PUBLIC "sailpoint.dtd" "sailpoint.dtd">
<Rule name="Move OU" type="BeforeProvisioning">
  <Description>Before Provisioning Rule which changes disables and enables to a modify.</Description>
  <Source><![CDATA[
import sailpoint.object.Identity;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;

List accountRequests = plan.getAccountRequests();

if (accountRequests != null) {
    for (AccountRequest accountRequest: accountRequests) {
        AccountRequest.Operation op = accountRequest.getOperation();
        if(op == null) continue;
        String nativeIdentity = accountRequest.getNativeIdentity();
        Identity identity = plan.getIdentity();
        String currentLCS = null;
        String activeOU = null;
        String disabledOU = null;
        if(identity != null){
            currentLCS = identity.getAttribute("cloudLifecycleState");
            activeOU = identity.getAttribute("activeParentOu");
            disabledOU = identity.getAttribute("disabledParentOu");
        }
        
        boolean departmentChanged = false;
        if(op.equals(AccountRequest.Operation.Modify)){
            List dAttrReqs = accountRequest.getAttributeRequests("department");
            if(dAttrReqs != null && !dAttrReqs.isEmpty()){
                departmentChanged = true;
            }
        }
        String newOU = null;
        /*
            Get a new OU only if we are in 
                Rehire Use Case (Enable, user LCS is active and user was in the disabled OU)
                Mover Use Case (Modify and user LCS is active)
                Termination Use Case (Disable and user LCS is inactive)
        */
        if ("active".equals(currentLCS) && op.equals(AccountRequest.Operation.Enable)
            && disabledOU != null && activeOU != null && nativeIdentity.endsWith(disabledOU)){
            //Rehire Use Case
            newOU = activeOU;
        }
        else if ("active".equals(currentLCS) && departmentChanged && activeOU != null){
            //Mover Use Case
            newOU = activeOU;
        }
        else if("inactive".equals(currentLCS) && op.equals(AccountRequest.Operation.Disable)){
            //Termination Use Case
            newOU = disabledOU;
        }
        if(newOU != null && !nativeIdentity.endsWith(newOU)){
            //Account not currently in the proper OU. Put them there
            accountRequest.add(new AttributeRequest("AC_NewParent", ProvisioningPlan.Operation.Set, newOU));
        }
    }
}

  ]]></Source>
</Rule>