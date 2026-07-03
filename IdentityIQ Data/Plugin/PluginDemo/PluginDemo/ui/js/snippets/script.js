var cutomBaseURL = 'http://localhost:8080';
//======== load All managers ==============
// sample response from ajax
//{"body":[{"displayName":"raj kumar","name":"003"},{"displayName":"amit sood","name":"004"},{"displayName":"samy gayle","name":"005"}],"headers":{},"statusCode":"OK","statusCodeValue":200}
$(document).ready(function () {
	var restUrl = cutomBaseURL + PluginHelper.getPluginRestUrl('demo/managers');
    $.ajax({  
        type: "GET",  
       // url: "http://localhost:8080/identityiq/plugin/rest/demo/managers",
        url: restUrl,
        beforeSend: function(xhr){xhr.setRequestHeader('X-XSRF-TOKEN', PluginHelper.getCsrfToken());},
        data:{},
        success: function (data) {  
            var s = '<option value="ALL">All Managers</option>';  
            for (var i = 0; i < data.body.length; i++) {  
                s += '<option value="' + data.body[i].name + '">' + data.body[i].displayName + '</option>';  
            }  
            $("#allManagerList").html(s);  
        }  
    });  
});

//======== JQuery : load All Subordinates ==============
//Sample response from ajax
//["samy gayle-004-amit sood","samy gayle-006-akash vin","samy gayle-014-demo news","samy gayle-015-john wick","samy gayle-016-ram jain","samy gayle-017-james anderson"]
$(document).ready(function () { 
	 $("#getSubOrdinate").click(function()
		 {
			 var restUrl = cutomBaseURL + PluginHelper.getPluginRestUrl('demo/subordinates');
			 var managerName = document.getElementById("allManagerList").value;
			    $.ajax({  
			        type: "GET",  
			        url: restUrl,
			        beforeSend: function(xhr){xhr.setRequestHeader('X-XSRF-TOKEN', PluginHelper.getCsrfToken());},
			        data: 
			        { 
			        	managerName: managerName 
			        },
			        success: function (data) 
			        {  
			        	//==== deleting all the rows including header of existing table === 
			        	$("#subOrdinateTable").find("tr").remove();
			        	
			        	//==== rows and cell creation of the table ===
			            subOrdinateTable(data);
			        }  
			    }); 
	 });
});


//============= create sub-ordinate table ===
function subOrdinateTable(data)
{
	
	var tableValue = data;
	var tableValueLength = tableValue.length;
	var table = document.getElementById("subOrdinateTable");
	
	//==== hard coded header of table====
	var headerRow = table.insertRow(0);
	var headerCell1 = headerRow.insertCell(0);
	var headerCell2 = headerRow.insertCell(1);
	var headerCell3 = headerRow.insertCell(2);
	
	headerCell1.innerHTML = "<b>Manager Name</b>";
	headerCell2.innerHTML = "<b>SubOrdinate Username</b>";
	headerCell3.innerHTML = "<b>SubOrdinate Name</b>";
	
    for (var i = 0; i < tableValueLength; i++) 
    {
    	var row = table.insertRow(i+1);
    	var rowValues = tableValue[i].split("-");
    	var rowValuesLength = rowValues.length;
    	
		//===== create Cells ====
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		//var cell4 = row.insertCell(3);
		
		//==== insert values in cells ===
		cell1.innerHTML = rowValues[0];
		cell2.innerHTML = rowValues[1];
		cell3.innerHTML = rowValues[2];
		//cell4.innerHTML = rowValues[3];
    }
}