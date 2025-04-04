package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.entity.CarImage;
import com.lihua.car.mapper.CarImageMapper;
import com.lihua.car.service.CarImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 汽车图片表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Service
public class CarImageServiceImpl extends ServiceImpl<CarImageMapper, CarImage> implements CarImageService {

    @Autowired
    private CarImageMapper imageMapper;

    /**
     * 查询图片列表
     *
     * @param image 图片查询条件
     * @return 图片列表
     */
    @Override
    public List<CarImage> selectImageList(CarImage image) {
        return imageMapper.selectImageList(image);
    }

    /**
     * 根据ID查询图片
     *
     * @param id 图片ID
     * @return 图片信息
     */
    @Override
    public CarImage selectImageById(Long id) {
        return imageMapper.selectImageById(id);
    }
    
    /**
     * 根据车型ID查询图片列表
     *
     * @param modelId 车型ID
     * @return 图片列表
     */
    @Override
    public List<CarImage> selectImagesByModelId(Long modelId) {
        return imageMapper.selectImagesByModelId(modelId);
    }
    
    /**
     * 根据车型ID和图片类型查询图片列表
     *
     * @param modelId 车型ID
     * @param imageType 图片类型（0首图 1详情图）
     * @return 图片列表
     */
    @Override
    public List<CarImage> selectImagesByModelIdAndType(Long modelId, String imageType) {
        return imageMapper.selectImagesByModelIdAndType(modelId, imageType);
    }

    /**
     * 新增图片
     *
     * @param image 图片信息
     * @return 结果
     */
    @Override
    public int insertImage(CarImage image) {
        image.setCreateTime(LocalDateTime.now());
        image.setUpdateTime(LocalDateTime.now());
        return imageMapper.insertImage(image);
    }

    /**
     * 修改图片
     *
     * @param image 图片信息
     * @return 结果
     */
    @Override
    public int updateImage(CarImage image) {
        image.setUpdateTime(LocalDateTime.now());
        return imageMapper.updateImage(image);
    }

    /**
     * 删除图片
     *
     * @param id 图片ID
     * @return 结果
     */
    @Override
    public int deleteImageById(Long id) {
        return imageMapper.deleteImageById(id);
    }

    /**
     * 批量删除图片
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteImageByIds(Long[] ids) {
        return imageMapper.deleteImageByIds(ids);
    }
    
    /**
     * 根据车型ID删除图片
     *
     * @param modelId 车型ID
     * @return 结果
     */
    @Override
    public int deleteImageByModelId(Long modelId) {
        return imageMapper.deleteImageByModelId(modelId);
    }
}