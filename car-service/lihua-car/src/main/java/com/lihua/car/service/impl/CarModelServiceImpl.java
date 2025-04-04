package com.lihua.car.service.impl;

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
import com.lihua.utils.file.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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
    private LihuaConfig lihuaConfig;

    /**
     * 查询车型列表
     *
     * @param model 车型查询条件
     * @return 车型列表
     */
    @Override
    public List<CarModel> selectModelList(CarModel model) {
        return modelMapper.selectModelList(model);
    }

    /**
     * 根据ID查询车型
     *
     * @param id 车型ID
     * @return 车型信息
     */
    @Override
    public CarModel selectModelById(Long id) {
        return modelMapper.selectModelById(id);
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
        CarModel model = modelMapper.selectModelById(id);
        if (model == null) {
            return null;
        }
        
        // 转换为DTO
        CarModelDTO modelDTO = new CarModelDTO();
        BeanUtils.copyProperties(model, modelDTO);
        
        // 查询品牌信息
        CarBrand brand = brandMapper.selectBrandById(model.getBrandId());
        modelDTO.setBrand(brand);
        
        // 查询首图列表
        List<CarImage> mainImages = imageMapper.selectImagesByModelIdAndType(id, "0");
        modelDTO.setMainImages(mainImages);
        
        // 查询详情图列表
        List<CarImage> detailImages = imageMapper.selectImagesByModelIdAndType(id, "1");
        modelDTO.setDetailImages(detailImages);
        
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
     * 新增车型
     *
     * @param model 车型信息
     * @return 结果
     */
    @Override
    public int insertModel(CarModel model) {
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        return modelMapper.insertModel(model);
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
    public int changeStatus(CarModelDTO carModelDTO) {
        CarModel model = new CarModel();
        model.setId(carModelDTO.getId());
        model.setStatus(carModelDTO.getStatus());
        model.setUpdateTime(LocalDateTime.now());
        return modelMapper.updateModel(model);
    }
    
    /**
     * 上传车型3D模型文件
     *
     * @param file 模型文件
     * @return 模型文件URL
     */
    public String uploadModel(MultipartFile file) {
        // 上传文件并获取文件路径
        String modelUrl = FileUtils.upload(file);
        return modelUrl;
    }
}