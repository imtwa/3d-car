package com.lihua.car.controller;

import com.lihua.car.dto.CarBrandDTO;
import com.lihua.car.entity.CarBrand;
import com.lihua.car.service.CarBrandService;
import com.lihua.model.validation.MaxPageSizeLimit;
import com.lihua.model.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 汽车品牌表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@RestController
@RequestMapping("/car/brand")
public class CarBrandController extends BaseController {

    @Autowired
    private CarBrandService carBrandService;

    @PostMapping("page")
    public String queryPage(@RequestBody @Validated(MaxPageSizeLimit.class) CarBrandDTO dto) {
        return success(carBrandService.queryPage(dto));
    }

    /**
     * 获取品牌列表
     */
    @GetMapping("/list")
    public String selectBrandList(CarBrand brand) {
        return success(carBrandService.selectBrandList(brand));
    }

    /**
     * 根据ID获取品牌详情
     */
    @GetMapping("/{id}")
    public String selectBrandById(@PathVariable("id") Long id) {
        return success(carBrandService.selectBrandById(id));
    }
    
    /**
     * 根据ID获取品牌详情（包含车型列表）
     */
    @GetMapping("/detail/{id}")
    public String selectBrandDetailById(@PathVariable("id") Long id) {
        return success(carBrandService.selectBrandDetailById(id));
    }
    

    /**
     * 新增品牌
     */
    @PostMapping("/add")
    public String insertBrand(@RequestBody CarBrand brand) {
        return success(carBrandService.insertBrand(brand));
    }

    /**
     * 修改品牌
     */
    @PostMapping("/update")
    public String updateBrand(@RequestBody CarBrand brand) {
        return success(carBrandService.updateBrand(brand));
    }

    /**
     * 删除品牌
     */
    @DeleteMapping("/delete/{id}")
    public String deleteBrandById(@PathVariable("id") Long id) {
        return success(carBrandService.deleteBrandById(id));
    }

    /**
     * 批量删除品牌
     */
    @DeleteMapping("/delete")
    public String deleteBrandByIds(@RequestBody Long[] ids) {
        return success(carBrandService.deleteBrandByIds(ids));
    }
}