app.directive('validConfirmPassword', function () {
    return {
        restrict: 'A', // don't compulsory
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function (viewValue, $scope) {
                //var noMatch = viewValue != scope.Form.password.$viewValue;

                var noMatch;
                if(viewValue !== scope.Form.userPassword.$viewValue && viewValue !== '') {
                    noMatch = true;
                }

                ctrl.$setValidity('noMatch', !noMatch);
                return viewValue; //important - to return viewValue an the end!!!
            })
        }
    }
});
