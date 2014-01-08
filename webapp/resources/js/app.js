'use strict';

var textManagerApp = {};

var App = angular.module('textManagerApp', ['ngGrid']);

// Declare app level module which depends on filters, and services
App.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/Settings', {
        templateUrl: '/layout/settings',
        controller: SettingsController
    });

    $routeProvider.otherwise({redirectTo: '/welcome'});
}]);
