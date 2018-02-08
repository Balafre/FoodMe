/**For overcome The same Origin Policy (where it is impossible to make http request between different domains)
 * server side need to provide an 'Access-Control-Allow-Origin' header. Access-Control-Allow-Origin: * - will work
 * for all requests. */
app.config(['$httpProvider', function ($httpProvider) {
    delete $httpProvider.defaults.headers.common['Content-Type']; //Fixes cross domain requests (need to comment it)
}]);

app.factory('httpGetQuery', ['$q', '$http', '$cookies', function($q, $http, $cookies) {
    var accessToken = $cookies.get('access_token');
    if(accessToken) {
        $http.defaults.headers.common.Authorization = 'Bearer ' + accessToken;
    }
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
app.factory('httpPostQuery', ['$q', '$http', '$cookies', function($q, $http, $cookies) {
    var accessToken = $cookies.get('access_token');
    if(accessToken) {
        $http.defaults.headers.common.Authorization = 'Bearer ' + accessToken;
    }
    return {
        postData: function(url, sendData) {
            var delay = $q.defer();
            $http.post(url, sendData, {
                //withCredentials: false,
                "Content-Type": "application/json; charset=utf-8"
            })
                .success(function(data, status, headers, config) {
                    //console.log(data);
                    //console.log(status);
                    console.log(headers());
                    if(headers().location) {
                        var locationURL = headers().location,
                            lastSlash = locationURL.lastIndexOf("/"),
                            length = locationURL.length,
                            entityId = locationURL.substring(lastSlash + 1, length),
                            cutUrl =  locationURL.substring(0, lastSlash),
                            previousSlash = cutUrl.lastIndexOf("/"),
                            cutUrlLength = cutUrl.length,
                            entity = cutUrl.substring(previousSlash + 1, cutUrlLength);

                        console.log(entityId);
                        console.log(cutUrl);
                        console.log(entity);
                        $cookies.put(entity + '_Id', entityId);
                    }


                    delay.resolve(data);
                }).error( function(data) {
                    delay.reject('Unable to fetch the item' + data);
                });
            return delay.promise;
        }
    };
}]);
app.factory('httpPutQuery', ['$q', '$http', '$cookies', function($q, $http, $cookies) {
    var accessToken = $cookies.get('access_token');
    if(accessToken) {
        $http.defaults.headers.common.Authorization = 'Bearer ' + accessToken;
    }
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
app.factory('httpDeleteQuery', ['$q', '$http', '$cookies', function($q, $http, $cookies) {
    var accessToken = $cookies.get('access_token');
    if(accessToken) {
        $http.defaults.headers.common.Authorization = 'Bearer ' + accessToken;
    }
    return {
        deleteData: function(url) {
            var delay = $q.defer();
            $http.delete(url)
                .success(function(data) {
                    delay.resolve(data);
                }).error( function(data) {
                    delay.reject('Unable to fetch the item' + data);
                });
            return delay.promise; //need compulsory to return it
        }
    };
}]);
app.factory('httpPostCloudinaryQuery', ['$q', '$http', function($q, $http) {
    //delete $http.defaults.headers.common.Authorization;
    return {
        postData: function(url, sendData) {
            var delay = $q.defer(),
            transform = function(data, headersGetter) {
                var headers = headersGetter();
                console.log(headers);
                delete headers['authorization'];
                //delete headers['content-type'];
                //delete headers['accept'];
                console.log(headers);
                return headers;
            };
            $http.post(url, sendData, {
                headers: {"Content-Type": "application/json; charset=utf-8"},
                transformRequest: transform
            }).success(function(data) {
                    delay.resolve(data);
                }).error( function(data) {
                    delay.reject('Unable to fetch the item' + data);
                });
            return delay.promise;

            /*
            $http({
                method: 'POST',
                url: url,
                headers: {
                    'Content-Type': undefined,
                    'X-Requested-With': 'XMLHttpRequest'
                },
                transformRequest: function(data, headersGetter) {
                    var headers = headersGetter();
                    delete headers['authorization'];
                    return headers;
                }
            }).success(function(data) {
                delay.resolve(data);
            }).error( function(data) {
                delay.reject('Unable to fetch the item' + data);
            });
            return delay.promise;
            */

        }
    };
}]);