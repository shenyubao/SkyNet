'use strict';
(function( $ ) {

	// Declare app level module which depends on filters, and services
	$.module( 'skyNet', [
	  'ngRoute',
	  'skynet.controllers',
	  'skynet.services',
	  'skynet.directives'
	]).
	config(['$routeProvider', function( $routeProvider ) {
		$routeProvider.when('/desktop', {templateUrl: 'partials/desktop.html', controller: 'desktop'});
	    $routeProvider.when('/login', {templateUrl: 'partials/login.html', controller: 'login'});
	    $routeProvider.otherwise({redirectTo: '/'});
	}]);
})( angular );