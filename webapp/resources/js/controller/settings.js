'use strict';

var SettingsController = function($scope, $http) {
    $scope.fetchAllSettings = function() {
        $http.post('entity/setting/query',{},{}).success(function(settings){
            $scope.settings = settings;
        });
    }

    $scope.fetchAllSettings();
}
