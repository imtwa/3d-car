package com.lihua.car.controller;

import com.lihua.car.dto.CarModelDTO;
import com.lihua.car.entity.CarImage;
import com.lihua.car.entity.CarModel;
import com.lihua.car.service.CarImageService;
import com.lihua.car.service.CarModelService;
import com.lihua.model.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/list")
    public String selectModelList(CarModel model) {
        return success(carModelService.selectModelList(model));
    }

    /**
     * 新增车型
     */
    @PostMapping("/add")
    public String insertModel(@RequestBody CarModel model) {
        return success(carModelService.insertModel(model));
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