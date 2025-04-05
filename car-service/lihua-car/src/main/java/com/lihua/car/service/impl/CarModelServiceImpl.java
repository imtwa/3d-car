package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.dto.CarModelDTO;
import com.lihua.car.entity.CarBrand;
import com.lihua.car.entity.CarImage;
import com.lihua.car.entity.CarModel;
import com.lihua.car.mapper.CarBrandMapper;
import com.lihua.car.mapper.CarImageMapper;
import com.lihua.car.mapper.CarModelMapper;
import com.lihua.car.service.CarModelService;
import com.lihua.config.LihuaConfig;
import com.lihua.entity.system.SysAttachment;

import com.lihua.mapper.system.SysAttachmentMapper;
import com.lihua.service.system.attachment.impl.SysAttachmentServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 汽车车型表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Service
public class CarModelServiceImpl extends ServiceImpl<CarModelMapper, CarModel> implements CarModelService {

    @Autowired
    private CarModelMapper modelMapper;

    @Autowired
    private CarBrandMapper brandMapper;

    @Autowired
    private CarImageMapper imageMapper;

    @Autowired
    private SysAttachmentServiceImpl attachmentService;

    /**
     * 查询车型列表
     *
     * @param dto 车型查询条件
     * @return 车型列表
     */
    @Override
    public IPage<? extends CarModel> queryPage(CarModelDTO dto) {
        IPage<CarModel> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarModel> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        // 添加品牌ID查询条件
        if (dto.getBrandId() != null) {
            queryWrapper.eq(CarModel::getBrandId, dto.getBrandId());
        }
        
        // 添加状态查询条件
        if (StringUtils.hasText(dto.getStatus())) {
            queryWrapper.eq(CarModel::getStatus, dto.getStatus());
        }
        
        // 添加删除标志条件，只查询未删除的记录
        queryWrapper.eq(CarModel::getDelFlag, "0");
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(CarModel::getCreateTime);
        
        // 查询车型列表
        IPage<CarModel> resultPage = this.page(page, queryWrapper);
        
        // 为每个车型关联图片信息和品牌名称
        if (resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
            for (CarModel model : resultPage.getRecords()) {
                // 查询品牌名称
                if (model.getBrandId() != null) {
                    CarBrand brand = brandMapper.selectById(model.getBrandId());
                    if (brand != null) {
                        model.setBrandName(brand.getName());
                    }
                }
                
                // 查询首图（类型为0的图片）
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> mainImageWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                mainImageWrapper.eq(CarImage::getModelId, model.getId())
                               .eq(CarImage::getImageType, "0")
                               .eq(CarImage::getDelFlag, "0");
                List<CarImage> mainImages = imageMapper.selectList(mainImageWrapper);
                if (!mainImages.isEmpty()) {
                    CarImage mainImage = mainImages.get(0);
                    // 设置首图URL
                    model.setCoverImage(mainImage.getImageUrl());
                    // 设置首图ID
                    model.setCoverImageId(String.valueOf(mainImage.getAttachmentId()));
                }
                
                // 查询详情图（类型为1的图片）
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> detailImageWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                detailImageWrapper.eq(CarImage::getModelId, model.getId())
                                 .eq(CarImage::getImageType, "1")
                                 .eq(CarImage::getDelFlag, "0");
                List<CarImage> detailImages = imageMapper.selectList(detailImageWrapper);
                if (!detailImages.isEmpty()) {
                    // 设置详情图URL列表
                    List<String> imageUrls = detailImages.stream()
                            .map(CarImage::getImageUrl)
                            .collect(Collectors.toList());
                    model.setImages(imageUrls);
                    
                    // 设置详情图ID列表
                    List<String> imageIds = detailImages.stream()
                            .map(image -> String.valueOf(image.getAttachmentId()))
                            .collect(Collectors.toList());
                    model.setImageIds(imageIds);
                }
            }
        }
        
        return resultPage;
    }

    /**
     * 新增车型
     *
     * @param model 车型信息
     * @return 结果
     */
    @Override
    public boolean insertModel(CarModel model) {
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());

        // 处理模型文件附件ID和URL
        if (model.getModelAttachmentId() != null) {
            SysAttachment modelAttachment = attachmentService.getById(model.getModelAttachmentId());
            if (modelAttachment != null) {
                // 设置模型文件附件ID
                model.setModelAttachmentId(Long.valueOf(modelAttachment.getId()));
                // 设置模型URL
                model.setModelUrl(modelAttachment.getPath());
            }
        }

        boolean result = super.save(model);

        if (result && CollectionUtils.isNotEmpty(model.getImages())) {
            batchInsertCarImages(model);
        }

        return result;
    }

    private void batchInsertCarImages(CarModel model) {
        // 创建图片列表
        List<CarImage> imageList = new ArrayList<>();
        
        // 处理封面图片，如果存在
        if (StringUtils.hasText(model.getCoverImage())) {
            SysAttachment coverImageAttachment = attachmentService.getById(model.getCoverImage());
            if (coverImageAttachment != null) {
                CarImage coverCarImage = new CarImage()
                        .setModelId(model.getId())
                        .setAttachmentId(Long.valueOf(coverImageAttachment.getId()))
                        .setImageUrl(coverImageAttachment.getPath())
                        .setImageType("0");
                imageList.add(coverCarImage); // 添加封面图片
            }
        }

        // 添加其他图片，如果存在
        if (CollectionUtils.isNotEmpty(model.getImages())) {
            model.getImages().stream()
                    .filter(StringUtils::hasText)
                    .map(attachmentId -> {
                        SysAttachment imageAttachment = attachmentService.getById(attachmentId);
                        if (imageAttachment != null) {
                            return new CarImage()
                                    .setModelId(model.getId())
                                    .setAttachmentId(Long.valueOf(imageAttachment.getId()))
                                    .setImageUrl(imageAttachment.getPath())
                                    .setImageType("1");
                        }
                        return null;
                    })
                    .filter(carImage -> carImage != null)
                    .forEach(imageList::add);
        }

        // 批量插入图片
        if (!imageList.isEmpty()) {
            for (CarImage carImage : imageList) {
                imageMapper.insert(carImage);
            }
        }
    }

    /**
     * 根据ID查询车型
     *
     * @param id 车型ID
     * @return 车型信息
     */
    @Override
    public CarModel selectModelById(Long id) {
        return this.getById(id);
    }

    /**
     * 根据ID查询车型详情（包含图片列表）
     *
     * @param id 车型ID
     * @return 车型详情
     */
    @Override
    public CarModelDTO selectModelDetailById(Long id) {
        // 查询车型信息
        CarModel model = this.getById(id);
        if (model == null) {
            return null;
        }

        // 转换为DTO
        CarModelDTO modelDTO = new CarModelDTO();
        BeanUtils.copyProperties(model, modelDTO);

        // 查询品牌信息
        if (model.getBrandId() != null) {
            CarBrand brand = brandMapper.selectById(model.getBrandId());
            if (brand != null) {
                model.setBrandName(brand.getName());
                modelDTO.setBrand(brand);
            }
        }

        // 查询首图（类型为0的图片）
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> mainImageWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        mainImageWrapper.eq(CarImage::getModelId, id)
                       .eq(CarImage::getImageType, "0")
                       .eq(CarImage::getDelFlag, "0");
        List<CarImage> mainImages = imageMapper.selectList(mainImageWrapper);
        modelDTO.setMainImages(mainImages);
        
        if (!mainImages.isEmpty()) {
            CarImage mainImage = mainImages.get(0);
            // 设置首图URL
            model.setCoverImage(mainImage.getImageUrl());
            // 设置首图ID
            model.setCoverImageId(String.valueOf(mainImage.getAttachmentId()));
        }

        // 查询详情图（类型为1的图片）
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> detailImageWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        detailImageWrapper.eq(CarImage::getModelId, id)
                         .eq(CarImage::getImageType, "1")
                         .eq(CarImage::getDelFlag, "0");
        List<CarImage> detailImages = imageMapper.selectList(detailImageWrapper);
        modelDTO.setDetailImages(detailImages);
        
        if (!detailImages.isEmpty()) {
            // 设置详情图URL列表
            List<String> imageUrls = detailImages.stream()
                    .map(CarImage::getImageUrl)
                    .collect(Collectors.toList());
            model.setImages(imageUrls);
            
            // 设置详情图ID列表
            List<String> imageIds = detailImages.stream()
                    .map(image -> String.valueOf(image.getAttachmentId()))
                    .collect(Collectors.toList());
            model.setImageIds(imageIds);
        }
        
        return modelDTO;
    }

    /**
     * 根据品牌ID查询车型列表
     *
     * @param brandId 品牌ID
     * @return 车型列表
     */
    @Override
    public List<CarModel> selectModelsByBrandId(Long brandId) {
        return modelMapper.selectModelsByBrandId(brandId);
    }

    /**
     * 修改车型
     *
     * @param model 车型信息
     * @return 结果
     */
    @Override
    public int updateModel(CarModel model) {
        model.setUpdateTime(LocalDateTime.now());
        return modelMapper.updateModel(model);
    }

    /**
     * 删除车型
     *
     * @param id 车型ID
     * @return 结果
     */
    @Override
    public int deleteModelById(Long id) {
        // 先删除车型关联的图片
        imageMapper.deleteImageByModelId(id);
        // 再删除车型
        return modelMapper.deleteModelById(id);
    }

    /**
     * 批量删除车型
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteModelByIds(Long[] ids) {
        // 先删除车型关联的图片
        for (Long id : ids) {
            imageMapper.deleteImageByModelId(id);
        }
        // 再删除车型
        return modelMapper.deleteModelByIds(ids);
    }

    /**
     * 变更车型状态
     *
     * @param carModelDTO 车型信息
     * @return 结果
     */
    @Override
    public int changeStatus(CarModelDTO carModelDTO) {
        // 使用BaseMapper的updateById方法更新状态
        CarModel model = new CarModel();
        model.setId(Long.valueOf(carModelDTO.getId()));
        model.setStatus(carModelDTO.getStatus());
        model.setUpdateTime(LocalDateTime.now());
        
        // 使用MyBatis-Plus提供的update方法
        boolean result = this.updateById(model);
        return result ? 1 : 0;
    }
}