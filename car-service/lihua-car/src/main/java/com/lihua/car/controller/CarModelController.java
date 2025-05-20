package com.lihua.car.controller;


import com.lihua.annotation.Log;
import com.lihua.car.dto.CarModelDTO;
import com.lihua.car.entity.CarModel;
import com.lihua.car.service.CarModelService;
import com.lihua.enums.LogTypeEnum;

import com.lihua.model.validation.MaxPageSizeLimit;
import com.lihua.model.web.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



/**
 * <p>
 * 车型表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@RestController
@RequestMapping("/car/model")
public class CarModelController extends BaseController {

    @Autowired
    private CarModelService carModelService;

    /**
     * 获取车型列表
     */
    @PostMapping("/page")
    public String selectModelList(@RequestBody @Validated(MaxPageSizeLimit.class) CarModelDTO dto) {
        return success(carModelService.queryPage(dto));
    }

    /**
     * ID查询车型
     */
    @GetMapping("/selectId/{id}")
    public String selectModelDetailById(@PathVariable("id") Long id) {
        return success(carModelService.selectModelDetailById(id));
    }


    /**
     * 新增车型
     */
    @PostMapping("add")
    @Log(description = "保存汽车数据", type = LogTypeEnum.SAVE)
    public String saveAdd(@RequestBody @Validated() CarModel carModel) {
        // 校验 query 是否为json参数
        return success(carModelService.insertModel(carModel));
    }

    /**
     * 修改车型
     */
    @PostMapping("/update")
    public String updateModel(@RequestBody CarModel model) {
        return success(carModelService.updateModel(model));
    }

    /**
     * 删除车型
     */
    @DeleteMapping("/delete/{id}")
    public String deleteModelById(@PathVariable("id") Long id) {
        return success(carModelService.deleteModelById(id));
    }
    
    /**
     * 删除车型 (适配新的URL格式)
     */
    @DeleteMapping("/{id}")
    public String deleteModel(@PathVariable("id") Long id) {
        return success(carModelService.deleteModelById(id));
    }

    /**
     * 变更车型状态
     */
    @PostMapping("/changeStatus")
    public String changeStatus(@RequestBody CarModel model) {
        return success(carModelService.changeStatus(model));
    }
}