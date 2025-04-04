package com.lihua.car.service;

import com.lihua.car.dto.CarModelDTO;
import com.lihua.car.entity.CarModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 汽车车型表 服务接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
public interface CarModelService {

    /**
     * 查询车型列表
     *
     * @param model 车型查询条件
     * @return 车型列表
     */
    List<CarModel> selectModelList(CarModel model);

    /**
     * 根据ID查询车型
     *
     * @param id 车型ID
     * @return 车型信息
     */
    CarModel selectModelById(Long id);
    
    /**
     * 根据ID查询车型详情（包含图片列表）
     *
     * @param id 车型ID
     * @return 车型详情
     */
    CarModelDTO selectModelDetailById(Long id);
    
    /**
     * 根据品牌ID查询车型列表
     *
     * @param brandId 品牌ID
     * @return 车型列表
     */
    List<CarModel> selectModelsByBrandId(Long brandId);

    /**
     * 新增车型
     *
     * @param model 车型信息
     * @return 结果
     */
    int insertModel(CarModel model);

    /**
     * 修改车型
     *
     * @param model 车型信息
     * @return 结果
     */
    int updateModel(CarModel model);

    /**
     * 删除车型
     *
     * @param id 车型ID
     * @return 结果
     */
    int deleteModelById(Long id);

    /**
     * 批量删除车型
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteModelByIds(Long[] ids);
    
    /**
     * 变更车型状态
     *
     * @param carModelDTO 车型信息
     * @return 结果
     */
    int changeStatus(CarModelDTO carModelDTO);
    
    /**
     * 上传车型3D模型文件
     *
     * @param file 模型文件
     * @return 模型文件URL
     */
    String uploadModel(MultipartFile file);
}