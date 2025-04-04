package com.lihua.car.controller;

import com.lihua.car.entity.CarImage;
import com.lihua.car.service.CarImageService;
import com.lihua.model.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 汽车图片表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@RestController
@RequestMapping("/car/image")
public class CarImageController extends BaseController {

    @Autowired
    private CarImageService carImageService;

    /**
     * 获取图片列表
     */
    @GetMapping("/list")
    public String selectImageList(CarImage image) {
        return success(carImageService.selectImageList(image));
    }

    /**
     * 根据ID获取图片详情
     */
    @GetMapping("/{id}")
    public String selectImageById(@PathVariable("id") Long id) {
        return success(carImageService.selectImageById(id));
    }
    
    /**
     * 根据车型ID获取图片列表
     */
    @GetMapping("/model/{modelId}")
    public String selectImagesByModelId(@PathVariable("modelId") Long modelId) {
        return success(carImageService.selectImagesByModelId(modelId));
    }
    
    /**
     * 根据车型ID和图片类型获取图片列表
     */
    @GetMapping("/model/{modelId}/type/{imageType}")
    public String selectImagesByModelIdAndType(
            @PathVariable("modelId") Long modelId,
            @PathVariable("imageType") String imageType) {
        return success(carImageService.selectImagesByModelIdAndType(modelId, imageType));
    }

    /**
     * 新增图片
     */
    @PostMapping("/add")
    public String insertImage(@RequestBody CarImage image) {
        return success(carImageService.insertImage(image));
    }

    /**
     * 修改图片
     */
    @PostMapping("/update")
    public String updateImage(@RequestBody CarImage image) {
        return success(carImageService.updateImage(image));
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/delete/{id}")
    public String deleteImageById(@PathVariable("id") Long id) {
        return success(carImageService.deleteImageById(id));
    }

    /**
     * 批量删除图片
     */
    @DeleteMapping("/delete")
    public String deleteImageByIds(@RequestBody Long[] ids) {
        return success(carImageService.deleteImageByIds(ids));
    }
}