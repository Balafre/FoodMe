app.factory('AuthService', ['$http', '$cookies', '$rootScope', '$httpParamSerializer', '$timeout', 'httpGetQuery', function ($http, $cookies, $rootScope, $httpParamSerializer, $timeout, httpGetQuery) {
    var authObj = {
        authEncoded: btoa("foodmeWeb:bfe0ff72-66c7-48e8-9719-3a0cf9851015"), //btoa - window method with encode a string in base-64
        //Zm9vZG1lV2ViOmJmZTBmZjcyLTY2YzctNDhlOC05NzE5LTNhMGNmOTg1MTAxNQ==
        login: function(paramData) {
            //console.log(paramData);
            var req = {
                method: 'POST',
                url: "http://harristest.com.mocha6001.mochahost.com/foodme/oauth/token",
                headers: {
                    "Authorization": "Basic " + this.authEncoded,
                    "Content-Type": "application/x-www-form-urlencoded; charset=utf-8"
                },
                data: $httpParamSerializer(paramData)
            };
            //console.log(req);
            return $http(req).then(function(res){
                console.log(res);
                //console.log(res.headers());
                $cookies.put("account_Id", res.data.accountId);
                $cookies.put("access_token", res.data.access_token);
                $cookies.put("expires_in", res.data.expires_in);
                $cookies.put("refresh_token", res.data.refresh_token);
                $cookies.put("validate", 'true');

                $rootScope.validate = true;
                $http.defaults.headers.common.Authorization = 'Bearer ' + res.data.access_token;


                authObj.getCurrentUserName().then(function(res){
                    console.log(res);
                    $rootScope.loggedUser = res.data.firstName + ' ' + res.data.lastName;
                    $cookies.put("logged_user", $rootScope.loggedUser);
                });


                authObj.getUserRestaurantsData(res.data.accountId).then(function(res){
                    console.log('object is taken, reply is:');
                    console.log(res);
                    if(res) {
                        if(res.data){
                            if(res.data.length > 0) {
                                console.log(res.data[0]);
                                $cookies.put("restaurant_Id", res.data[0].id);
                                $rootScope.$emit("CallGetRestaurantDataMethod", {}); //call method from the main page scope
                                $rootScope.$emit("CallGetMenuDataMethod", {}); //call method from the main page scope

                                var currentCurrency = $cookies.get('restaurant_currency');
                                if(!currentCurrency) {
                                    var restaurantDataPromiseGet = httpGetQuery.getData('http://harristest.com.mocha6001.mochahost.com/foodme/restaurant/' + res.data[0].id);
                                    restaurantDataPromiseGet.then(function(value) {
                                        console.log('object is retrieved, reply is:');
                                        if(value) {
                                            console.log(value);
                                            $cookies.put('restaurant_currency', value.currency);
                                            $rootScope.restaurantCurrency = value.currency;
                                        }
                                    });
                                }


                                if(res.data[0].menus) {
                                    if(res.data[0].menus.length > 0) {
                                        if(res.data[0].menus[0].id) {
                                            $cookies.put('current_menu_Id', res.data[0].menus[0].id); //made current menu the first one
                                            $rootScope.currentMenu = +res.data[0].menus[0].id;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

                var newLoginData = {
                    grant_type: "refresh_token",
                    refresh_token: res.data.refresh_token.trim()
                };
                console.log(newLoginData);
                $timeout(function(){authObj.login(newLoginData)}, res.data.expires_in * 100 - 30000);

            }, function (err) {
                //console.log(err);
                $rootScope.errorMessage = true;
                $rootScope.validate = false;
                $cookies.put("validate", 'false');
            });
        },
        registration: function(paramData) {
            //console.log(paramData);
            console.log($httpParamSerializer(paramData));
            var req = {
                method: 'POST',
                url: "http://harristest.com.mocha6001.mochahost.com/foodme/account",
                headers: {
                    "Content-Type": "application/json; charset=utf-8" //application/x-www-form-urlencoded
                },
                data: JSON.stringify(paramData)
            };
            console.log(req);
            return $http(req).then(function(res){
                console.log(res);
                if(res.statusText === 'Created') {
                    $cookies.put("registered", 'true');
                }
            }, function (err) {
                console.log(err);
                $rootScope.validate = false;
                $cookies.put("validate", 'false');
            });
        },
        getCurrentUserName: function() {
            var req = {
                method: 'GET',
                url: "http://harristest.com.mocha6001.mochahost.com/foodme/account/current",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded; charset=utf-8"
                }
            };
            return $http(req);
        },
        getUserRestaurantsData: function(accountId) {
            var req = {
                method: 'GET',
                url: 'http://harristest.com.mocha6001.mochahost.com/foodme/account/' + accountId + '/restaurants',
                headers: {
                    "Content-Type": "application/json; charset=utf-8"
                }
            };
            return $http(req);
        },
        refreshToken: function() {
            var expiresIn = $cookies.get("expires_in"),
                refreshToken = $cookies.get("refresh_token");
            if(refreshToken) {
                var newLoginData = {
                    grant_type: "refresh_token",
                    refresh_token: refreshToken.trim()
                };
                console.log(newLoginData);
            }
            if(newLoginData && expiresIn) {
                $timeout(function(){authObj.login(newLoginData)}, expiresIn * 100 - 30000);
            }
        }
    };
return authObj;

}]);