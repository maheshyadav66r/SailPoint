$http.defaults.withCredentials = true;
function getCookie(name) {
  var match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  if (match) {
    var result = decodeURIComponent(match[2]);
    console.log('Retrieved cookie ' + name + ':', result);
    return result;
  }
  console.log('Cookie ' + name + ' not found');
  return null;
}

function getCSRFToken() {

  // ✅ FIX: SailPoint uses CSRF-TOKEN (not XSRF-TOKEN)
  var token = getCookie('CSRF-TOKEN');

  if (token) {
    console.log('CSRF token from cookie:', token);
    return token;
  }

  // fallback
  if (window.PluginHelper && typeof window.PluginHelper.getCsrfToken === 'function') {
    var helperToken = window.PluginHelper.getCsrfToken();
    console.log('CSRF token from PluginHelper:', helperToken);
    return helperToken;
  }

  console.warn('No CSRF token found');
  return '';
}

// ✅ central header builder
function getHeaders() {
  var token = getCSRFToken();
  var headers = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  };

  if (token) {
    headers['X-XSRF-TOKEN'] = token;
  }

  return headers;
}

// ================= ROLES =================
angular.module('myApp', [])

.controller('RolesTableCtrl', ['$scope', '$filter', '$http', function ($scope, $filter, $http) {

  $scope.roles = [];
  $scope.loading = true;
  $scope.error = null;
  $scope.roleSearch = {};

  var rolesEndpoint = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserRoles');

  $scope.fetchRoles = function (searchKeyword) {

    $scope.loading = true;
    $scope.error = null;

    var url = rolesEndpoint;

    if (searchKeyword) {
      url += '?roleName=' + encodeURIComponent(searchKeyword);
    }

    console.log('Fetching roles:', url);

    var headers = getHeaders(); // ✅ FIX

    $http.get(url, {
      withCredentials: true,
      headers: headers
    })
    .then(function (response) {

      if (response.data && response.data.objects) {
        $scope.roles = response.data.objects;
      } else if (Array.isArray(response.data)) {
        $scope.roles = response.data;
      } else {
        $scope.roles = [];
        console.warn('Unexpected format:', response.data);
      }

      $scope.loading = false;

    })
    .catch(function (error) {

      console.error('Roles API Error:', error);

      var errorMsg = 'Failed to load roles: ';

      if (error.status === 403) {
        errorMsg += 'CSRF issue';
        console.error('Token:', getCSRFToken());
      } else {
        errorMsg += error.statusText;
      }

      $scope.error = errorMsg;
      $scope.loading = false;
    });
  };

  $scope.fetchRoles();

}])

// ================= ENTITLEMENTS =================
.controller('EntitlementsTableCtrl', ['$scope', '$filter', '$http', function ($scope, $filter, $http) {

  $scope.entitlements = [];
  $scope.entLoading = true;
  $scope.entError = null;

  var endpoint = PluginHelper.getPluginRestUrl('viewMyEntitlements/getUserEntitlements');

  $scope.fetchEntitlements = function () {

    $scope.entLoading = true;
    $scope.entError = null;

    var headers = getHeaders(); // ✅ FIX

    $http.get(endpoint, {
      withCredentials: true,
      headers: headers
    })
    .then(function (response) {

      if (response.data && response.data.objects) {
        $scope.entitlements = response.data.objects;
      } else {
        $scope.entitlements = [];
      }

      $scope.entLoading = false;

    })
    .catch(function (error) {

      console.error('Entitlements API Error:', error);

      var errorMsg = 'Failed to load entitlements: ';

      if (error.status === 403) {
        errorMsg += 'CSRF issue';
        console.error('Token:', getCSRFToken());
      } else {
        errorMsg += error.statusText;
      }

      $scope.entError = errorMsg;
      $scope.entLoading = false;
    });
  };

  $scope.fetchEntitlements();

}]);