app.controller('loginController', function ($scope, loginService) {
    $scope.showLoginName = function () {
        loginService.loginName().success(function (response) {
        $scope.name=response.loginName;

        })
    }
})