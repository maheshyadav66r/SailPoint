 var url = SailPoint.CONTEXT_PATH + '/plugins/pluginPage.jsf?pn=TrainingPlugin';  

 
 // Ex 5 snippet insertion
    jQuery(document).ready(function(){
    	jQuery("ul.navbar-left li:contains('Intelligence') ul li.divider")
    	   .before(
    			   '<li role="presentation">' +  
                   ' <a class="menuitem" href="' + url + '" role="menuitem">Object Search' +   
                   ' </a>' +  
                   '</li>'  
    			   );   
    });
 // Ex 5 Extension snippet insertion
    jQuery(document).ready(function(){  
        jQuery("ul.navbar-right li:first")  
            .before(  
                '<li class="dropdown">' +  
                ' <a href="' + url + '" tabindex="0" role="menuitem" title="Object Search">' +  
                ' <i role="presentation" class="fa fa-search fa-lg"></i>' +  
                ' </a>' +  
                '</li>'  
            );  
    })