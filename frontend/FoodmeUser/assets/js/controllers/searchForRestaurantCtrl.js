app.controller('SearchForRestaurant', ['$scope', '$rootScope', '$cookies', '$http', '$window', '$document', function($scope, $rootScope, $cookies, $http, $window, $document) {
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
    $rootScope.errorMessage = false;

/**----------------------------------------------Authentication part--------------------------------------------------*/
/*
var isLoginPage = window.location.href.indexOf("login") != -1; //there is no login page
    if(isLoginPage){
        if($cookies.get("access_token")){
            //window.location.href = "index"; //don't need to redirect somewhere but to change $rootScope.validate
        }
    }else{
        if($cookies.get("access_token")){
            $http.defaults.headers.common.Authorization= 'Bearer ' + $cookies.get("access_token");
        }else{
            //window.location.href = "login";
        }
    }
*/
    if($cookies.get("access_token")){
        $http.defaults.headers.common.Authorization= 'Bearer ' + $cookies.get("access_token");
    }
/**-------------------------------------------SET-EMPTY-LOGIN-OBJECT-DATA---------------------------------------------*/
    $scope.setEmptyRegisterUserData = function () {
        $scope.userData = {
            firstName: '',
            lastName: '',
            email: '',
            password: '',
            confirmPassword: ''
        }
    };
    $scope.setEmptyRegisterUserData();
    $scope.setEmptyLoginUserData = function() {
        $scope.loginData = {
            loginEmail: '',
            loginPassword: ''
        }
    };
    $scope.setEmptyLoginUserData();
/**-------------------------------------------------ALIGN-SEARCH-BLOCK------------------------------------------------*/
    $scope.searchBlockAlign = function() {
        var blockHeight = $document.find('.search-block')[0].offsetHeight,
        marginTop = ($window.innerHeight - 40 - 50 - 27 - 156 - blockHeight) / 2;
        $scope.verticalAlign = {'margin-top': marginTop + 'px'}
    };
    $scope.searchBlockAlign(); //modal-dialog modal-lg

}]);