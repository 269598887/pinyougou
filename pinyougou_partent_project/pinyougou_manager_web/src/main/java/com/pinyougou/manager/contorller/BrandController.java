package com.pinyougou.manager.contorller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.messger.Result;
import com.pinyougou.page.PageResult;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    /**
     * 查找所有
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 分页查询
     *
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("/findByPage")
    public PageResult<TbBrand> findByPage(Integer currPage, Integer pageSize) {
        return brandService.findByPage(currPage, pageSize);
    }

    /**
     * 添加
     *
     * @param tbBrand
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand) {
        try {
            brandService.add(tbBrand);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "添加失败");
    }

    /**
     * id查询
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    /**
     * 品牌修改
     *
     * @param tbBrand
     * @return
     */
    @RequestMapping("/updateBrand")
    public Result updateBrand(@RequestBody TbBrand tbBrand) {
        try {
            brandService.updateBrand(tbBrand);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "修改失败");
    }

    /**
     * 品牌删除
     * @param ids
     * @return
     */
    @RequestMapping("/deleteBrand")
    public Result deleteBrand(Long[] ids) {
        try {
            brandService.deleteBrand(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "删除失败");
    }

    /**
     * 条件查询
     * @param tbBrand
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("/findBrandByCondition")
    public PageResult findBrandByCondition(@RequestBody TbBrand tbBrand,Integer currPage,Integer pageSize){
        PageResult brandByCondition = brandService.findBrandByCondition(tbBrand, currPage, pageSize);
        return brandByCondition;
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
    return brandService.selectOptionList();
    }
}
