app.controller('AddRestaurant', ['$scope', '$rootScope', '$cookies', '$http', 'NgMap', 'httpGetQuery', 'httpPostQuery', 'httpPutQuery', 'AuthService', function($scope, $rootScope, $cookies, $http, NgMap, httpGetQuery, httpPostQuery, httpPutQuery, AuthService) {
    var validate = $cookies.get('validate'),
        loggedUser = $cookies.get('logged_user'),
        restaurantId = $cookies.get('restaurant_Id'),
        facebookToken = $cookies.get('access_token');
    $scope.loginPage = true;
    $rootScope.errorMessage = false;
    //console.log(facebookToken);
    /*
    if(facebookToken) {
        console.log('facebook works');
        $scope.facebookTokenPromiseGet = httpGetQuery.getData('http://harristest.com.mocha6001.mochahost.com/foodme/facebook/' + facebookToken);
        $scope.facebookTokenPromiseGet.then(function(value) {
            console.log('facebook object is retrieved, reply is:');
            if(value) {
                console.log(value);
            }
        });
    }*/
/**-----------------------------------------------REFRESH-TOKEN-------------------------------------------------------*/
    AuthService.refreshToken();
/**-------------------------------------------------------------------------------------------------------------------*/
    $scope.getRestaurantData = function () {
        var validate = $cookies.get('validate'),
            restaurantId = $cookies.get('restaurant_Id');
        if(validate === 'true') {
            if(loggedUser) {
                $rootScope.loggedUser = loggedUser;
            }
            if(restaurantId) {
                console.log('works');
                $scope.restaurantDataPromiseGet = httpGetQuery.getData('http://harristest.com.mocha6001.mochahost.com/foodme/restaurant/' + restaurantId);
                $scope.restaurantDataPromiseGet.then(function(value) {
                    console.log('object is retrieved, reply is:');
                    if(value) {
                        console.log(value);
                        $cookies.put('restaurant_currency', value.currency);
                        $rootScope.restaurantCurrency = value.currency;
                        $scope.fillingRestaurantData(value);
                        $scope.position = [value.geoLocation.latitude, value.geoLocation.longitude];
                        console.log($scope.position);
                    }
                });
            }
            $rootScope.validate = true;
        }else {
            $rootScope.validate = false;
        }
    };
    $scope.getRestaurantData();
    $rootScope.$on("CallGetRestaurantDataMethod", function(){
        console.log('emit works fine');
        $scope.getRestaurantData();
    });

/**-------------------------------------------SET-EMPTY-LOGIN-OBJECT-DATA---------------------------------------------*/
    $scope.addressOptions = [
        {name: 'Show before address', shortName: 'BEFORE_ADDRESS'}, //BEFORE_ADDRESS
        {name: 'Show after address', shortName: 'AFTER_ADDRESS'}, //AFTER_ADDRESS
        {name: 'Show instead of address', shortName: 'INSTEAD_OF_ADDRESS'} //INSTEAD_OF_ADDRESS
    ];
    $scope.languageOptions = [
        {name: 'English (United States)', shortName: 'EN'}
    ];
    $scope.currencyOptions = [
        {name: 'United States dollar $', shortName: 'USD'}
    ];
    $scope.setDefaultOpeningHours = function() {
        $scope.restaurantData.openingHours.dailyScheduleList = [
            {
                dayOfWeek: "MO",
                timeFrames: [
                    {
                        fromTime: '09:00 ',
                        tillTime: '18:00'
                    }
                ]
            },
            {
                dayOfWeek: "TU",
                timeFrames: [
                    {
                        fromTime: '09:00 ',
                        tillTime: '18:00'
                    }
                ]
            },
            {
                dayOfWeek: "WD",
                timeFrames: [
                    {
                        fromTime: '09:00 ',
                        tillTime: '18:00'
                    }
                ]
            },
            {
                dayOfWeek: "TH",
                timeFrames: [
                    {
                        fromTime: '09:00 ',
                        tillTime: '18:00'
                    }
                ]
            },
            {
                dayOfWeek: "FR",
                timeFrames: [
                    {
                        fromTime: '09:00 ',
                        tillTime: '18:00'
                    }
                ]
            },
            {
                dayOfWeek: "SA",
                timeFrames: [
                    {
                        fromTime: '09:00 ',
                        tillTime: '18:00'
                    }
                ]
            },
            {
                dayOfWeek: "SU",
                timeFrames: [
                    {
                        fromTime: '09:00 ',
                        tillTime: '18:00'
                    }
                ]
            }
        ]
    };
    $scope.setEmptyRegisterRestaurantData = function () {
        $scope.restaurantData = {
            name: '',
            description: '',
            address: '',
            addressDescription: '',
            selectedAddressOption: 'AFTER_ADDRESS',
            geoLocation: {
                latitude: '',
                longitude: ''
            },
            email: '',
            phoneNumber: '',
            faxNumber: '',
            openingHours: {},
            language: 'EN',
            currency: 'USD'
        };
        $scope.setDefaultOpeningHours();
        $scope.time = '';
        $scope.timeCell = 'mo_from';
        /*
        dailyScheduleList: {
         mn: {from: '2016-06-11T05:00:00.000Z', till: '2016-06-11T15:00:00.000Z'},
         tu: {from: '2016-06-11T05:00:00.000Z', till: '2016-06-11T15:00:00.000Z'},
         wd: {from: '2016-06-11T05:00:00.000Z', till: '2016-06-11T15:00:00.000Z'},
         th: {from: '2016-06-11T05:00:00.000Z', till: '2016-06-11T15:00:00.000Z'},
         fr: {from: '2016-06-11T05:00:00.000Z', till: '2016-06-11T15:00:00.000Z'},
         sa: {from: '2016-06-11T07:00:00.000Z', till: '2016-06-11T18:00:00.000Z'},
         su: {from: '2016-06-11T07:00:00.000Z', till: '2016-06-11T18:00:00.000Z'}
         },
         */
    };
    $scope.setEmptyRegisterRestaurantData();

    $scope.fillingRestaurantData = function (value) {
        /*
         $scope.restaurantData = {
         name: value.name,
         description: value.description,
         address: value.address,
         addressDescription: value.addressDescription,
         selectedAddressOption: value.selectedAddressOption,
         geoLocation: {
         latitude: value.geoLocation.latitude,
         longitude: value.geoLocation.longitude
         },
         email: value.email,
         phoneNumber: value.phoneNumber,
         faxNumber: value.faxNumber,
         openingHours: value.openingHours,
         language: value.language,
         currency: value.currency,
         id: value.id,
         menus: value.menus
         };
        */
        $scope.restaurantData = value;

        if(value.openingHours.dailyScheduleList.length !== 7) {
            $scope.setDefaultOpeningHours();
        }
    };
    $scope.checkCurrentItem = function(x, time) {
        var makeLengthTwo = function pad(d) {
            return (d < 10) ? '0' + d.toString() : d.toString();
        };
        console.log(time);
        //time = new Date(time.valueOf() + time.getTimezoneOffset() * 60000); // convert to unic time
        var currentMonth = makeLengthTwo(time.getMonth() + 1),
            currentDate = makeLengthTwo(time.getDate()),
            currentHour = makeLengthTwo(time.getHours()),
            currentMinutes = makeLengthTwo(time.getMinutes()),
            convertedTime = time.getFullYear() + '-' + currentMonth + '-' + currentDate + 'T' + currentHour + ':' + currentMinutes + ':00.000Z',
            newConvertedTime = currentHour + ':' + currentMinutes;
        console.log(convertedTime);
        console.log(newConvertedTime);
        var dayOfWeek = x.substr(0, x.indexOf('_')).toUpperCase(),
            fromTill = x.substr(x.indexOf('_') + 1, x.length) + 'Time',
            i;
        for(i = $scope.restaurantData.openingHours.dailyScheduleList.length; i--;) {
            if($scope.restaurantData.openingHours.dailyScheduleList[i].dayOfWeek === dayOfWeek) {
                $scope.restaurantData.openingHours.dailyScheduleList[i].timeFrames[0][fromTill] = newConvertedTime;
            }
        }
    };

    $scope.lengthControl = function() {
        if($scope.restaurantData.description.length >= 150) {
            $scope.restaurantData.description = $scope.restaurantData.description.substr(0, 150);
        }
    };

    $scope.showPosition = function() {
        console.log($scope.restaurantData.address);
        if ($scope.restaurantData.address === '' || $scope.restaurantData.address === undefined) {
                navigator.geolocation.getCurrentPosition(function(pos) {
                    console.log(pos);
                    $scope.position = [pos.coords.latitude, pos.coords.longitude];
                    //$scope.position = new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
                },
                function(error) {
                    alert('Unable to get location: ' + error.message);
                }, {enableHighAccuracy: true});
        } else if($scope.restaurantData.address && $scope.restaurantData.address !== '') {
            console.log($scope.position);
            $scope.position = $scope.restaurantData.address.trim();
        }
    };
    if(!restaurantId) {
        $scope.showPosition();
    }


    $scope.markerClick = function() {
        console.log(this); //marker
        console.log(this.map.getCenter().lat()); //map center position
        console.log(this.getPosition().lat()); //marker position
    };

    $scope.centreChange = function() {
        console.log(this); //map
        console.log(this.getCenter().lat()); //map center position
        console.log(this.markers[0].getPosition().lat()); //marker position
        var mapPosition = this.getCenter(),
            mapPositionLat = mapPosition.lat(),
            mapPositionLng = mapPosition.lng();
            //markerPosition = this.markers[0].getPosition(),
            //markerPositionLat = markerPosition.lat(),
            //markerPositionLng = markerPosition.lng();

        $scope.restaurantData.geoLocation = {
            latitude: mapPositionLat,
            longitude: mapPositionLng
        };
        console.log(mapPositionLat + ' x ' + mapPositionLng);
        //console.log(markerPositionLat + ' x ' + markerPositionLng); //marker one step late of map position, we can't use it
        //console.log($scope.restaurantData.geoLocation);
    };

/**------------------------------------------------ALL-OBJ-SAVING-----------------------------------------------------*/
$scope.addNewRestaurantObject = function (form) {
    console.log(form);
    var firstError = null;
    if (form.$invalid) {
        var field = null, firstError = null;
        for (field in form) {
            if (field[0] != '$') {
                if (firstError === null && !form[field].$valid) {
                    firstError = form[field].$name;
                }
                if (form[field].$pristine) {
                    form[field].$dirty = true;
                }
            }
        }
        angular.element('.ng-invalid[name=' + firstError + ']').focus();
        return;
    } else {

        console.log('hello');
        console.log($scope.restaurantData);
        console.log(angular.toJson($scope.restaurantData));
        var restaurantId = $cookies.get('restaurant_Id'),
            accountId = $cookies.get("account_Id");
        if(accountId) {
            if(!restaurantId) {
                $scope.restaurantDataPromisePost = httpPostQuery.postData('http://harristest.com.mocha6001.mochahost.com/foodme/account/' + accountId + '/restaurant', $scope.restaurantData);
                $scope.restaurantDataPromisePost.then(function(value) {
                    console.log('object is send, reply is:');
                    console.log(value);
                    $scope.getRestaurantData();
                });
            } else {
                $scope.restaurantDataPromisePut = httpPutQuery.putData('http://harristest.com.mocha6001.mochahost.com/foodme/restaurant/' + restaurantId, $scope.restaurantData);
                $scope.restaurantDataPromisePut.then(function(value) {
                    console.log('object is updated, reply is:');
                    console.log(value);
                    $scope.getRestaurantData();
                });
            }
        }
    }
};
/**---------------------------------------------------CANCEL----------------------------------------------------------*/
$scope.cancelFunctionality = function() {
    var restaurantId = $cookies.get('restaurant_Id'),
        accountId = $cookies.get("account_Id");
    if(accountId) {
        if(!restaurantId) {
            $scope.setEmptyRegisterRestaurantData();
        } else {
            $scope.restaurantDataPromiseGet = httpGetQuery.getData('http://harristest.com.mocha6001.mochahost.com/foodme/restaurant/' + restaurantId);
            $scope.restaurantDataPromiseGet.then(function(value) {
                console.log('object is retrieved, reply is:');
                if(value) {
                    console.log(value);
                    $scope.fillingRestaurantData(value);
                }
            });
        }
    }
}



}]);
