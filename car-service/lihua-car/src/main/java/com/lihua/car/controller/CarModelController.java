package com.lihua.car.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lihua.annotation.Log;
import com.lihua.car.dto.CarBrandDTO;
import com.lihua.car.dto.CarModelDTO;
import com.lihua.car.entity.CarImage;
import com.lihua.car.entity.CarModel;
import com.lihua.car.service.CarImageService;
import com.lihua.car.service.CarModelService;
import com.lihua.enums.LogTypeEnum;
import com.lihua.enums.ResultCodeEnum;
import com.lihua.model.validation.MaxPageSizeLimit;
import com.lihua.model.web.BaseController;
import com.lihua.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * 变更车型状态
     */
    @PostMapping("/changeStatus")
    public String changeStatus(@RequestBody CarModelDTO carModelDTO) {
        return success(carModelService.changeStatus(carModelDTO));
    }
}