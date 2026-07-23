<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE Rule PUBLIC "sailpoint.dtd" "sailpoint.dtd">
<Rule language="beanshell" name="Pre Iterator Header Validation" type="PreIterate">
  <Description>This rule is called before the connector processes the data in a file.</Description>
  <Signature returnType="void">
    <Inputs>
      <Argument name="log" type="org.apache.commons.logging.Log">
        <Description>
          The log object associated with the SailPointContext.
        </Description>
      </Argument>
      <Argument name="context" type="sailpoint.api.SailPointContext">
        <Description>
          A sailpoint.api.SailPointContext object that can be used to query the database if necessary.
        </Description>
      </Argument>
      <Argument name="application">
        <Description>
          Application being iterated.
        </Description>
      </Argument>
      <Argument name="schema">
        <Description>
          Schema representing the data being iterated.
        </Description>
      </Argument>
      <Argument name="stats">
        <Description>
          A map passed by the connector of the stats for the file about to be iterated.
          Contains keys:
            fileName : (String) filename of the file about to be processed
            absolutePath : (String) absolute filename
            length : (Long) length in bytes
            lastModified : (Long) last time the file was updated Java GMT
        </Description>
      </Argument>
    </Inputs>
  </Signature>
  <Source>
  import java.io.BufferedReader;
  import java.io.File;
  import java.io.FileReader;
  import sailpoint.tools.GeneralException;
   import sailpoint.tools.Util;

  
  String expectedHeader = "EmployeeID,FirstName,LastName,Department,Email,Status";
  String filePath = (String) stats.get("absolutePath");

  File file = new File(filePath);
  if (!file.exists()) {
    throw new GeneralException("Input file does not exist : " + filePath);
  }

  BufferedReader reader = null;
  try {

    reader = new BufferedReader(new FileReader(file));
    String actualHeader = reader.readLine();
    if (actualHeader == null) {
      throw new GeneralException("Input file is empty.");
    }

    actualHeader = actualHeader.trim();
    if (!expectedHeader.equalsIgnoreCase(actualHeader)) {
      log.error("Header Validation Failed");
      log.error("Expected : " + expectedHeader);
      log.error("Actual   : " + actualHeader);

      throw new GeneralException("Aggregation stopped due to invalid header.");
    }
  }
  finally {

    if(reader != null){
      reader.close();
    }
  }

</Source>
</Rule>
