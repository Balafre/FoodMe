'use strict';
/**
 * controllers for UI Bootstrap components
 */
app.controller('AlertDemoCtrl', ["$scope", function ($scope) {
    $scope.alerts = [{
        type: 'danger',
        msg: 'Oh snap! Change a few things up and try submitting again.'
    }, {
        type: 'success',
        msg: 'Well done! You successfully read this important alert message.'
    }];

    $scope.addAlert = function () {
        $scope.alerts.push({
            msg: 'Another alert!'
        });
    };

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };
}]).controller('ButtonsCtrl', ["$scope", function ($scope) {
    $scope.singleModel = 1;

    $scope.radioModel = 'Middle';

    $scope.checkModel = {
        left: false,
        middle: true,
        right: false
    };
}]).controller('ProgressDemoCtrl', ["$scope", function ($scope) {
    $scope.max = 200;

    $scope.random = function () {
        var value = Math.floor((Math.random() * 100) + 1);
        var type;

        if (value < 25) {
            type = 'success';
        } else if (value < 50) {
            type = 'info';
        } else if (value < 75) {
            type = 'warning';
        } else {
            type = 'danger';
        }

        $scope.showWarning = (type === 'danger' || type === 'warning');

        $scope.dynamic = value;
        $scope.type = type;
    };
    $scope.random();

    $scope.randomStacked = function () {
        $scope.stacked = [];
        var types = ['success', 'info', 'warning', 'danger'];

        for (var i = 0, n = Math.floor((Math.random() * 4) + 1) ; i < n; i++) {
            var index = Math.floor((Math.random() * 4));
            $scope.stacked.push({
                value: Math.floor((Math.random() * 30) + 1),
                type: types[index]
            });
        }
    };
    $scope.randomStacked();
}]).controller('TooltipDemoCtrl', ["$scope", function ($scope) {
    $scope.dynamicTooltip = 'I am a dynamic Tooltip text';
    $scope.dynamicTooltipText = 'I am a dynamic Tooltip Popup text';
    $scope.htmlTooltip = 'I\'ve been made <b>bold</b>!';
}]).controller('PopoverDemoCtrl', ["$scope", function ($scope) {
    $scope.dynamicPopover = 'Hello, World!';
    $scope.dynamicPopoverTitle = 'Title';
    $scope.popoverType = 'bottom';
}]).controller('PaginationDemoCtrl', ["$scope", "$log", function ($scope, $log) {
    $scope.totalItems = 64;
    $scope.currentPage = 4;

    $scope.setPage = function (pageNo) {
        $scope.currentPage = pageNo;
    };

    $scope.pageChanged = function () {
        $log.log('Page changed to: ' + $scope.currentPage);
    };

    $scope.maxSize = 5;
    $scope.bigTotalItems = 175;
    $scope.bigCurrentPage = 1;
}]).controller('RatingDemoCtrl', ["$scope", function ($scope) {
    $scope.rate = 7;
    $scope.max = 10;
    $scope.isReadonly = false;

    $scope.hoveringOver = function (value) {
        $scope.overStar = value;
        $scope.percent = 100 * (value / $scope.max);
    };

    $scope.ratingStates = [{
        stateOn: 'glyphicon-ok-sign',
        stateOff: 'glyphicon-ok-circle'
    }, {
        stateOn: 'glyphicon-star',
        stateOff: 'glyphicon-star-empty'
    }, {
        stateOn: 'glyphicon-heart',
        stateOff: 'glyphicon-ban-circle'
    }, {
        stateOn: 'glyphicon-heart'
    }, {
        stateOff: 'glyphicon-off'
    }];
}]).controller('DropdownCtrl', ["$scope", "$log", function ($scope, $log) {
    $scope.items = ['The first choice!', 'And another choice for you.', 'but wait! A third!'];

    $scope.status = {
        isopen: false
    };

    $scope.toggled = function (open) {
        $log.log('Dropdown is now: ', open);
    };

    $scope.toggleDropdown = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.status.isopen = !$scope.status.isopen;
    };
}]).controller('TabsDemoCtrl', ["$scope", "SweetAlert", function ($scope, SweetAlert) {
    $scope.tabs = [{
        title: 'Dynamic Title 1',
        content: 'Dynamic content 1'
    }, {
        title: 'Dynamic Title 2',
        content: 'Dynamic content 2',
        disabled: false
    }];

    $scope.alertMe = function () {
        setTimeout(function () {
            SweetAlert.swal({
                title: 'You\'ve selected the alert tab!',
                confirmButtonColor: '#007AFF'
            });
        });
    };
}]).controller('AccordionDemoCtrl', ["$scope", function ($scope) {
    $scope.oneAtATime = true;

    $scope.groups = [{
        title: 'Dynamic Group Header - 1',
        content: 'Dynamic Group Body - 1'
    }, {
        title: 'Dynamic Group Header - 2',
        content: 'Dynamic Group Body - 2'
    }];

    $scope.items = ['Item 1', 'Item 2', 'Item 3'];

    $scope.addItem = function () {
        var newItemNo = $scope.items.length + 1;
        $scope.items.push('Item ' + newItemNo);
    };

    $scope.status = {
        isFirstOpen: true,
        isFirstDisabled: false
    };
}]).controller('DatepickerDemoCtrl', ["$scope", "$log", function ($scope, $log) {
    $scope.today = function () {
        $scope.dt = new Date();
    };
    $scope.today();

    $scope.clear = function () {
        $scope.dt = null;
    };

    // Disable weekend selection
    $scope.disabled = function (date, mode) {
        return (mode === 'day' && (date.getDay() === 0 || date.getDay() === 6));
    };

    $scope.toggleMin = function () {
        $scope.minDate = $scope.minDate ? null : new Date();
    };
    $scope.maxDate = new Date(2020, 5, 22);
    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened = !$scope.opened;
    };
    $scope.endOpen = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.startOpened = false;
        $scope.endOpened = !$scope.endOpened;
    };
    $scope.startOpen = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.endOpened = false;
        $scope.startOpened = !$scope.startOpened;
    };

    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1
    };

    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[0];

    $scope.hstep = 1;
    $scope.mstep = 1;

    // Time Picker
    $scope.options = {
        hstep: [1, 2, 3],
        mstep: [1, 5, 10, 15, 25, 30]
    };

    $scope.ismeridian = true;
    $scope.toggleMode = function () {
        $scope.ismeridian = !$scope.ismeridian;
    };

    $scope.update = function () {
        var d = new Date();
        d.setHours(14);
        d.setMinutes(0);
        $scope.dt = d;
    };

    $scope.changed = function () {
        $log.log('Time changed to: ' + $scope.dt);
    };

    $scope.clear = function () {
        $scope.dt = null;
    };

}]).controller('DropdownCtrl', ["$scope", "$log", function ($scope, $log) {
    $scope.items = ['The first choice!', 'And another choice for you.', 'but wait! A third!'];

    $scope.status = {
        isopen: false
    };

    $scope.toggled = function (open) {
        $log.log('Dropdown is now: ', open);
    };

    $scope.toggleDropdown = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.status.isopen = !$scope.status.isopen;
    };
}]).controller('ModalLoginCtrl', ["$scope", "$rootScope", "$cookies", "$uibModal", "$log", function ($scope, $rootScope, $cookies, $uibModal, $log) {
    $scope.open = function (size, url, button) {
        var modalInstance = $uibModal.open({
            templateUrl: url,
            controller: 'ModalInstanceCtrl',
            size: size,
            resolve: {
                modalWindow: function() {
                    return button;
                }
            }
        });
        modalInstance.result.then(function (result) {
            console.log(result);
            if(result) {
                if(result.logged) {
                    $rootScope.validate = true;
                    $rootScope.loggedUser = result.loggedUser;
                }else if(result.currentMenuName) {
                    console.log('this par works fine');
                    $rootScope.currentMenu = result.currentMenuId;
                    $rootScope.$emit("CallGetMenuDataMethod", {}); //call method from the main page scope
                }
            }
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };
}]);
// Please note that $uibModalInstance represents a modal window (instance) dependency.
// It is not the same as the $uibModal service used above.
app.controller('ModalInstanceCtrl', ["$window", "$document", "$scope", "$rootScope", "$cookies", "$timeout", "Facebook", "$httpParamSerializer", "$uibModalInstance", "modalWindow", 'dataSer', 'AuthService', 'httpPostQuery', 'httpGetQuery', function ($window, $document, $scope, $rootScope, $cookies, $timeout, Facebook, $httpParamSerializer, $uibModalInstance, modalWindow, dataSer, AuthService, httpPostQuery, httpGetQuery) {
    $scope.currentPage = modalWindow;
    $scope.modalPopup = {
        loginEmail: '',
        loginPassword: '',
        recoverPasswordEmail: '',
        firstName: '',
        lastName: '',
        restaurantName: '',
        address: '',
        email: '',
        zip: '',
        phone: '',
        password: '',
        confirmPassword: '',
        newMenuName: ''
    };
    $scope.changeModal = function(form, modalName) {
        console.log('we change it');
        console.log(modalName);
        $scope.currentPage = modalName;
        if(form) {
            var field;
            for (field in form) {
                if(form[field]) {
                    if (form[field].$pristine !== undefined) {
                        form[field].$pristine = true;
                        form[field].$dirty = false;
                    }
                }
            }
        }
    };
    $scope.clearModal = function() {
        console.log('clearing');
        var i;
        for(i in $scope.modalPopup) {
            $scope.modalPopup[i] = '';
        }
        $rootScope.errorMessage = false;
    };
/**--------------------------------------------WHOLE-FORM-SUBMIT-FUNCTION---------------------------------------------*/
    $scope.send = function (form) {
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
            console.log($scope.currentPage);
            if($scope.currentPage === 'login') {
                var loginData = {
                    grant_type: "password",
                    username: $scope.modalPopup.loginEmail.trim(),
                    password: $scope.modalPopup.loginPassword.trim()
                };
                AuthService.login(loginData).then(function() {
                    var validate = $cookies.get('validate');
                    if(validate === 'true') {
                        console.log('data are correct');
                        $uibModalInstance.close();
                        $scope.clearModal();
                        $rootScope.errorMessage = false;
                        //$scope.changeModal();
                    }else {
                        console.log('data are NOT correct');
                    }
                });
            }else if($scope.currentPage === 'registration') {
                var registrationData = {
                    "firstName": $scope.modalPopup.firstName.trim(),
                    "lastName": $scope.modalPopup.lastName.trim(),
                    "restaurantName": $scope.modalPopup.restaurantName.trim(),
                    "username": $scope.modalPopup.email.trim(),
                    "phone": $scope.modalPopup.phone.trim(),
                    "address": $scope.modalPopup.address.trim(),
                    "zip": $scope.modalPopup.zip.trim(),
                    "password": $scope.modalPopup.password.trim(),
                    "enabled": "true",
                    "authorities": [
                        {"authority": "ROLE_ADMIN"} //ROLE_USER, ROLE_ADMIN, ROLE_STUFF,ROLE_CUSTOMER
                    ]};
                console.log(registrationData);
                AuthService.registration(registrationData).then(function() {
                    var registered = $cookies.get('registered');
                    if(registered === 'true') {
                        console.log('registration was successful');
                        $scope.currentPage = 'registeredMessage';
                        var closeRegisteredWindow = function() {
                            $cookies.remove('registered');
                            $uibModalInstance.close();
                            $scope.clearModal();
                        };
                        $timeout(closeRegisteredWindow, 5000);
                    }else {
                        console.log('some error');
                    }
                });
                //console.log(form);
            }else if($scope.currentPage === 'updatePassword') {
                console.log($scope.modalPopup);
            }else if($scope.currentPage === 'addingMenu') {
                console.log('addingMenu');
                var restaurantId = $cookies.get('restaurant_Id');
                console.log(restaurantId);
                if(restaurantId) {
                    //console.log(restaurantId);
                    //console.log($scope.modalPopup.newMenuName);
                    $scope.menuDataPromisePost = httpPostQuery.postData('http://harristest.com.mocha6001.mochahost.com/foodme/restaurant/' + restaurantId + '/menu', {name: $scope.modalPopup.newMenuName});
                    $scope.menuDataPromisePost.then(function(value) {
                        console.log('object is send, reply is:');
                        var menuId = $cookies.get('menu_Id');
                        $scope.menuDataPromiseGet = httpGetQuery.getData('http://harristest.com.mocha6001.mochahost.com/foodme/menu/' + menuId);
                        $scope.menuDataPromiseGet.then(function(value) {
                            console.log('object is taken, reply is:');
                            console.log(value.name);
                            var currentMenu = {
                                currentMenuName: value.name,
                                currentMenuId: value.id
                            };
                            $cookies.put('current_menu_Id', value.id);
                            $uibModalInstance.close(currentMenu);
                        });
                    });
                }

                    //menuList = $cookies.get('menu_list');
                /*
                if(!menuList) {
                    var menuArr = [];
                    menuArr.push(menuId);
                    $cookies.put('menu_list', {menuArr: menuArr});
                }*/
                //$uibModalInstance.close();
                $scope.clearModal();
            }
            form.$setPristine(true);

        }
    };
/**---------------------------------------------FACEBOOK-SIGN-IN-FUNCTION---------------------------------------------*/
    $scope.fbLogin = function () {
        Facebook.login(function(response) {
            console.log(response);
            //console.log(response.authResponse.accessToken);
            if (response.status === 'connected') {
                console.log('yes');
                $cookies.put('access_token', response.authResponse.accessToken); // put received access_token in cookie

                Facebook.api('/me?fields=email,name', function(myResponse) {
                    if(myResponse) {
                        console.log(myResponse.email);
                        $scope.user = myResponse; // receive the user data from Facebook
                        console.log('Good to see you, ' + myResponse.name + '.');

                        var result = {
                            logged: true,
                            loggedUser: myResponse.name
                        };
                        $cookies.put('logged_user', result.loggedUser);
                        $cookies.put("validate", 'true');
                        $uibModalInstance.close(result);
                        $scope.clearModal();
                        $rootScope.errorMessage = false;
                        $scope.changeModal();
                    }
                });
            } else {
                console.log('no');
            }
        });
    };
/**--------------------------------------------WHOLE-FORM-CANCEL-FUNCTION---------------------------------------------*/
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
        $scope.clearModal();
        $scope.changeModal();
        $rootScope.errorMessage = false;
    };


}]);