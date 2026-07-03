jQuery(document).ready(function(){
	
        var etsUrl = SailPoint.CONTEXT_PATH + '/plugins/pluginPage.jsf?pn=impersonateplugin';
        var leftCol = jQuery("div.panel-body>div:eq(0)");
        if (typeof leftCol == "undefined") {
        	leftCol = jQuery("div.list-group:nth-child(1)");
        }
        var rightCol = jQuery("div.panel-body>div:eq(1)");
        if (typeof rightCol == "undefined") {
        	rightCol = jQuery("div.list-group:nth-child(2)");
        }
        
        var colToAddTo=leftCol;
        if(leftCol.children().length>rightCol.children().length) {
                colToAddTo=rightCol;
        }
        colToAddTo.append('<a href="'+etsUrl+'" class="list-group-item">'
        +'<i class="fa fa-chevron-right"></i>'
        +'<b>Impersonation Settings</b>'
        +'<br>'
        +'<span class="text-muted small">Configure the Impersonate Plugin.</span></a>');

});