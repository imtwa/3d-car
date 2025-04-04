package com.lihua.car.service;

import com.lihua.car.entity.CarImage;

import java.util.List;

/**
 * <p>
 * 汽车图片表 服务接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
public interface CarImageService {

    /**
     * 查询图片列表
     *
     * @param image 图片查询条件
     * @return 图片列表
     */
    List<CarImage> selectImageList(CarImage image);

    /**
     * 根据ID查询图片
     *
     * @param id 图片ID
     * @return 图片信息
     */
    CarImage selectImageById(Long id);
    
    /**
     * 根据车型ID查询图片列表
     *
     * @param modelId 车型ID
     * @return 图片列表
     */
    List<CarImage> selectImagesByModelId(Long modelId);
    
    /**
     * 根据车型ID和图片类型查询图片列表
     *
     * @param modelId 车型ID
     * @param imageType 图片类型（0首图 1详情图）
     * @return 图片列表
     */
    List<CarImage> selectImagesByModelIdAndType(Long modelId, String imageType);

    /**
     * 新增图片
     *
     * @param image 图片信息
     * @return 结果
     */
    int insertImage(CarImage image);

    /**
     * 修改图片
     *
     * @param image 图片信息
     * @return 结果
     */
    int updateImage(CarImage image);

    /**
     * 删除图片
     *
     * @param id 图片ID
     * @return 结果
     */
    int deleteImageById(Long id);

    /**
     * 批量删除图片
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteImageByIds(Long[] ids);
    
    /**
     * 根据车型ID删除图片
     *
     * @param modelId 车型ID
     * @return 结果
     */
    int deleteImageByModelId(Long modelId);
}