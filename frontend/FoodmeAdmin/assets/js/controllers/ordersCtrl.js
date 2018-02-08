app.controller('Orders', ['$scope', '$rootScope', '$cookies', function($scope, $rootScope, $cookies) {
    var validate = $cookies.get('validate');
    if(validate === 'true') {
        var loggedUser = $cookies.get('logged_user');
        if(loggedUser) {
            $rootScope.loggedUser = loggedUser;
        }
        $rootScope.validate = true;
    }else {
        $rootScope.validate = false;
    }
    $scope.loginPage = true;
    $rootScope.errorMessage = false;

    /**-------------------------------------------SET-EMPTY-LOGIN-OBJECT-DATA---------------------------------------------*/
    $scope.setEmptyRegisterRestaurantData = function () {
        $scope.userData = {
            firstName: '',
            lastName: '',
            restaurantName: '',
            address: '',
            email: '',
            zip: '',
            phone: '',
            password: '',
            confirmPassword: ''
        }
    };
    $scope.setEmptyRegisterRestaurantData();
}]);



