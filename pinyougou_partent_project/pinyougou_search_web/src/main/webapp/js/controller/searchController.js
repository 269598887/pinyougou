app.controller('searchController', function ($scope, searchService) {
    $scope.searchMap = {
        'keywords': '', 'category': '', 'brand': '', 'spec':{}, 'price': '','pageNo': 1, 'pageSize': 10
    ,'sortField':'','sort':''};
    //搜索
    $scope.search = function () {
        if ($scope.searchMap == null) {
            $scope.searchMap = {"categoryList": [categoryList], "rows": [rows]};
            searchService.search($scope.searchMap).success(function (response) {
                $scope.resultMap = response;
            })
        } else {
            searchService.search($scope.searchMap).success(function (response) {
                $scope.resultMap = response;
                $scope.resultMap.totalPages;
            })
        }
        buildPageLable()//分页方法调用
    };
    $scope.addSearchItem = function (key, value) {
        //如果点击的是分类或者品牌
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value
        } else {
            $scope.searchMap.spec[key] == value;
        }
        $scope.search();//执行搜索
    }
    //移除符合搜索条件
    $scope.removeSeachItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果是分类或品牌价格
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search()//执行搜索
    };
    //构建分页标签
    buildPageLable = function () {
        $scope.pageLable = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后页码
        var firstPage = 1;//得到开始页码
        var lastPage = maxPageNo;//截至页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if ($scope.resultMap.totalPages > 5) {//如果总页数大于5页，显示部分页码
            if ($scope.searchMap.pageNo <= 3) {//如果当前页小于等于3
                firstPage=1;
                lastPage = 5;//前五页
                $scope.firstDot = false;//前面没点
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {//如果当前页大于等于最大页码2
                firstPage = maxPageNo - 4;//后5页
                $scope.lastDot = false;//后面没点
            } else {//显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.lastDot = false;//后面没点
            $scope.lastDot = false;//后面没点
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLable.push(i)
        }
    }
    //根据页码查询寻
    $scope.queryByPage = function (pageNo) {
        //页面验证
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search()
    }
    //判断当前页是否为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false
        }
    }
    //判断当前页是否为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    }
    //设置排序规则
    $scope.sortSearch=function (sortField, sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }
    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0) {
                //如果包含
                return true;
            }
        }
        return false;
    }
    ///加载查询字符串
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search()
    }

});