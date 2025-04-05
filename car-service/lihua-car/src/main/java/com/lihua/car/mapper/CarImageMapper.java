package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 汽车图片表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Mapper
public interface CarImageMapper extends BaseMapper<CarImage> {
    
    void insertBatch(@Param("list") List<CarImage> imageList);

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
    CarImage selectImageById(@Param("id") Long id);

    /**
     * 根据车型ID查询图片列表
     *
     * @param modelId 车型ID
     * @return 图片列表
     */
    List<CarImage> selectImagesByModelId(@Param("modelId") Long modelId);

    /**
     * 根据车型ID和图片类型查询图片列表
     *
     * @param modelId 车型ID
     * @param imageType 图片类型（0首图 1详情图）
     * @return 图片列表
     */
    List<CarImage> selectImagesByModelIdAndType(@Param("modelId") Long modelId, @Param("imageType") String imageType);

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
    int deleteImageByModelId(@Param("modelId") Long modelId);
}