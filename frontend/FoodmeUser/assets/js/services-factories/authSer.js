app.factory('AuthService', ['$http', '$cookies', '$rootScope', '$httpParamSerializer', function ($http, $cookies, $rootScope, $httpParamSerializer) {
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
                //console.log(res);
                $rootScope.validate = true;
                $http.defaults.headers.common.Authorization = 'Bearer ' + res.data.access_token;
                authObj.getCurrentUserName().then(function(res){
                    //console.log(res);
                    console.log(res.data.firstName);
                    $rootScope.loggedUser = res.data.firstName + ' ' + res.data.lastName;
                    $cookies.put("logged_user", $rootScope.loggedUser);

                });
                $cookies.put("access_token", res.data.access_token);
                $cookies.put("validate", 'true');
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
                    "Content-Type": "application/json; charset=utf-8"
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
        }
    };
return authObj;
}]);