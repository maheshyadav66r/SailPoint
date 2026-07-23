var urlContext = SailPoint.CONTEXT_PATH;
var identityName = PluginHelper.getCurrentUsername();
var csrfToken = Ext.util.Cookies.get('XSRF-TOKEN');


function debounce(func, wait) {
    var timeout;
    return function () {
        var args = Array.prototype.slice.call(arguments);
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            func.apply(null, args);
        }, wait);
    };
}



var viewMyEntitlements = angular.module('viewMyEntitlements', ['ui.bootstrap', 'sailpoint.i18n']);

viewMyEntitlements.config(function ($httpProvider) {
    $httpProvider.defaults.xsrfCookieName = "CSRF-TOKEN";
});





  // ================= ROLES CONTROLLER =================
 viewMyEntitlements.controller('RolesTableCtrl', ['$scope', '$http', function ($scope, $http) {
    $scope.roles = [];
    $scope.loading = true;
    $scope.error = null;
    $scope.roleSearch = {};

    var rolesEndpoint = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserRoles');

    $scope.fetchRoles = function (searchKeyword) {
        $scope.loading = true;
        $scope.error = null;

      //  var xsrfToken = getCsrfToken();
     
        $http.get(rolesEndpoint, {
            withCredentials: true,
           
            params: {
                roleName: searchKeyword || ''
                
            }
        })
        .then(function (response) {
            if (typeof response.data === 'string' && response.data.indexOf('<!DOCTYPE html>') > -1) {
                $scope.error = 'Backend Error: Check identityiq.log for System Exception.';
                $scope.loading = false;
                return;
            }
            var dataToProcess = response.data && (response.data.objects || response.data);
			if (dataToProcess && Array.isArray(dataToProcess.data)) {
			    $scope.roles = dataToProcess.data.map(function (item) {
			        return {
			            roleName:        item.roleName || item.name || '',
			            description:     item.description || '',
			            classification:  item.classification || '',
			            assignedBy:      item.assignedBy || '',
			            acquired:        item.acquired || '',
			            status:          item.status || '',
			            applicationName: item.applicationName || '',
			            accountName:     item.accountName || '',
			            identityRequestId: item.identityRequestId || ''
			        };
			    });
            } else {
                $scope.error = 'Unexpected response format.';
            }
            $scope.loading = false;
        })
        .catch(function (error) {
            $scope.error = 'Failed to load roles: ' + (error.statusText || 'Connection Error');
            $scope.loading = false;
        });
    };

    $scope.fetchRoles();

    var debouncedFetchRoles = debounce(function (k) {
        $scope.$apply(function () {
            $scope.fetchRoles(k);
        });
    }, 300);

    $scope.$watch('roleSearch.roleName', function (newVal, oldVal) {
        if (newVal === oldVal) return;
        debouncedFetchRoles(newVal || '');
    });

    setupPaging($scope, function () { return $scope.roles; });
  }])

  // ================= ENTITLEMENTS CONTROLLER =================
 viewMyEntitlements.controller('EntitlementsTableCtrl', ['$scope', '$http', function ($scope, $http) {
    $scope.entitlements = [];
    $scope.entLoading = true;
    $scope.entError = null;
    $scope.entSearch = {};

    var entitlementsEndpoint = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserEntitlements');

    $scope.fetchEntitlements = function (attr, app) {
        $scope.entLoading = true;
        $scope.entError = null;

       // var xsrfToken = getCsrfToken();
       
        $http.get(entitlementsEndpoint, {
            withCredentials: true,
           
            params: {
                entitlementName:  attr || '',
                application: app || ''
               
            }
        })
        .then(function (response) {
            if (typeof response.data === 'string' && response.data.indexOf('<!DOCTYPE html>') > -1) {
                $scope.entError = 'Backend Error: Check identityiq.log for System Exception.';
                $scope.entLoading = false;
                return;
            }
            var objects = response.data && (response.data.objects || response.data);
			console.log(objects);
			if (objects && Array.isArray(objects.data)) {
			    $scope.entitlements = objects.data.map(function (item) {
			        return {
			            attribute:        item.attribute || '',
			            entitlement:      item.entitlement || '',
			            classifications:  item.classification || '',
			            application:      item.applicationName || '',
			            status:           item.status || '',
			            accountName:      item.accountName || '',
			            identityRequestId: item.identityRequestId || ''
			        };
			    });

            } else {
                $scope.entError = 'Unexpected response format.';
            }
            $scope.entLoading = false;
        })
        .catch(function (error) {
            $scope.entError = 'Failed to load entitlements: ' + (error.statusText || 'Connection Error');
            $scope.entLoading = false;
        });
    };

    $scope.fetchEntitlements();

    var debouncedFetchEntitlements = debounce(function (a, b) {
        $scope.$apply(function () {
            $scope.fetchEntitlements(a, b);
        });
    }, 300);

    $scope.$watchGroup(['entSearch.entitlementName', 'entSearch.application'], function (newVals, oldVals) {
        if (newVals[0] === oldVals[0] && newVals[1] === oldVals[1]) return;
        debouncedFetchEntitlements(newVals[0], newVals[1]);
    });

    setupPaging($scope, function () { return $scope.entitlements; });
  }]);


// ================= PAGING FUNCTION =================
function setupPaging($scope, getItemsFn) {
    $scope.pageSizeOptions = [5, 10, 25, 50, 100];
    $scope.pageSize = 10;
    $scope.currentPage = 0;
    $scope.pageNumber = 1;
    $scope.pagedItems = [];

    $scope.getFilteredItems = function () {
        return getItemsFn() || [];
    };

    $scope.pageCount = function () {
        return Math.max(1, Math.ceil($scope.getFilteredItems().length / $scope.pageSize));
    };

    function updatePage() {
        var start = $scope.currentPage * $scope.pageSize;
        var newItems = $scope.getFilteredItems().slice(start, start + $scope.pageSize);
        $scope.pagedItems.length = 0;
        Array.prototype.push.apply($scope.pagedItems, newItems);
    }

    $scope.goFirst   = function () { $scope.currentPage = 0; };
    $scope.goLast    = function () { $scope.currentPage = Math.max(0, $scope.pageCount() - 1); };
    $scope.prevPage  = function () { if ($scope.currentPage > 0) $scope.currentPage--; };
    $scope.nextPage  = function () { if ($scope.currentPage < $scope.pageCount() - 1) $scope.currentPage++; };
    $scope.resetPaging = function () { $scope.currentPage = 0; $scope.pageNumber = 1; };

    $scope.$watch('currentPage', function (n) {
        var expected = n + 1;
        if ($scope.pageNumber !== expected) {
            $scope.pageNumber = expected;
        }
        updatePage();
    });

    $scope.$watch('pageNumber', function (n) {
        var num = parseInt(n, 10) - 1;
        if (!isNaN(num) && num >= 0 && num < $scope.pageCount() && $scope.currentPage !== num) {
            $scope.currentPage = num;
        }
    });

    $scope.$watch('pageSize', function () {
        $scope.currentPage = 0;
        $scope.pageNumber = 1;
        updatePage();
    });

    // re-page whenever the underlying data array changes
    $scope.$watch(function () { return getItemsFn().length; }, function () {
        updatePage();
    });
}