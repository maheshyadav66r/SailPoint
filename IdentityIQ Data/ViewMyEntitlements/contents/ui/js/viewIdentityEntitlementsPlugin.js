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

/* ================= APP CONTROLLER ================= */
viewMyEntitlements.controller('AppCtrl', ['$scope', '$rootScope',
function ($scope, $rootScope) {

    $rootScope.rolesLoading = true;
    $rootScope.entLoading   = true;

    $scope.$watch(function () {
        return $rootScope.rolesLoading || $rootScope.entLoading;
    }, function (val) {
        $scope.isPageLoading = val;
    });
}]);


/* ===========================================================
   ROLES CONTROLLER
   =========================================================== */
viewMyEntitlements.controller('RolesTableCtrl',
['$scope', '$http', '$q', '$rootScope',
function ($scope, $http, $q, $rootScope) {

    var rolesEndpoint      = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserRoles');
    var rolesCountEndpoint = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserRolesCount');

    $scope.roles        = [];
    $scope.loading      = true;
    $scope.error        = null;
    $scope.roleName     = '';
    $scope.pendingRoles = false;   /* false = show committed, true = show pending */

    $scope.pageSizeOptions = [5, 10, 25, 50, 100];
    $scope.pageSize    = 10;
    $scope.currentPage = 1;
    $scope.totalCount  = 0;
    $scope.totalPages  = 1;
    $scope.startRecord = 0;
    $scope.endRecord   = 0;

    $scope._updateRecordRange = function () {
        if ($scope.totalCount === 0) {
            $scope.startRecord = 0;
            $scope.endRecord   = 0;
        } else {
            $scope.startRecord = (($scope.currentPage - 1) * $scope.pageSize) + 1;
            $scope.endRecord   = Math.min($scope.currentPage * $scope.pageSize, $scope.totalCount);
        }
    };

    /**
     * Build query params.
     * IMPORTANT: key name must exactly match Java @QueryParam("isPendingRoles")
     */
    $scope._buildParams = function (includePaging) {
        var p = {
            roleName      : $scope.roleName || '',
            isPendingRoles: $scope.pendingRoles   /* matches @QueryParam("isPendingRoles") in Java */
        };
        if (includePaging) {
            p.page = $scope.currentPage;
            p.size = $scope.pageSize;
        }
        return p;
    };

    /* Fire count + data in parallel — call on filter change or reset */
    $scope.fetchRolesWithCount = function () {
        $scope.loading = true;
        $rootScope.rolesLoading = true;
        $scope.error = null;

        $q.all({
            count : $http.get(rolesCountEndpoint, { withCredentials: true, params: $scope._buildParams(false) }),
            data  : $http.get(rolesEndpoint,      { withCredentials: true, params: $scope._buildParams(true)  })
        })
        .then(function (results) {

            /* count */
            var countData = results.count.data && (results.count.data.objects || results.count.data);
            $scope.totalCount = (countData && countData.count != null) ? countData.count : 0;
            $scope.totalPages = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
            if ($scope.currentPage > $scope.totalPages) { $scope.currentPage = $scope.totalPages; }

            /* data */
            var data = results.data.data && (results.data.data.objects || results.data.data);
            if (data && Array.isArray(data.data)) {
                $scope.roles = data.data;
                $scope._updateRecordRange();
            } else {
                $scope.error = "Unexpected response format.";
            }

            $scope.loading = false;
            $rootScope.rolesLoading = false;
        })
        .catch(function () {
            $scope.error = "Failed to load roles";
            $scope.loading = false;
            $rootScope.rolesLoading = false;
        });
    };

    /* Data page only — count cached. Call on page / size change. */
    $scope.fetchRolesData = function () {
        $scope.loading = true;
        $rootScope.rolesLoading = true;
        $scope.error = null;

        $http.get(rolesEndpoint, {
            withCredentials: true,
            params: $scope._buildParams(true)
        })
        .then(function (response) {
            var data = response.data && (response.data.objects || response.data);
            if (data && Array.isArray(data.data)) {
                $scope.roles      = data.data;
                $scope.totalPages = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
                $scope._updateRecordRange();
            } else {
                $scope.error = "Unexpected response format.";
            }
            $scope.loading = false;
            $rootScope.rolesLoading = false;
        })
        .catch(function () {
            $scope.error = "Failed to load roles";
            $scope.loading = false;
            $rootScope.rolesLoading = false;
        });
    };

    /* Debounced text filter watch */
    var debouncedFetch = debounce(function () {
        $scope.$apply(function () {
            $scope.currentPage = 1;
            $scope.fetchRolesWithCount();
        });
    }, 300);

    $scope.$watch('roleName', function (n, o) {
        if (n === o) return;
        debouncedFetch();
    });

    /* Checkbox watch — primitive bool, fires instantly, no debounce needed */
    $scope.$watch('pendingRoles', function (newVal, oldVal) {
        if (newVal === oldVal) return;
        $scope.currentPage = 1;
        $scope.fetchRolesWithCount();
    });

    $scope.$watch('pageSize', function (n, o) {
        if (n === o) return;
        $scope.currentPage = 1;
        $scope.totalPages  = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
        $scope.fetchRolesData();
    });

    $scope.onPageInputChange = function () {
        var p = parseInt($scope.currentPage, 10);
        if (isNaN(p) || p < 1)     { $scope.currentPage = 1; }
        if (p > $scope.totalPages)  { $scope.currentPage = $scope.totalPages; }
        $scope.fetchRolesData();
    };

    $scope.goFirst = function () {
        if ($scope.currentPage > 1) { $scope.currentPage = 1; $scope.fetchRolesData(); }
    };
    $scope.goLast = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage = $scope.totalPages; $scope.fetchRolesData(); }
    };
    $scope.prevPage = function () {
        if ($scope.currentPage > 1) { $scope.currentPage--; $scope.fetchRolesData(); }
    };
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage++; $scope.fetchRolesData(); }
    };

    $scope.resetPaging = function () {
        $scope.currentPage  = 1;
        $scope.pageSize     = 10;
        $scope.roleName     = '';
        $scope.pendingRoles = false;
        $scope.fetchRolesWithCount();
    };

    /* Initial load */
    $scope.fetchRolesWithCount();
}]);


/* ===========================================================
   ENTITLEMENTS CONTROLLER
   =========================================================== */
viewMyEntitlements.controller('EntitlementsTableCtrl',
['$scope', '$http', '$q', '$rootScope',
function ($scope, $http, $q, $rootScope) {

    var entEndpoint      = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserEntitlements');
    var entCountEndpoint = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserEntitlementsCount');

    $scope.entitlements        = [];
    $scope.entLoading          = true;
    $scope.entError            = null;
    $scope.entitlementName     = '';
    $scope.application         = '';
    $scope.pendingEntitlements = false;   /* false = show committed, true = show pending */

    $scope.pageSizeOptions = [5, 10, 25, 50, 100];
    $scope.pageSize    = 10;
    $scope.currentPage = 1;
    $scope.totalCount  = 0;
    $scope.totalPages  = 1;
    $scope.startRecord = 0;
    $scope.endRecord   = 0;

    $scope._updateRecordRange = function () {
        if ($scope.totalCount === 0) {
            $scope.startRecord = 0;
            $scope.endRecord   = 0;
        } else {
            $scope.startRecord = (($scope.currentPage - 1) * $scope.pageSize) + 1;
            $scope.endRecord   = Math.min($scope.currentPage * $scope.pageSize, $scope.totalCount);
        }
    };

    /**
     * Build query params.
     * IMPORTANT: key names must exactly match Java @QueryParam annotations:
     *   @QueryParam("entitlementName"), @QueryParam("application"), @QueryParam("isPendingEntitlements")
     */
    $scope._buildParams = function (includePaging) {
        var p = {
            entitlementName        : $scope.entitlementName || '',
            application            : $scope.application     || '',
            isPendingEntitlements  : $scope.pendingEntitlements   /* matches @QueryParam("isPendingEntitlements") */
        };
        if (includePaging) {
            p.page = $scope.currentPage;
            p.size = $scope.pageSize;
        }
        return p;
    };

    /* Fire count + data in parallel — call on filter change or reset */
    $scope.fetchEntitlementsWithCount = function () {
        $scope.entLoading = true;
        $rootScope.entLoading = true;
        $scope.entError = null;

        $q.all({
            count : $http.get(entCountEndpoint, { withCredentials: true, params: $scope._buildParams(false) }),
            data  : $http.get(entEndpoint,      { withCredentials: true, params: $scope._buildParams(true)  })
        })
        .then(function (results) {

            /* count */
            var countData = results.count.data && (results.count.data.objects || results.count.data);
            $scope.totalCount = (countData && countData.count != null) ? countData.count : 0;
            $scope.totalPages = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
            if ($scope.currentPage > $scope.totalPages) { $scope.currentPage = $scope.totalPages; }

            /* data */
            var data = results.data.data && (results.data.data.objects || results.data.data);
            if (data && Array.isArray(data.data)) {
                $scope.entitlements = data.data;
                $scope._updateRecordRange();
            } else {
                $scope.entError = "Unexpected response format.";
            }

            $scope.entLoading = false;
            $rootScope.entLoading = false;
        })
        .catch(function () {
            $scope.entError = "Failed to load entitlements";
            $scope.entLoading = false;
            $rootScope.entLoading = false;
        });
    };

    /* Data page only — count cached. Call on page / size change. */
    $scope.fetchEntitlementsData = function () {
        $scope.entLoading = true;
        $rootScope.entLoading = true;
        $scope.entError = null;

        $http.get(entEndpoint, {
            withCredentials: true,
            params: $scope._buildParams(true)
        })
        .then(function (response) {
            var data = response.data && (response.data.objects || response.data);
            if (data && Array.isArray(data.data)) {
                $scope.entitlements = data.data;
                $scope.totalPages   = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
                $scope._updateRecordRange();
            } else {
                $scope.entError = "Unexpected response format.";
            }
            $scope.entLoading = false;
            $rootScope.entLoading = false;
        })
        .catch(function () {
            $scope.entError = "Failed to load entitlements";
            $scope.entLoading = false;
            $rootScope.entLoading = false;
        });
    };

    /* Debounced text filter watches */
    var debouncedFetch = debounce(function () {
        $scope.$apply(function () {
            $scope.currentPage = 1;
            $scope.fetchEntitlementsWithCount();
        });
    }, 300);

    $scope.$watch('entitlementName', function (n, o) {
        if (n === o) return;
        debouncedFetch();
    });

    $scope.$watch('application', function (n, o) {
        if (n === o) return;
        debouncedFetch();
    });

    /* Checkbox watch — primitive bool, fires instantly, no debounce needed */
    $scope.$watch('pendingEntitlements', function (newVal, oldVal) {
        if (newVal === oldVal) return;
        $scope.currentPage = 1;
        $scope.fetchEntitlementsWithCount();
    });

    $scope.$watch('pageSize', function (n, o) {
        if (n === o) return;
        $scope.currentPage = 1;
        $scope.totalPages  = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
        $scope.fetchEntitlementsData();
    });

    $scope.onPageInputChange = function () {
        var p = parseInt($scope.currentPage, 10);
        if (isNaN(p) || p < 1)     { $scope.currentPage = 1; }
        if (p > $scope.totalPages)  { $scope.currentPage = $scope.totalPages; }
        $scope.fetchEntitlementsData();
    };

    $scope.goFirst = function () {
        if ($scope.currentPage > 1) { $scope.currentPage = 1; $scope.fetchEntitlementsData(); }
    };
    $scope.goLast = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage = $scope.totalPages; $scope.fetchEntitlementsData(); }
    };
    $scope.prevPage = function () {
        if ($scope.currentPage > 1) { $scope.currentPage--; $scope.fetchEntitlementsData(); }
    };
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage++; $scope.fetchEntitlementsData(); }
    };

    $scope.resetPaging = function () {
        $scope.currentPage         = 1;
        $scope.pageSize            = 10;
        $scope.entitlementName     = '';
        $scope.application         = '';
        $scope.pendingEntitlements = false;
        $scope.fetchEntitlementsWithCount();
    };

    /* Initial load */
    $scope.fetchEntitlementsWithCount();
}]);
