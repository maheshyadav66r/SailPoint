
 import java.io.BufferedReader;
  import java.io.BufferedWriter;
  import java.io.File;
  import java.io.FileNotFoundException;
  import java.io.FileOutputStream;
  import java.io.FileReader;
  import java.io.FileWriter;
  import java.io.IOException;
  import java.io.OutputStreamWriter;
  import sailpoint.tools.*;
  import sailpoint.object.*;
  import sailpoint.tools.GeneralException;
  import java.sql.*;
  import sailpoint.api.*;
  import sailpoint.object.ProvisioningPlan;
  import sailpoint.object.ProvisioningPlan.AbstractRequest;
  import sailpoint.object.ProvisioningPlan.AttributeRequest;
  import sailpoint.object.ProvisioningPlan.AccountRequest;
  import sailpoint.object.Identity;
  import sailpoint.object.TaskDefinition;
  import java.util.Date;

  log.error("====Provisioning Plan in DelimitedIntegrationConfigRule====="+plan.toXml());

   public void reWriteFile(String filepath,String line){

    //Creating a File object using the filepath
    File file = new File(filepath);

    try{

      //Reading the userFile using Bufferedreader and Adding the values to a final String Object
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

      bufferedWriter.write(line);


      bufferedWriter.close();
    }
    catch(FileNotFoundException fnfe){
      log.error("File not found"+fnfe);

    }

  }

  //Adding groups to the map using operation as a key and groups as list of values
  public Map addingGroupToListBasedOnOperation(Map operations,AccountRequest accReq,AttributeRequest attrReq){

    log.error("=====entered addingGroupToListBasedOnOperation method=====");

    if(operations.get(attrReq.getOperation()) == null){

      //To strore the groups data
      List addEntList = new ArrayList();

      //passing to a method to  build map
      operations = addingGroupToList(operations,addEntList,accReq,attrReq);
    }
    else{

      List entList = operations.get(attrReq.getOperation());

      //passing to a method to  build map
      operations = addingGroupToList(operations,entList,accReq,attrReq);
    }

    log.error("=====leaving addingGroupToListBasedOnOperation method======");

    //return	
    return operations;
  }

  //Method to add GroupData string to List
  public Map addingGroupToList(Map operations,List groups,AccountRequest accReq,AttributeRequest attrReq){

    log.error("=====entered addingGroupToList method======");

    //getting ManagedAttribute Object and getting the GroupName from the object
    Filter f= Filter.eq("value",attrReq.getValue());
    ManagedAttribute ma = context.getUniqueObject(ManagedAttribute.class,f);
    log.error("=====managed attribute object======"+ma);
    String maValue = ma.getAttributes().getString("groupName");

    //building the string
    String value = accReq.getNativeIdentity()+","+attrReq.getValue()+","+maValue;

    //adding to list
    groups.add(value);

    //put in map
    operations.put(attrReq.getOperation(),groups);

    log.error("======leaving addingGroupToList method=====");

    //return
    return operations;
  }

  public String buildFinalStringForGroups(String groupFilePath,Map operations){

    //String to store the final String
    String finalString = "";

    try{

      //Reading the file using Bufferedreader and Adding the values to a final String Object
      BufferedReader userBufferedReader = new BufferedReader(new FileReader(groupFilePath));

      String header = userBufferedReader.readLine();
      finalString = finalString+header+"\n";
      String entLine = "";

      while ((entLine = userBufferedReader.readLine()) != null) {

        if( !entLine.equals("")){

          //Checking groupData to remove
          List removeEntsList = operations.get(ProvisioningPlan.Operation.Remove);
          if(Util.nullSafeSize(removeEntsList) > 0){	
            if(removeEntsList.contains(entLine)){
              log.error("skip this line coz this is group to remove");
            }
            else{
              finalString = finalString+entLine+"\n";
            }
          }
          else{
            finalString = finalString+entLine+"\n";
          }
        }
      }
      userBufferedReader.close();
    }
    catch(FileNotFoundException fnfe){
      log.error("File not found"+fnfe);
    }

    //Adding Add Operation groups to the final String
    List addEntsList = operations.get(ProvisioningPlan.Operation.Add);
    if(Util.nullSafeSize(addEntsList) > 0){
      for(String input:addEntsList){
        finalString = finalString+input+"\n";
      }
    }

    //return
    return finalString;	  
  }

  public String  buildFinalStringForModifyUser(String userFilePath,List finalDataList){

    //String to store the final String
    String finalString = "";

    try{

      //Reading the file using Bufferedreader and Adding the values to a final String Object
      BufferedReader userBufferedReader = new BufferedReader(new FileReader(userFilePath));

      String header = userBufferedReader.readLine();
      finalString = finalString+header+"\n";
      String empLine = "";

      while ((empLine = userBufferedReader.readLine()) != null) {

        if( !empLine.equals("")){

          String[] empArray = empLine.split(",");
          List oriData = Util.arrayToList(empArray);

          for(List data:finalDataList){

            if(data.get(0).equals(oriData.get(0))){ 

              int num = data.get(1);
              log.error(num);
              oriData.remove(num);
              log.error("===oriData after remove==="+oriData);
              oriData.add(data.get(1),data.get(2));
              log.error("===oriData after add==="+oriData);
              empLine = Util.listToCsv(oriData);
              empLine = empLine.replace(", ",",");
              log.error("======final line====="+empLine);
            }
          }
          finalString = finalString+empLine+"\n";
        }
      }
      userBufferedReader.close();
    }
    catch(FileNotFoundException fnfe){
      log.error("File not found"+fnfe);
    }

    return finalString;

  }

  private void writeLine( String filepath,  String line) throws Exception {
    File file = new File(filepath);
    FileWriter fw = new FileWriter(file, true);    
    BufferedWriter bw = new BufferedWriter(fw);
    bw.newLine();
    bw.write(line);
    bw.close();
    fw.close();
    System.out.println("Line has been written successfully!!");
  }





  log.error("--- before plan.getAccountRequests  ---");

  //listing all the account requests from the plan
  List accReqList = plan.getAccountRequests();

  String finalUserLine;
  String finalGroupData;
  List userDataList=new ArrayList();
  List groupDataList=new ArrayList();

  //Iterating the accountRequests
  for(AccountRequest accReq:accReqList){
    log.error("---AccountRequest loop ---");
    //Getting the operation of particular accountRequest and checking conditions based on the operation
    ProvisioningPlan.AccountRequest.Operation accReqOperation = accReq.getOperation();
    log.error("--- before accReqOperation  ---");
    //Getting applicationName and loading the Application Object
    String applicationName = accReq.getApplicationName();
    Application applicationObject = context.getObjectByName(Application.class,applicationName);

   // log.error("--- after applicationObject xml  ---"+applicationObject.toXml());
    //getting filepaths from application Object
    String userFilePath = applicationObject.getAttributes().getString("file");
    String groupFilePath = applicationObject.getAttributes().getString("Group.file");

    log.error("---AccountRequest loop ---"+userFilePath+"   "+groupFilePath);
    //Getting attribute names from account schema
    if(applicationObject!=null)
      log.error("--- fetching accountSchema and groupSchema from applicationObject--");
    Schema accountSchema = applicationObject.getSchema("account");
    Schema groupSchema=applicationObject.getSchema("Group");

     log.error("---accountSchema and groupSchema printing--"+accountSchema+"   "+groupSchema);
    
    if(accountSchema!=null @and groupSchema!=null)
      log.error("---accountSchema and groupSchema null check--");
    List accountAttributes = accountSchema.getAttributeNames();
    List groupAttributes = groupSchema.getAttributeNames();

    if(accountAttributes!=null @and groupAttributes!=null)
    log.error("====AccountAttribute Names====="+accountAttributes);
     log.error("====groupAttributes Names====="+groupAttributes);

    //To store Set operation of attributeRequest
    List finalDataList = new ArrayList();

    if(accReqOperation!=null @and accReqOperation.equals(ProvisioningPlan.AccountRequest.Operation.Create)){

      log.error("---accReqOperation create ---");
      //adding EnployeeId column value from the accountRequest nativeidentity(IdentityAttribute) since it's not available in attributeRequest
      userDataList.add(accReq.getNativeIdentity());
      List attReqsList= accReq.getAttributeRequests();

      if(attReqsList!=null){

        for(AttributeRequest attReq:attReqsList){

          log.error("---AttributeRequest loop ---");
          ProvisioningPlan.Operation attReqOp=attReq.getOp();
          boolean isEntitlement=accountSchema.getAttributeDefinition(attReq.getName()).isEntitlement();
          boolean isManaged=accountSchema.getAttributeDefinition(attReq.getName()).isManaged();


          // group(entitlements) attributes will have entitlement="true" managed="true" multi="true" in its attributeDefinition(of schema)
          if(attReqOp!=null @and isEntitlement  @and isManaged){

            log.error("---group AttributeRequest block ---");
            //Adding group file column values to groupDataList based on their indexes fetching from groupAttributes list
            groupDataList.add(accReq.getNativeIdentity());            
            groupDataList.add(groupAttributes.indexOf(attReq.getName()),attReq.getValue());
            groupDataList.add(groupAttributes.indexOf("groupName"),attReq.getDisplayValue());          

            finalGroupData=Util.listToCsv(groupDataList);

            //Calling writeLine() for every group AttributeRequest, Since for every  group AttributeRequest one record in the group file will be updated
            writeLine(groupFilePath, finalGroupData);

            //clearing the data from list in which previous group AttributeRequest data exist for next iteration
            groupDataList.clear();    

          }else {

            log.error("---account AttributeRequest block ---");
            int userColumnIndex=accountAttributes.indexOf(attReq.getName());
            userDataList.add(userColumnIndex,attReq.getValue());
          }
        }
        finalUserLine=Util.listToCsv(userDataList);
      }

    }
    if(finalUserLine!=null @and userFilePath!=null){
      //writing the data into files by calling writeLine() for every AccountRequest create Operation
      writeLine(userFilePath, finalUserLine);

    }


    if(accReqOperation.equals(ProvisioningPlan.AccountRequest.Operation.Modify)){

      log.error("=====entered into modify operation=====");

      //Getting List of attributeRequests form AccountRequest Object
      List attrReqs = accReq.getAttributeRequests();

      log.error("====List of attributeRequests===="+attrReqs);

      //To store the Data against the operation
      Map operations = new HashMap();

      //Iterating the AttributeRequests List
      for(AttributeRequest attrReq:attrReqs){

        log.error("====entered attributeRequests loop=====");

        //Add operation of attributeRequest
        if(attrReq.getOperation().equals(ProvisioningPlan.Operation.Add)){

          log.error("====entered attribute request add operation=====");

          operations = addingGroupToListBasedOnOperation(operations,accReq,attrReq);

          log.error("====operations Map===="+operations);
        }

        //Remove operation of attributeRequest
        else if(attrReq.getOperation().equals(ProvisioningPlan.Operation.Remove)) {

          log.error("====entered attribute request remove operation=====");

          operations = addingGroupToListBasedOnOperation(operations,accReq,attrReq);

          log.error("====operations Map===="+operations);
        }

        //Set operation of attributeRequest
        else if(attrReq.getOperation().equals(ProvisioningPlan.Operation.Set)){

          //Creatinng a list to store the data of attribute request
          List data = new ArrayList();
          data.add(accReq.getNativeIdentity());

          //passing attreq.getName() and getting index of particular attribute from accountattributes list from schema
          int index = accountAttributes.indexOf(attrReq.getName());
          data.add(index);
          data.add(attrReq.getValue());

          //Adding the final data to the final Data List
          finalDataList.add(data);
          log.error("=====finalDataList====="+finalDataList);
        }
        else{
          log.error("for future updations");
        }
      }

      //Condition to check wheather any operation of add or remove is present in attribute request to perform write operation
      if(!(operations.get(ProvisioningPlan.Operation.Add) == null @and operations.get(ProvisioningPlan.Operation.Remove) == null)){
        String finalGroupString = buildFinalStringForGroups(groupFilePath,operations);
        reWriteFile(groupFilePath,finalGroupString);
      }      

      //condition to check wheather the set operation is present in attribute request to perform write operation
      if(Util.nullSafeSize(finalDataList) > 0){

        //passing to buildFinalStringForModifyUser and getting final string as output
        String finalUserString = buildFinalStringForModifyUser(userFilePath,finalDataList);

        log.error("=====Final user String===="+finalUserString);

        //reWriting the file
        reWriteFile(userFilePath,finalUserString);
      }

      //running account aggregation task to refresh all accounts and their entitlements (here 'HR2' is the name of the account aggregation task for the application)
      TaskDefinition task = context.getObjectByName(TaskDefinition.class,"IntegrationConfiigAccountAggregation");
      TaskManager tm = new TaskManager(context);
      tm.run(task,null);

      ProvisioningResult pr = new ProvisioningResult();
      pr.setStatus("committed");

      return pr;

    }
    else if("Delete".equals(accReqOperation)){

    }
    else{
      log.error("for future updations");
    }
  }

  TaskDefinition task = context.getObjectByName(TaskDefinition.class,"IntegrationConfiigAccountAggregation");
  TaskManager tm = new TaskManager(context);
  tm.run(task,null);

  ProvisioningResult pr = new ProvisioningResult();
  pr.setStatus("committed");

  return pr;


















  log.error("plan :"+plan.toXml());

  Application application = context.getObjectByName(Application.class,"TASK-AccountDetails");
  Map appConfig = application.getAttributes();
  String file1 = (String)appConfig.get("file");
  String file2 = (String)appConfig.get("group.file");
  String nativeIdentity;
  String header;
  String line;
  String fileData;
  String[] record;
  String statusOfoverRideExistingFileMethod;
  int indexNumber;
  BufferedReader reader;
  ProvisioningResult pr;

  public String operationDelete(String fileName,String type,String indexValue,String nativeIdentity) throws Exception {
   
    StringBuilder sb = new StringBuilder();
    if(!Util.isAnyNullOrEmpty(fileName,type,indexValue,nativeIdentity)){
      indexNumber = Util.atoi(indexValue);
      reader = new BufferedReader(new FileReader(fileName));
      header = reader.readLine();//header will skip--line 1
      while( (line = reader.readLine()) != null ){
        if(type.equalsIgnoreCase("group")){
          record = line.split(appConfig.get("group.delimiter"));
        }else if(type.equalsIgnoreCase("account")){
          record = line.split(appConfig.get("delimiter"));
        }else{
          log.error("paramter error : type should equals to the group or account ");
        }

        if(!nativeIdentity.equalsIgnoreCase(record[indexNumber])){
          sb.append(line+"\n");
        }else{
          log.error("delete the record");
          //Skip the record
        }
        //write to file
      }
       reader.close();
      log.error("logic code executed successfully");
      fileData = sb.toString();
      log.error("fileData :"+fileData);
      statusOfoverRideExistingFileMethod = overRideExistingFile(fileName,fileData,header);
    }else{
      log.error("fileName,type,indexValue,nativeIdentity -- any of the string is empty or null ");
    }
    return statusOfoverRideExistingFileMethod;
  }

  public String overRideExistingFile(String fileName,String data,String header)throws Exception {
    if(!Util.isAnyNullOrEmpty(fileName,data,header)){
      //Util.writeFile(fileName, header+"\n"+data);
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName,false));
      bufferedWriter.write(header+"\n"+data);
      bufferedWriter.close();
      return "success";
    }
    else{
      log.error("no data either filename or header or data ");
      return "failed";
    }

  }

  //logic start from there
  List listOfAccountRequest = plan.getAccountRequests("TASK-AccountDetails");

  for(AccountRequest accountRequest : listOfAccountRequest){

    if(accountRequest.getOperation().equals(ProvisioningPlan.AccountRequest.Operation.Delete)){

      nativeIdentity = accountRequest.getNativeIdentity();
      String groupStatus = operationDelete(file2,"group","0",nativeIdentity);
      String accountStatus = operationDelete(file1,"account","0",nativeIdentity);
      if(!Util.isAnyNullOrEmpty(groupStatus,accountStatus)){
        ProvisioningResult provisioningResult = new ProvisioningResult();
        provisioningResult.setStatus("committed");
      }

    }  
  }
  log.error("finnally complteted delete operation");

  return provisioningResult;