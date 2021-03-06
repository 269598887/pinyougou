//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, itemCatService, typeTemplateService, $location) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };
    //定义组合实体存放三个实体类
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};
//读取一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findItemCatByParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        })
    };
    //读取二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findItemCatByParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
        })

    });
    //读取二级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findItemCatByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        })
    });
    //监控三级分类，获取模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        })
    });
    //监控模板id，获取品牌
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        typeTemplateService.findOne(newValue).success(function (response) {
            $scope.typeTemplate = response;//获取类型模板
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//转换josn数据
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems)//扩展属性
        });
        //根据模板id获取规格列表
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList = response;
        })
    });

    //根据模板id获取规格列表
    $scope.updateSpecAttribute = function ($event, specName, optionName) {
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', specName);
        if (object != null) {
            if ($event.target.checked) {
                object.attributeValue.push(optionName)
            } else {
                object.attributeValue.splice(object.attributeValue.indexOf(optionName), 1);
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1)
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": specName, "attributeValue": [optionName]})
        }
    };
//创建sku列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];
        //初始
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn(
                $scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    };
    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加数据
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表,将字符产转化为json数据
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //将扩展属性转化为Json数据
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //读取规格信息
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //sku列表数据转换json
                for (var i = 0; $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec)
                }
            }
        );
    };
    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttrbuteValue = function (name, value) {
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, "attributeName", name);
        if (object == null) {
            return false;
        } else {
            return object.attributeValue.indexOf(value) >= 0;
        }
    }
    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
                //定义状态数组
                $scope.status = ["未审核", "已审核", "审核通过", "关闭"];
            }
        );
    };

    $scope.itemCat1List = [];
    //查询商品分类列表
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; response.length > i; i++) {
                $scope.itemCat1List[response[i].id] = response[i].name;
            }
        })
    };
    //商品状态审核
    $scope.updateStatus = function (status) {
        goodsService.updateStatus($scope.selectIds, status).success(function (response) {
            if (response.success) {
            $scope.reloadList();//刷新列表
            $scope.selectIds=[];//将数组清空
            }else {
                alert(response.message)
            }
        })
    }
});
