//分页
app.controller("baseController",function ($scope) {
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [5,10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };
    //页面刷新
    $scope.reloadList = function () {

        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };
    //删除复选框id提取
    $scope.selectIds = [];
    $scope.updateIds = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var ids = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(ids, 1);
        }
    };
    //从集合中按照key查询对象
    $scope.searchObjectByKey=function (list, key, keyValue) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key]==keyValue){
                return list[i]
            }
        }
        return null;
    }
});