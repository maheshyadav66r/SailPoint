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

var viewOrphanIdentities = angular.module('viewOrphanIdentities', ['ui.bootstrap', 'sailpoint.i18n']);

viewOrphanIdentities.config(function ($httpProvider) {
    $httpProvider.defaults.xsrfCookieName = "CSRF-TOKEN";
});

/* ================= APP CONTROLLER ================= */
viewOrphanIdentities.controller('AppCtrl', ['$scope', '$rootScope',
function ($scope, $rootScope) {

    $rootScope.orphanLoading = true;

    $scope.$watch(function () {
        return $rootScope.orphanLoading;
    }, function (val) {
        $scope.isPageLoading = val;
    });

    /* Registry of REST endpoints exposed by this plugin. Add a new entry here
       (plus its own {key}TableCtrl + ng-if section in page.xhtml) whenever a
       new endpoint is added — the widget above picks it up automatically. */
    $scope.endpoints = [
        {
            key: 'accounts',
            label: 'Orphan Identities',
            description: 'Orphan identities with their linked accounts'
        },
        {
            key: 'summary',
            label: 'Uncorrelated Identities',
            description: 'OUncorrelated Identities with a comma-separated list of linked application names'
        }
    ];

    $scope.activeEndpoint = 'accounts';

    $scope.selectEndpoint = function (key) {
        $scope.activeEndpoint = key;
    };
}]);


/* ===========================================================
   ORPHAN IDENTITIES (ACCOUNTS) CONTROLLER
   =========================================================== */
viewOrphanIdentities.controller('OrphanAccountsTableCtrl',
['$scope', '$http', '$rootScope',
function ($scope, $http, $rootScope) {

    var orphanEndpoint = PluginHelper.getPluginRestUrl('ViewOrphanIdentities/getOrphanUserAccounts');

    $scope.allUsers   = [];
    $scope.pagedUsers = [];
    $scope.loading    = true;
    $scope.error      = null;
    $scope.userName   = '';
    $scope.selectedLink = null;

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

    /* Derive the Link Names column and paginate the already-fetched result set client side,
       since the REST resource returns the full filtered list in one call (no server paging). */
    $scope._applyPaging = function () {
        $scope.totalCount = $scope.allUsers.length;
        $scope.totalPages = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
        if ($scope.currentPage > $scope.totalPages) { $scope.currentPage = $scope.totalPages; }
        if ($scope.currentPage < 1) { $scope.currentPage = 1; }

        var start = ($scope.currentPage - 1) * $scope.pageSize;
        $scope.pagedUsers = $scope.allUsers.slice(start, start + $scope.pageSize);
        $scope._updateRecordRange();
    };

    $scope.fetchOrphanUsers = function () {
        $scope.loading = true;
        $rootScope.orphanLoading = true;
        $scope.error = null;

        $http.get(orphanEndpoint, {
            withCredentials: true,
            params: { userName: $scope.userName || '' }
        })
        .then(function (response) {
            var data = response.data && (response.data.objects || response.data);
            var users = (data && Array.isArray(data.users)) ? data.users : [];

            $scope.allUsers = users.map(function (user) {
                var links = user.links || [];
                var names = links.map(function (link) {
                    return link.application || link.displayName || link.nativeIdentity;
                }).filter(function (n) { return !!n; });

                user.linkNamesDisplay = names.length ? names.join(', ') : (user.linksNames || '—');
                return user;
            });

            $scope.currentPage = 1;
            $scope._applyPaging();

            $scope.loading = false;
            $rootScope.orphanLoading = false;
        })
        .catch(function () {
            $scope.error = "Failed to load orphan identities";
            $scope.loading = false;
            $rootScope.orphanLoading = false;
        });
    };

    /* Debounced text filter watch */
    var debouncedFetch = debounce(function () {
        $scope.$apply(function () {
            $scope.fetchOrphanUsers();
        });
    }, 300);

    $scope.$watch('userName', function (n, o) {
        if (n === o) return;
        debouncedFetch();
    });

    $scope.$watch('pageSize', function (n, o) {
        if (n === o) return;
        $scope.currentPage = 1;
        $scope._applyPaging();
    });

    $scope.onPageInputChange = function () {
        var p = parseInt($scope.currentPage, 10);
        if (isNaN(p) || p < 1)      { $scope.currentPage = 1; }
        if (p > $scope.totalPages)  { $scope.currentPage = $scope.totalPages; }
        $scope._applyPaging();
    };

    $scope.goFirst = function () {
        if ($scope.currentPage > 1) { $scope.currentPage = 1; $scope._applyPaging(); }
    };
    $scope.goLast = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage = $scope.totalPages; $scope._applyPaging(); }
    };
    $scope.prevPage = function () {
        if ($scope.currentPage > 1) { $scope.currentPage--; $scope._applyPaging(); }
    };
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage++; $scope._applyPaging(); }
    };

    $scope.resetPaging = function () {
        $scope.currentPage = 1;
        $scope.pageSize     = 10;
        $scope.userName      = '';
        $scope.fetchOrphanUsers();
    };

    /* Link hyperlink -> detail modal */
    $scope.showLinkModal = function (link) {
        $scope.selectedLink = link;
    };
    $scope.closeLinkModal = function () {
        $scope.selectedLink = null;
    };
    $scope.hasAttributes = function () {
        var attrs = $scope.selectedLink && $scope.selectedLink.attributes;
        return !!attrs && Object.keys(attrs).length > 0;
    };

    /* Initial load */
    $scope.fetchOrphanUsers();
}]);


/* ===========================================================
   ORPHAN IDENTITIES (SUMMARY) CONTROLLER
   =========================================================== */
viewOrphanIdentities.controller('OrphanSummaryTableCtrl',
['$scope', '$http', '$rootScope',
function ($scope, $http, $rootScope) {

    var orphanEndpoint = PluginHelper.getPluginRestUrl('ViewOrphanIdentities/getUncorrelatedUsers');

    $scope.allUsers   = [];
    $scope.pagedUsers = [];
    $scope.loading    = true;
    $scope.error      = null;
    $scope.userName   = '';

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

    /* Paginate the already-fetched result set client side, since the REST resource
       returns the full filtered list in one call (no server paging). */
    $scope._applyPaging = function () {
        $scope.totalCount = $scope.allUsers.length;
        $scope.totalPages = Math.max(1, Math.ceil($scope.totalCount / $scope.pageSize));
        if ($scope.currentPage > $scope.totalPages) { $scope.currentPage = $scope.totalPages; }
        if ($scope.currentPage < 1) { $scope.currentPage = 1; }

        var start = ($scope.currentPage - 1) * $scope.pageSize;
        $scope.pagedUsers = $scope.allUsers.slice(start, start + $scope.pageSize);
        $scope._updateRecordRange();
    };

    $scope.fetchOrphanUsers = function () {
        $scope.loading = true;
        $rootScope.orphanLoading = true;
        $scope.error = null;

        $http.get(orphanEndpoint, {
            withCredentials: true,
            params: { userName: $scope.userName || '' }
        })
        .then(function (response) {
            var data = response.data && (response.data.objects || response.data);
            var users = (data && Array.isArray(data.users)) ? data.users : [];

            $scope.allUsers = users;
            $scope.currentPage = 1;
            $scope._applyPaging();

            $scope.loading = false;
            $rootScope.orphanLoading = false;
        })
        .catch(function () {
            $scope.error = "Failed to load orphan identities";
            $scope.loading = false;
            $rootScope.orphanLoading = false;
        });
    };

    /* Debounced text filter watch */
    var debouncedFetch = debounce(function () {
        $scope.$apply(function () {
            $scope.fetchOrphanUsers();
        });
    }, 300);

    $scope.$watch('userName', function (n, o) {
        if (n === o) return;
        debouncedFetch();
    });

    $scope.$watch('pageSize', function (n, o) {
        if (n === o) return;
        $scope.currentPage = 1;
        $scope._applyPaging();
    });

    $scope.onPageInputChange = function () {
        var p = parseInt($scope.currentPage, 10);
        if (isNaN(p) || p < 1)      { $scope.currentPage = 1; }
        if (p > $scope.totalPages)  { $scope.currentPage = $scope.totalPages; }
        $scope._applyPaging();
    };

    $scope.goFirst = function () {
        if ($scope.currentPage > 1) { $scope.currentPage = 1; $scope._applyPaging(); }
    };
    $scope.goLast = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage = $scope.totalPages; $scope._applyPaging(); }
    };
    $scope.prevPage = function () {
        if ($scope.currentPage > 1) { $scope.currentPage--; $scope._applyPaging(); }
    };
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages) { $scope.currentPage++; $scope._applyPaging(); }
    };

    $scope.resetPaging = function () {
        $scope.currentPage = 1;
        $scope.pageSize     = 10;
        $scope.userName      = '';
        $scope.fetchOrphanUsers();
    };

    /* Initial load */
    $scope.fetchOrphanUsers();
}]);