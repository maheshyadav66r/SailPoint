<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE Rule PUBLIC "sailpoint.dtd" "sailpoint.dtd">
<Rule language="beanshell" name="JDBC Generic Provision Eshiam" type="JDBCProvision">
  <Description>This rule is used by the JDBC connector to do provisioning to any JDBC application.</Description>
  <ReferencedRules>
    <Reference class="sailpoint.object.Rule" id="c0a800fc99b9111d8199c47ad9a00bfa" name="JDBC-Generic-Rule-Library"/>
  </ReferencedRules>
  <Signature returnType="ProvisioningResult">
    <Inputs>
      <Argument name="log">
        <Description>
          The log object associated with the SailPointContext.
        </Description>
      </Argument>
      <Argument name="context">
        <Description>
          A sailpoint.api.SailPointContext object that can be used to query the database if necessary.
        </Description>
      </Argument>
      <Argument name="application">
        <Description>
          The application whose data file is being processed.
        </Description>
      </Argument>
      <Argument name="schema">
        <Description>
          The Schema currently in use.
        </Description>
      </Argument>
      <Argument name="connection">
        <Description>
          A connection object to connect to database.
        </Description>
      </Argument>
      <Argument name="plan">
        <Description>
          The ProvisioningPlan created against the logical application.
        </Description>
      </Argument>
    </Inputs>
    <Returns>
      <Argument name="result">
        <Description>
          A Provisioning Result object is desirable to return the status. IT can be a new object or part of the Provisioning Plan
        </Description>
      </Argument>
    </Returns>
  </Signature>
  <Source>
  import java.sql.PreparedStatement;
  import java.sql.ResultSetMetaData;
  import java.text.DateFormat;
  import java.text.SimpleDateFormat;
  import java.util.Calendar;
  import java.util.Date;
  import java.util.Map;
  import java.util.TreeMap;
  import org.apache.commons.logging.Log;
  import org.apache.commons.logging.LogFactory;
  import org.apache.log4j.Level;
  import org.apache.log4j.Logger;
  import sailpoint.api.IdentityService;
  import sailpoint.object.ApprovalSet;
  import sailpoint.object.Attributes;
  import sailpoint.object.Custom;
  import sailpoint.object.Identity;
  import sailpoint.object.Link;
  import sailpoint.object.ProvisioningPlan.AccountRequest;
  import sailpoint.object.ProvisioningPlan.AccountRequest.Operation;
  import sailpoint.object.ProvisioningPlan.AttributeRequest;
  import sailpoint.object.ProvisioningPlan.ObjectRequest;
  import sailpoint.object.ProvisioningPlan.Operation;
  import sailpoint.object.ProvisioningResult;
  import sailpoint.object.WorkItem;
  import sailpoint.persistence.Sequencer;
  import sailpoint.tools.Util;



  {
    Log prlogger = LogFactory.getLog("rule.sp.JDBCProvisioning");
    if (prlogger.isTraceEnabled()) {
      prlogger.trace("JDBC-PR-001: Entering JDBC Generic Provisioning Rule");
      prlogger.trace("JDBC-PR-002: Application: " + appName );
    }
    String applicationError, sql, appName = application.getName();
    List reqs = plan.getAccountRequests(appName), valueList, 
    disableOperations = Util.arrayToList(new AccountRequest.Operation[] {AccountRequest.Operation.Disable, AccountRequest.Operation.Delete});
    boolean hasAcctReq = true, applicationAvailable = true;

    IdentityService identityService = new IdentityService(context);
    Identity identity = plan.getIdentity();

    PreparedStatement prepareStatement;
    Date now = new Date();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Object attributeVal;

    /* JDBC Provisioning Rule Body
     * Handles these cases: 
     * - Account Request Create
     * - Account Request Modify
     *   o Password Reset
     * - Account Request Disable
     * - Account Request Enable
     * - Account Request Unlock
     */
    if (prlogger.isTraceEnabled()) {
      prlogger.trace("JDBC-PR-003: Getting the requests for application..." + appName);
    }

    if (reqs == null || reqs.size() &lt; 1){
      if (prlogger.isTraceEnabled()) {
        prlogger.trace("JDBC-PR-004: No requests for application " + appName);
      }
      hasAcctReq = false;
    }

    if (!hasAcctReq)
    {
      if (prlogger.isTraceEnabled()) {
        prlogger.trace("JDBC-PR-005: Nothing to do. Exit.");
      }
      return;
    }

    if (prlogger.isTraceEnabled()) {
      prlogger.trace("JDBC-PR-006: JDBC Generic Provisioning Rule processing Provisioning Plan...");
      prlogger.trace(String.format("Current Time:%s", now.toString()));
    }

    // The ProvisioningResult is the return object for this type of rule. We'll create it here and then populate it later
    ProvisioningResult result = new ProvisioningResult();

    String dataBaseDriver = application.getAttributeValue( "driverClass" );


    /* Check if the plan is null or not, if not null, process it... */
    if (null!=plan) {
      if (prlogger.isTraceEnabled())
      {
        prlogger.trace(String.format("JDBC-PR-007: The Provisioning Plan being passed in:\n%s\n", plan.toXml()));
      }
      List acctReqs = plan.getAccountRequests();
      /* Get all Account Requests out of the plan */
      if (null!=acctReqs &amp;&amp; acctReqs.size()>0 &amp;&amp; applicationAvailable) {
        /* If the plan contains one or more account requests, we'll iterate through them */
        if (prlogger.isTraceEnabled()) {
          prlogger.trace("JDBC-PR-008: ==>iterating Account Requests - acctReqs.size():" + acctReqs.size());
        }
        for (AccountRequest acctReq : acctReqs) { 
          try {
            /* All of the account operations will reside in a try block in case we have any errors.
               We can mark the provisioningresult as "Failed" if we have an issue.
            */
            List attributeRequests = acctReq.getAttributeRequests();
            List appEntList = application.getEntitlementAttributeNames();
            if (prlogger.isTraceEnabled()) {
              prlogger.trace("JDBC-PR-009: attributeRequests.size():" + (null!=attributeRequests?attributeRequests.size():"No Attribute Requests!"));
              prlogger.trace("JDBC-PR-010: Application Entitlement attributes-------"+appEntList);
              prlogger.trace(String.format("JDBC-PR-011: %s Plan operation %s for %s", appName, acctReq.getOperation(), plan.getNativeIdentity()));
            }

              String tmpTableName,schemaName,keyAttributeName,enableCode,disableCode,statusAttribute,tableName;
              
              //Value of keyAttribute of the table
              String nativeIdentity = acctReq.getNativeIdentity();
              //check the tables need to update or insert
              Boolean isInsertRow=false;
              Boolean isDeleteRow=false;
              Boolean isUpdateRow=false;

              String entitlementAttr=null;
              List attributeList = new ArrayList();
              valueList = new ArrayList();
              PreparedStatement ps=connection.prepareStatement("select * from "+tableName);  
              ps.setFetchSize(1);
              ResultSetMetaData rsmd=ps.getMetaData();
              if (prlogger.isTraceEnabled()) {
                prlogger.trace("JDBC-PR-014: Column Count : "+rsmd.getColumnCount());
                prlogger.trace("JDBC-PR-015: ResultSetMetaData  " + rsmd);
              }
              if(!appEntList.isEmpty())
              {
                for(int i=1;i&lt;=rsmd.getColumnCount();i++)
                {
                  if(appEntList.contains(rsmd.getColumnName(i)))
                  {
                    entitlementAttr=rsmd.getColumnName(i);
                    if (prlogger.isTraceEnabled()) {
                      prlogger.trace("JDBC-PR-016: Entitlement attribute for table--"+tableName+" is----"+entitlementAttr);
                    }
                    break;
                  }
                }
              }
              List entAttributeRequestList=new ArrayList();
              List attrAddListValues=new ArrayList();
              List AttrRemoveListValues=new ArrayList();
              if(entitlementAttr!=null)
              {
                entAttributeRequestList=acctReq.getAttributeRequests(entitlementAttr);// Entitlement Attribute AttributeRequests
              }
              //Separating List of Entitlements according to Operation (Add/Remove)
              if(entAttributeRequestList.size()>0)
              {
                for(AttributeRequest attr: entAttributeRequestList)
                {
                  if(attr.getOperation().toString().equals("Add"))
                  {
                    attrAddListValues.add(attr);
                  }
                  else 
                  {
                    AttrRemoveListValues.add(attr);
                  }
                }
                if (prlogger.isTraceEnabled()) {
                  prlogger.trace("JDBC-PR-017: Entitlement list size with Add Operation--"+attrAddListValues.size());
                  prlogger.trace("JDBC-PR-018: Entitlement list size with Remove Operation--"+AttrRemoveListValues.size());
                }
              }

              List multivaluedAttrList = new ArrayList();
              if(attributeRequests!=null)
              {
                if (prlogger.isTraceEnabled()) {
                  prlogger.trace("JDBC-PR-019: Inside multi valued check");
                }
                multivaluedAttrList=checkMultivaluedAttribute(attributeRequests,rsmd,application,entitlementAttr);
              }

              if (AccountRequest.Operation.Create.equals(acctReq.getOperation())) { /* CREATE Operation */
                List requesters = plan.getRequesters(); 
                /* Process Create SQL operation */
                /* Iterate throught Attribute requests to dynamically construct the list of attributes to set */
                attributeList.add(keyAttributeName); //keyAttribute in Attribute List
                valueList.add("?");
                String createStatement = "insert into %s (%s) values(%s)";
                /* Iterates throught the attributes defined in the Provisioning Plan Account request */
                for(int i=1;i&lt;=rsmd.getColumnCount();i++)
                {                                                                                                                                               
                  for (AttributeRequest attributeRequest : attributeRequests) {
                    String attributeName=attributeRequest.getName();
                    if(!attributeName.equals(keyAttributeName)  &amp;&amp; rsmd.getColumnName(i).equals(attributeName))
                    {
                      attributeList.add(attributeName);
                      valueList.add("?");
                      break;
                    }
                  }
                }
                if(attributeList.size()>1)
                {
                  sql=String.format(createStatement, tableName, Util.listToCsv(attributeList), Util.listToCsv(valueList));
                  prepareStatement = connection.prepareStatement(sql);
                  if(entitlementAttr!=null &amp;&amp; attributeList.contains(entitlementAttr)) 
                    //Condition for LCM/Role based Entitlement Requestsazxc 
                  {
                    if (prlogger.isTraceEnabled()) {
                      prlogger.trace("JDBC-PR-020: Entitlement part of Insert Operation---------");
                      prlogger.trace("JDBC-PR-021: Insert query for entitlement insertion in rows----"+sql);
                    }
                    insertEntitlementsInRows(attrAddListValues,attributeList,nativeIdentity,entitlementAttr,
                                             acctReq,prepareStatement,connection,tableName,keyAttributeName);
                  }
                  else if(!multivaluedAttrList.isEmpty())
                  {
                    if (prlogger.isTraceEnabled()) {
                      prlogger.trace("JDBC-PR-022: Multi valued part of Insert Operation---------");
                      prlogger.trace("JDBC-PR-023: Insert query for multivalued attribute----"+sql);
                    }
                    insertMultivaluesintoRows(multivaluedAttrList,acctReq,tableName,keyAttributeName,nativeIdentity,prepareStatement,attributeList);
                  } 
                  else
                  {
                    if (prlogger.isTraceEnabled()) {
                      prlogger.trace("JDBC-PR-024: Else part of Insert Operation---------");
                    }
                    prepareStatement.setString(1, nativeIdentity);
                    /* Iterate throught attributes list to dynamically assign the values */
                    for (int attributeIndx=1; attributeIndx&lt;attributeList.size(); attributeIndx++) {
                      prepareStatement.setString(attributeIndx+1, getAttributeRequestValue(acctReq, attributeList.get(attributeIndx)));
                    }
                    if (prlogger.isTraceEnabled()) {
                      prlogger.trace("JDBC-PR-025: Query for Normal Insert:" + sql);
                    }
                    prepareStatement.executeUpdate();
                  }
                }
              } 
              else if (AccountRequest.Operation.Modify.equals(acctReq.getOperation())) { /* MODIFY Operation */
                /* Process Modify SQL operation */
                /* Iterate throught Attribute requests to dynamically construct the list of attributes to update */
                boolean settingPassword = "PasswordChange".equals(acctReq.getArgument("operation"));
                String updateSql = "update %s set %s where %s=?";
                String deleteSql="delete from %s where %s=? and %s=?";
                String insertSql="insert into %s (%s) values(%s)";
                if (settingPassword) {
                  if (prlogger.isTraceEnabled()) {
                    prlogger.trace(String.format("%s JDBC-PR-026: Plan operation 'Password Change' for %s", appName, nativeIdentity));
                    prlogger.trace("JDBC-PR-027: "+attributeRequests);
                  }
                }
                //Check flag for Inserting or Deleting Entitlement records in table
                if(entitlementAttr!=null)
                {
                  Boolean[] flag=insertOrDeleteRecordFlag(rsmd,entitlementAttr,attrAddListValues,AttrRemoveListValues,isInsertRow,isDeleteRow);
                  isInsertRow=flag[0];
                  isDeleteRow=flag[1];
                }
                if (prlogger.isTraceEnabled()) {
                  prlogger.trace("JDBC-PR-028: isInsertRow flag----"+isInsertRow);
                  prlogger.trace("JDBC-PR-029: isDeleteRow flag----"+isDeleteRow);
                }
                if(isInsertRow || !multivaluedAttrList.isEmpty() )
                {
                  attributeList.add(keyAttributeName); //keyAttribute in Attribute List
                  valueList.add("?");
                }
                if(isDeleteRow)
                {
                  deleteSql=String.format(deleteSql, tableName,entitlementAttr,keyAttributeName);
                  if (prlogger.isTraceEnabled()) {
                    prlogger.trace("JDBC-PR-030: Delete Sql Query for entitlement removal--" +deleteSql);                                                             
                  }
                  prepareStatement = connection.prepareStatement(deleteSql);
                  //Deleting records from table for Remove entitlement operation
                  deleteRecords(prepareStatement,AttrRemoveListValues,nativeIdentity);
                }
                if(isInsertRow==false &amp;&amp; isDeleteRow==false &amp;&amp; multivaluedAttrList.isEmpty())
                {
                  isUpdateRow=true;
                }
                if (prlogger.isTraceEnabled()) {
                  prlogger.trace("JDBC-PR-031: isUpdateRow flag----"+isUpdateRow);
                }
                //Preparing the update and insert attribute list according to operations.
                for(int i=1;i&lt;=rsmd.getColumnCount();i++)
                { 
                  for (AttributeRequest attributeRequest : attributeRequests) {
                    String attributeName = attributeRequest.getName();
                    if (!attributeName.equals(keyAttributeName)  &amp;&amp; rsmd.getColumnName(i).equals(attributeName)) {
                      if(isInsertRow || !multivaluedAttrList.isEmpty()){
                        attributeList.add(attributeName);
                        valueList.add("?");
                      }
                      else if(isUpdateRow){
                        attributeList.add(attributeName + "=?");
                        valueList.add(getAttributeRequestValue(acctReq, attributeName));
                      }
                      break;
                    }
                  }
                }
                //code adding for mpdify case, either table should update or insert or delete records
                if(attributeList.size()>0 &amp;&amp; isUpdateRow){
                  sql = String.format(updateSql, tableName, Util.listToCsv(attributeList), keyAttributeName);         
                  prepareStatement = connection.prepareStatement(sql);
                  prepareStatement.setString(valueList.size()+1, nativeIdentity);
                  /* Iterate throught attributes list to dynamically assign the values */
                  for (int attributeIndx=0; attributeIndx&lt;attributeList.size(); attributeIndx++) {
                    attributeVal = valueList.get(attributeIndx);
                    prepareStatement.setString(attributeIndx+1, attributeVal);
                  }
                  if (prlogger.isTraceEnabled()) {
                    prlogger.trace("JDBC-PR-032: Update SQL Query----" + sql);
                  }
                  prepareStatement.executeUpdate();
                }
                else if(isInsertRow || !multivaluedAttrList.isEmpty()){
                  if(attributeList.size()>1)
                  {
                    sql = String.format(insertSql, tableName, Util.listToCsv(attributeList), Util.listToCsv(valueList));
                    prepareStatement = connection.prepareStatement(sql);
                    if(attributeList.contains(entitlementAttr)) //Condition for LCM Entitlement Requests
                    {
                      if (prlogger.isTraceEnabled()) {
                        prlogger.trace("JDBC-PR-033: Insert query for entitlement insertion in rows----"+sql);
                      }
                      insertEntitlementsInRows(attrAddListValues,attributeList,nativeIdentity,entitlementAttr,acctReq,
                                               prepareStatement,connection,tableName,keyAttributeName);
                    }
                    else if(!multivaluedAttrList.isEmpty())
                    {
                      if (prlogger.isTraceEnabled()) {
                        prlogger.trace("JDBC-PR-034: Insert query for multivalued attribute----"+sql);
                      }
                      if ( Util.nullSafeCaseInsensitiveEq(acctReq.getAttributeRequest(multivaluedAttrList.get(0)).getOperation().toString(),
                                                          "Remove") ) {
                        deleteMultivaluedAttrEntries(multivaluedAttrList,acctReq,tableName,keyAttributeName,nativeIdentity,prepareStatement,
                                                     attributeList);
                        if (prlogger.isTraceEnabled()) {
                          prlogger.trace("JDBC-PR-035: After delete maulti valued");
                        }
                      } 
                      else {  
                        insertMultivaluesintoRows(multivaluedAttrList,acctReq,tableName,keyAttributeName,nativeIdentity,prepareStatement,
                                                  attributeList);
                        if (prlogger.isTraceEnabled()) {
                          prlogger.trace("JDBC-PR-036: After insert maulti valued");
                        }
                      }
                    }
                  }
                }
                /* Sucessful Update, so mark result as COMMITTED */
              }
              else if (AccountRequest.Operation.Disable.equals(acctReq.getOperation())) { /* DISABLE Operation */
                /* Process Disable SQL operation */
                /******************** Application table SQL Query ********************/
                for(int i=1;i&lt;=rsmd.getColumnCount();i++)
                { 
                  if(rsmd.getColumnName(i).equals(statusAttribute))
                  {
                    isUpdateRow=true;
                    if (prlogger.isTraceEnabled()) {
                      prlogger.trace("JDBC-PR-037: Inside Disable Operation");
                      prlogger.trace("JDBC-PR-038: isUpdateRow is set to true"+ isUpdateRow);
                    }
                    break;
                  }
                }
                if(entAttributeRequestList!=null)
                {
                  if(isUpdateRow==false &amp;&amp; entAttributeRequestList.size()>0)
                  {
                    for(int i=1;i&lt;=rsmd.getColumnCount();i++)
                    {
                      if(entitlementAttr!=null &amp;&amp; rsmd.getColumnName(i).equals(entitlementAttr))
                      {
                        isDeleteRow=true;
                        if (prlogger.isTraceEnabled()) {
                          prlogger.trace("JDBC-PR-039: isDeleteRow is set to true"+ isDeleteRow);
                        }
                        break;
                      }
                    }
                  }
                }
                if(isUpdateRow)
                {
                  sql = String.format("update %s set %s=? where %s=?", tableName, statusAttribute, keyAttributeName);
                  prepareStatement = connection.prepareStatement(sql);
                  if (prlogger.isTraceEnabled()) {
                    prlogger.trace("JDBC-PR-040: Update query to set disable code: " + sql);
                  }
                  prepareStatement.setString(1, disableCode);
                  prepareStatement.setString(2, nativeIdentity); /* ProvisioningPlan nativeIdentity */
                  prepareStatement.executeUpdate();
                }
                else if(isDeleteRow)
                {
                  sql=String.format("delete from %s where %s=?", tableName, keyAttributeName); 
                  prepareStatement = connection.prepareStatement(sql);
                  if (prlogger.isTraceEnabled()) {
                    prlogger.trace("JDBC-PR-041: Delete Sql Query for entitlement removal--" + sql);
                  }
                  prepareStatement.setString(1, nativeIdentity); /* ProvisioningPlan nativeIdentity */
                  prepareStatement.executeUpdate();
                }
                /* Sucessful Disable, so mark result as COMMITTED */
              } 
              else if (AccountRequest.Operation.Delete.equals(acctReq.getOperation())) { /* DELETE Operation */
                /* Process Disable SQL operation */
                /******************** Application table SQL Query ********************/
                if (prlogger.isTraceEnabled()) {
                  prlogger.trace("JDBC-PR-042: Inside Delete Operation method");
                }
                sql = String.format("delete from %s where %s=?", tableName, keyAttributeName);
                prepareStatement = connection.prepareStatement(sql);
                if (prlogger.isTraceEnabled()) {
                  prlogger.trace("JDBC-PR-043: Delete Query for record removal--" + sql);
                }
                prepareStatement.setString(1, nativeIdentity); /* ProvisioningPlan nativeIdentity */
                prepareStatement.executeUpdate();
                /* Sucessful Delete, so mark result as COMMITTED */
              } 
              else if (AccountRequest.Operation.Enable.equals(acctReq.getOperation())) { /* ENABLE Operation */
                /* Process Enable SQL operation */
                /******************** Application table SQL Query ********************/
                for(int i=1;i&lt;=rsmd.getColumnCount();i++)
                { 
                  if(rsmd.getColumnName(i).equals(statusAttribute))
                  {
                    isUpdateRow=true;
                    break;
                  }
                }
                if(isUpdateRow==false)
                {
                  isInsertRow=true;
                }
                if (prlogger.isTraceEnabled()) {
                  prlogger.trace("JDBC-PR-044: isInsertRow flag----"+isInsertRow);
                  prlogger.trace("JDBC-PR-045: isUpdateRow flag----"+isUpdateRow);
                }
                if(isUpdateRow)
                {
                  sql = String.format("update %s set %s=? where %s=?", tableName, statusAttribute, keyAttributeName);
                  prepareStatement = connection.prepareStatement(sql);
                  if (prlogger.isTraceEnabled()) {
                    prlogger.trace("JDBC-PR-046: Update query to set enable code: " + sql);
                  }
                  prepareStatement.setString(1, enableCode);
                  prepareStatement.setString(2, nativeIdentity); /* ProvisioningPlan nativeIdentity */
                  prepareStatement.executeUpdate();
                }
                if(isInsertRow)
                {
                  String insertSql="insert into %s (%s) values(%s)";
                  attributeList.add(keyAttributeName); //keyAttribute in Attribute List
                  valueList.add("?");
                  for(int i=1;i&lt;=rsmd.getColumnCount();i++)
                  {                                                                                                                                               
                    for (AttributeRequest attributeRequest : attributeRequests) {
                      String attributeName=attributeRequest.getName();
                      if(!attributeName.equals(keyAttributeName)  &amp;&amp; rsmd.getColumnName(i).equals(attributeName))
                      {
                        attributeList.add(attributeName);
                        valueList.add("?");
                        break;
                      }
                    }
                  }
                  if(attributeList.size()>1)
                  {
                    sql = String.format(insertSql, tableName, Util.listToCsv(attributeList), Util.listToCsv(valueList));
                    prepareStatement = connection.prepareStatement(sql);
                    if(attributeList.contains(entitlementAttr)) //Condition for LCM Entitlement Requests
                    {
                      if (prlogger.isTraceEnabled()) {
                        prlogger.trace("JDBC-PR-047: Insert query for entitlement insertion in rows----"+sql);
                      }
                      insertEntitlementsInRows(attrAddListValues,attributeList,nativeIdentity,entitlementAttr,acctReq,prepareStatement,
                                               connection,tableName,keyAttributeName);
                    }
                  }
                }
                /* Sucessful Enable, so mark result as COMMITTED */
              }
            /* Sucessful Create,Modify,Disable,Delete,Enable, so mark result as COMMITTED */
            result.setStatus(ProvisioningResult.STATUS_COMMITTED);
          }
          catch (Exception e) {
            prlogger.error("JDBC-PR-048: Error: " + e.toString());
            result=setFailedStatus(result, e.toString());
          }
        } // acctReq request loop
      } // if acctReq requests exist
    } // if plan not null
    if (prlogger.isTraceEnabled()) {
      prlogger.trace("JDBC-PR-049: Exiting JDBC Provisioning Rule.\nResult=\n" + result.toXml(false));
      prlogger.trace("JDBC-PR-050: Exit JDBC Provision Rule");
    }
    return result;
  }
  </Source>
</Rule>
