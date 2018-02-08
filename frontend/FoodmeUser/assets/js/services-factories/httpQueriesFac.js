/**For overcome The same Origin Policy (where it is impossible to make http request between different domains)
 * server side need to provide an 'Access-Control-Allow-Origin' header. Access-Control-Allow-Origin: * - will work
 * for all requests. */
app.config(['$httpProvider', function ($httpProvider) {
    delete $httpProvider.defaults.headers.common['Content-Type']; //Fixes cross domain requests (need to comment it)
}]);

app.factory('httpGetQuery', ['$q', '$http', function($q, $http) {
    return {
        getData: function(url) {
            var delay = $q.defer();
            $http.get(url)
                .success(function(data) {
                    delay.resolve(data);
                }).error( function(data) {
                    delay.reject('Unable to fetch the item' + data);
                });
            return delay.promise; //need compulsory to return it
        }
    };
}]);
app.factory('httpPostQuery', ['$q', '$http', function($q, $http) {
    return {
        postData: function(url, sendData) {
            var delay = $q.defer();
            $http.post(url, sendData, {
                withCredentials: false
            })
                .success(function(data) {
                    delay.resolve(data);
                }).error( function(data) {
                    delay.reject('Unable to fetch the item' + data);
                });
            return delay.promise;
        }
    };
}]);
app.factory('newHttpPostQuery', ['$q', '$http', function($q, $http) {
    return {
        postData: function(url, sendData, authEncoded) {
            var delay = $q.defer();
            $http.post(url, sendData, {
                //"withCredentials": false,
                "Authorization": "Basic " + authEncoded, //Zm9vZG1lV2ViOmJmZTBmZjcyLTY2YzctNDhlOC05NzE5LTNhMGNmOTg1MTAxNQ==
                "Content-Type": "application/x-www-form-urlencoded; charset=utf-8"
            })
                .success(function(data) {
                    delay.resolve(data);
                }).error( function(data) {
                    delay.reject('Unable to fetch the item' + data);
                });
            return delay.promise;
        }
    };
}]);
app.factory('httpPutQuery', ['$q', '$http', function($q, $http) {
    return {
        putData: function(url, sendData) {
            var delay = $q.defer();
            $http.put(url, sendData, {
                withCredentials: false
            })
                .success(function(data) {
                    delay.resolve(data);
                }).error( function(data) {
                    delay.reject('Unable to fetch the item' + data);
                });
            return delay.promise;
        }
    };
}]);

/*,
 headers : {
 'Content-Type' : 'application/json' undefined
 }*/