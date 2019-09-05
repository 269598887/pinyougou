//控制层
app.controller('seckillGoodsController',function ($scope, seckillGoodsService) {
    //读取列表数据绑定到表单中
    $scope.findList=function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list=response;
            }
        )
    }

    $scope.findOne=function () {
        seckillGoodsService.findOne($location().search(['id'])).success(
            function (response) {
                $scope.entity=response;
            }
        )
    };

    $scope.second=10;
    time=$interval(function () {
        if($scope.second>0){
            $scope.second=$scope.second-1;
        }else {
            $interval.cancel(time);
            alert("秒杀服务已结束")
        }
    },1000);

});
