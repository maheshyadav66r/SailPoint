var url = SailPoint.CONTEXT_PATH + '/plugins/pluginPage.jsf?pn=ActiveManagers';  
jQuery(document).ready(function(){  
    jQuery("ul.navbar-right li:first")  
        .before(  
            '<li class="dropdown">' +  
            ' <a href="' + url + '" tabindex="0" role="menuitem" title="Active Manager list">' +  
            ' <i role="presenation" class="fa fa-sticky-note-o fa-lg example"></i>' +  
            ' </a>' +  
            '</li>'  
        );  
});