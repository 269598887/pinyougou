//服务层
app.service('seckillGoodsService',function ($http) {
    this.findList=function () {
        return $http.get('seckillGoods/findList.do');
    }

    this.findOne=function (id) {
        return $htpp.get('seckillGoods/findOneFromRedis.do?id='+id);
    }
})