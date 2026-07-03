(function() {
    'use strict';
    var widgetFunction = function() {

angular.module('sailpoint.home.desktop.app')
.config(['$httpProvider',function($httpProvider) {
      $httpProvider.defaults.xsrfCookieName = "CSRF-TOKEN";  		  
 }])
 
.controller('trainingPluginWidgetCtrl', ['$scope', '$http', function($scope, $http) {
   // Insert controller logic here
	   // Variable declarations
	   $scope.results = "";
	   $scope.objects = "";   
	   //-----------
	   // populate the list of searchable objects from plugin database
	   $http({
	     method: 'GET',
	     url: PluginHelper.getPluginRestUrl("TrainingPlugin/search/objectNames")
	   }).then(function successCallback(response) {
		   try {
	          $scope.objects = response.data;
	       }  catch(err) {
	          $scope.directions= "Search error!";
	          $scope.objects = [err];
	       }
	     }, function errorCallback(response) {
	             $scope.directions= "Search error: unable to get list of objects!";
	           
	     }); // end http .then
	   
	   
	   // function called when enter key is pressed in input field
	   $scope.searchEnter = function(event) { 
	     if (event.keyCode == 13) { 
	       $scope.getSearch($scope.object,$scope.search); 
	     }
	   } 
	   
	   // function called when button is pressed
	   $scope.getSearch = function(object,search) { 

	 	   $http({
	          method: "GET",
	          // use PluginHelper to get full URL for rest call
	          url: PluginHelper.getPluginRestUrl("TrainingPlugin/search/" + object + "," + search), 
		   }).then(function successCallback(response) {
	           try {
	        	   let json = response.data;
	               $scope.directions= "Results:";
	               if (json === undefined || json.length == 0) {
	             	  $scope.results = ["No matches found"]; 
	               } else {
			          $scope.results = json;
	               }
	           }  catch(err) {
	               $scope.directions= "Search error!";
	               $scope.results = ["You asked to search for " + $scope.search + " in " + $scope.object];
	           } 
		    }, function errorCallback(response) {
		    	 $scope.directions= "Search error!";
			     $scope.results = response.status;
		    }) //end http .then
		    
	    };   // end getSearch

  }]) // End controller

  .directive('spTrainingWidget', function() {
   // Insert directive definition here
	    return {
			restrict: 'E',              
			controller: 'trainingPluginWidgetCtrl',
	        templateUrl:  PluginHelper.getPluginFileUrl('TrainingPlugin', 'ui/html/trainingWidgetTemplate.html')
	   	};
   }); // End directive

}; // End widgetFunction

// Adds widgetFunction to IdentityIQ
PluginHelper.addWidgetFunction(widgetFunction);

})(); 
