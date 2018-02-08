'use strict';

/**
 * Config for the router
 */
app.config(['$stateProvider', '$urlRouterProvider', '$controllerProvider', '$compileProvider', '$filterProvider', '$provide', '$ocLazyLoadProvider', 'JS_REQUIRES',
function ($stateProvider, $urlRouterProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $ocLazyLoadProvider, jsRequires) {

    app.controller = $controllerProvider.register;
    app.directive = $compileProvider.directive;
    app.filter = $filterProvider.register;
    app.factory = $provide.factory;
    app.service = $provide.service;
    app.constant = $provide.constant;
    app.value = $provide.value;

    // LAZY MODULES

    $ocLazyLoadProvider.config({
        debug: false,
        events: true,
        modules: jsRequires.modules
    });

    // APPLICATION ROUTES
    // -----------------------------------
    // For any unmatched url, redirect to /app/dashboard
    $urlRouterProvider.otherwise("/app/add_restaurant");
    //
    // Set up the states
    $stateProvider.state('app', {
        url: "/app",
        templateUrl: "assets/views/app.html",
        resolve: loadSequence('chartjs', 'chart.js', 'chatCtrl', 'authSer'),
        abstract: true
    }).state('app.add_restaurant', {
        url: '/add_restaurant',
        templateUrl: "assets/views/add_restaurant.html",
        title: 'Add Restaurant',
        ncyBreadcrumb: {
            label: 'Add Restaurant'
        },
        resolve: loadSequence('addRestaurantCtrl', 'dataSer', 'httpQueriesFac', 'ngMap', 'mapsCtrl')
    }).state('app.add_menu', {
        url: '/add_menu',
        templateUrl: "assets/views/add_menu.html",
        title: 'Add Menu',
        ncyBreadcrumb: {
            label: 'Add Menu'
        },
        resolve: loadSequence('addMenuCtrl', 'dataSer', 'httpQueriesFac')
    }).state('app.orders', {
        url: '/orders',
        templateUrl: "assets/views/orders.html",
        title: 'Orders',
        ncyBreadcrumb: {
            label: 'Orders'
        },
        resolve: loadSequence('ordersCtrl', 'dataSer', 'httpQueriesFac')
    }).state('app.pagelayouts', {
        url: '/ui',
        template: '<div ui-view class="fade-in-up"></div>',
        title: 'Page Layouts',
        ncyBreadcrumb: {
            label: 'Page Layouts'
        }
    }).state('app.ui.treeview', {
        url: '/treeview',
        templateUrl: "assets/views/ui_tree.html",
        title: 'TreeView',
        ncyBreadcrumb: {
            label: 'Treeview'
        },
        resolve: loadSequence('angularBootstrapNavTree', 'treeCtrl')
    });
    // Generates a resolve object previously configured in constant.JS_REQUIRES (config.constant.js)
    function loadSequence() {
        var _args = arguments;
        return {
            deps: ['$ocLazyLoad', '$q',
			function ($ocLL, $q) {
			    var promise = $q.when(1);
			    for (var i = 0, len = _args.length; i < len; i++) {
			        promise = promiseThen(_args[i]);
			    }
			    return promise;

			    function promiseThen(_arg) {
			        if (typeof _arg == 'function')
			            return promise.then(_arg);
			        else
			            return promise.then(function () {
			                var nowLoad = requiredData(_arg);
			                if (!nowLoad)
			                    return $.error('Route resolve: Bad resource name [' + _arg + ']');
			                return $ocLL.load(nowLoad);
			            });
			    }

			    function requiredData(name) {
			        if (jsRequires.modules)
			            for (var m in jsRequires.modules)
			                if (jsRequires.modules[m].name && jsRequires.modules[m].name === name)
			                    return jsRequires.modules[m];
			        return jsRequires.scripts && jsRequires.scripts[name];
			    }
			}]
        };
    }
}]);