app.controller('contentController', function ($scope, contentService,$location) {
    $scope.contentList = [];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId] = response;
            $scope.contentList[categoryId].pic=JSON.parse($scope.contentList[categoryId].pic)
        })
    }
    $scope.search=function () {
        location.href="http://location:9104/search.html#?keywords="+$scope.keywords;
    }
});