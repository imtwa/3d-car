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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
@Service
public class CarModelServiceImpl extends ServiceImpl<CarModelMapper, CarModel> implements CarModelService {

    @Autowired
    private CarModelMapper modelMapper;

    @Autowired
    private CarBrandMapper brandMapper;

    @Autowired
    private CarImageMapper imageMapper;

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

        boolean result = super.save(model);

        if (result && CollectionUtils.isNotEmpty(model.getImageIds())) {
            batchInsertCarImages(model);
        }

        return result;
    }

    private void batchInsertCarImages(CarModel model) {
        // 创建图片列表
        List<CarImage> imageList = new ArrayList<>();

        // 处理封面图片，如果存在
        if (StringUtils.hasText(model.getCoverImageId())) {
            CarImage coverCarImage = new CarImage()
                    .setModelId(model.getId())
                    .setAttachmentId(Long.valueOf(model.getCoverImageId()))
                    .setImageType("0");
            imageList.add(coverCarImage); // 添加封面图片
        }

        // 添加其他图片，如果存在
        if (CollectionUtils.isNotEmpty(model.getImageIds())) {
            model.getImageIds().stream()
                    .filter(StringUtils::hasText)
                    .map(attachmentId -> {
                            return new CarImage()
                                    .setModelId(model.getId())
                                    .setAttachmentId(Long.valueOf(attachmentId))
                                    .setImageType("1");
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
    public CarModel selectModelDetailById(Long id) {
        // 查询车型信息
        CarModel model = this.getById(id);
        if (model == null) {
            return null;
        }
        // 查询首图（类型为0的图片）
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> mainImageWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        mainImageWrapper.eq(CarImage::getModelId, model.getId())
                .eq(CarImage::getImageType, "0")
                .eq(CarImage::getDelFlag, "0");
        List<CarImage> mainImages = imageMapper.selectList(mainImageWrapper);
        if (!mainImages.isEmpty()) {
            CarImage mainImage = mainImages.get(0);
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
            // 设置详情图ID列表
            List<String> imageIds = detailImages.stream()
                    .map(image -> String.valueOf(image.getAttachmentId()))
                    .collect(Collectors.toList());
            model.setImageIds(imageIds);
        }

        return model;
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
    @Transactional(rollbackFor = Exception.class)
    public int updateModel(CarModel model) {
        model.setUpdateTime(LocalDateTime.now());

        // 使用MyBatis-Plus提供的updateById方法
        boolean result = this.updateById(model);

        // 处理图片更新
        if (result) {
            // 处理封面图片（如果有）
            if (model.getCoverImageId() != null) {
                // 查询是否已存在该附件ID的封面图片
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> coverImageWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                coverImageWrapper.eq(CarImage::getModelId, model.getId())
                        .eq(CarImage::getImageType, "0")
                        .eq(CarImage::getAttachmentId, model.getCoverImageId())
                        .eq(CarImage::getDelFlag, "0");
                List<CarImage> existingCoverImages = imageMapper.selectList(coverImageWrapper);

                // 先删除此车型的其他所有封面图片（保留当前使用的）
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> deleteCoverWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                deleteCoverWrapper.eq(CarImage::getModelId, model.getId())
                        .eq(CarImage::getImageType, "0")
                        .ne(CarImage::getAttachmentId, model.getCoverImageId())
                        .eq(CarImage::getDelFlag, "0");
                imageMapper.delete(deleteCoverWrapper);

                // 如果不存在当前封面图片，则新增
                if (existingCoverImages.isEmpty()) {
                        CarImage coverImage = new CarImage()
                                .setModelId(model.getId())
                                .setAttachmentId(Long.valueOf(model.getCoverImageId()))
                                .setImageType("0")  // 封面图类型为0
                                .setStatus("0")
                                .setDelFlag("0")
                                .setCreateTime(LocalDateTime.now())
                                .setUpdateTime(LocalDateTime.now());

                        imageMapper.insert(coverImage);
                }
            } else {
                // 如果没有提供封面图片，删除所有封面图片
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> deleteCoverWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                deleteCoverWrapper.eq(CarImage::getModelId, model.getId())
                        .eq(CarImage::getImageType, "0")
                        .eq(CarImage::getDelFlag, "0");
                imageMapper.delete(deleteCoverWrapper);
            }

            // 处理详情图片列表（如果有）
            if (CollectionUtils.isNotEmpty(model.getImageIds())) {
                // 查询现有的所有详情图片
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> detailImageWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                detailImageWrapper.eq(CarImage::getModelId, model.getId())
                        .eq(CarImage::getImageType, "1")
                        .eq(CarImage::getDelFlag, "0");
                List<CarImage> existingDetailImages = imageMapper.selectList(detailImageWrapper);

                // 获取已有图片的附件ID列表
                List<String> existingAttachmentIds = existingDetailImages.stream()
                        .map(image -> String.valueOf(image.getAttachmentId()))
                        .collect(Collectors.toList());

                // 获取需要保留的附件ID列表
                List<String> newAttachmentIds = model.getImageIds();

                // 找出需要删除的图片（在已有列表但不在新列表中）
                List<String> attachmentIdsToDelete = existingAttachmentIds.stream()
                        .filter(id -> !newAttachmentIds.contains(id))
                        .collect(Collectors.toList());

                // 删除不再需要的图片
                if (!attachmentIdsToDelete.isEmpty()) {
                    for (String attachmentId : attachmentIdsToDelete) {
                        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> deleteWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                        deleteWrapper.eq(CarImage::getModelId, model.getId())
                                .eq(CarImage::getImageType, "1")
                                .eq(CarImage::getAttachmentId, attachmentId)
                                .eq(CarImage::getDelFlag, "0");
                        imageMapper.delete(deleteWrapper);
                    }
                }

                // 找出需要添加的图片（在新列表但不在已有列表中）
                List<String> attachmentIdsToAdd = newAttachmentIds.stream()
                        .filter(id -> !existingAttachmentIds.contains(id))
                        .collect(Collectors.toList());

                // 添加新图片
                if (!attachmentIdsToAdd.isEmpty()) {
                    for (int i = 0; i < attachmentIdsToAdd.size(); i++) {
                        String attachmentId = attachmentIdsToAdd.get(i);

                            CarImage detailImage = new CarImage()
                                    .setModelId(model.getId())
                                    .setAttachmentId(Long.valueOf(attachmentId))
                                    .setImageType("1")  // 详情图类型为1
                                    .setSort(existingDetailImages.size() + i + 1)  // 设置排序号
                                    .setStatus("0")
                                    .setDelFlag("0")
                                    .setCreateTime(LocalDateTime.now())
                                    .setUpdateTime(LocalDateTime.now());

                            imageMapper.insert(detailImage);
                    }
                }
            } else {
                // 如果没有提供详情图片，删除所有详情图片
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarImage> deleteDetailWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                deleteDetailWrapper.eq(CarImage::getModelId, model.getId())
                        .eq(CarImage::getImageType, "1")
                        .eq(CarImage::getDelFlag, "0");
                imageMapper.delete(deleteDetailWrapper);
            }
        }

        return result ? 1 : 0;
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
    public int changeStatus(CarModel model) {
        model.setUpdateTime(LocalDateTime.now());

        // 使用MyBatis-Plus提供的update方法
        boolean result = this.updateById(model);
        return result ? 1 : 0;
    }
}