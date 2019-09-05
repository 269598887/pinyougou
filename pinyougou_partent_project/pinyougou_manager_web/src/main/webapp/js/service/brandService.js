//服务分层
app.service('brandService', function ($http) {
    //查询品牌列表集合
    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do")
    };
    this.findByPage = function (currPage, pageSize) {
        return $http.get('../brand/findByPage.do?currPage=' + currPage + '&pageSize=' + pageSize);
    };
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    };
    this.add = function (entity) {
        return $http.post('../brand/add.do', entity);
    };
    this.updateBrand = function (entity) {
        return $http.post('../brand/updateBrand.do', entity);
    };
    this.deleteBrand = function (ids) {
        return $http.post('../brand/deleteBrand.do?ids=' + ids)
    };
    this.search = function (currPage, pageSize, searchEntity) {
        return $http.post('../brand/findBrandByCondition.do?currPage=' + currPage + '&pageSize=' + pageSize, searchEntity)
    };
});