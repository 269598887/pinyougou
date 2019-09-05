//分页
app.controller('brandController', function ($scope,$controller,brandService) {
$controller("baseController",{$scope:$scope})
    $scope.findByPage = function (currPage, pageSize) {
        brandService.findByPage(currPage, pageSize).success(function (data) {
            $scope.list = data.roes;//当前页数据赋值
            $scope.paginationConf.totalItems = data.total;//总记录数
        });
    };
    //修改
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (data) {
            $scope.entity = data;
        });
    };
    $scope.save = function () {
        var Object = null;
        if ($scope.entity.id != null) {
            Object = brandService.updateBrand($scope.entity);
        } else {
            Object = brandService.add($scope.entity);
        }
        Object.success(function (data) {
            if (data.success) {
                $scope.reloadList();
            } else {
                alert(data.message);
            }
        });
    };

    //删除
    $scope.deleteBrand = function () {
        brandService.deleteBrand($scope.selectIds).success(function (data) {
            if (data.success) {
                $scope.reloadList();
            } else {
                alert(data.message)
            }
        });
        $scope.selectIds = [];
    };
    //条件查询
    //定义搜索对象
    $scope.searchEntity = {};
    //条件查询
    $scope.search = function (currPage, pageSize) {
        brandService.search(currPage, pageSize, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;//当前页数据赋值
            $scope.paginationConf.totalItems = data.total;//总记录数
        })
    };
})