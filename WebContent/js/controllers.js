var appControllers = angular.module('appControllers', []);
 

appControllers.controller('ListController', ['$scope', '$http', function($scope, $http) {
	
	$scope.search = function() {
		$http.get('http://localhost:8080/Example02/servlet1?query=' + $scope.query).success(function(data) {
			$scope.apps = data;
			$scope.appOrder = Rating;
		});
	};
}]);



appControllers.controller('DetailsController', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
  $http.get('http://localhost:8080/Example02/servlet1?query=data').success(function(data) {
    $scope.apps = data;
    $scope.whichItem = $routeParams.itemId;
    
    if ($routeParams.itemId > 0) {
      $scope.prevItem = Number($routeParams.itemId)-1
    } else {
      $scope.prevItem = $scope.apps.length-1;
    }
    
    if ($routeParams.itemId < $scope.apps.length-1) {
      $scope.nextItem = Number($routeParams.itemId)+1
    } else {
      $scope.nextItem = 0;
    }
      
  });
}]);
