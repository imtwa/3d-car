package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.dto.CarBrandDTO;
import com.lihua.car.entity.CarBrand;
import com.lihua.car.entity.CarModel;
import com.lihua.car.mapper.CarBrandMapper;
import com.lihua.car.mapper.CarModelMapper;
import com.lihua.car.service.CarBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 汽车品牌表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Service
public class CarBrandServiceImpl extends ServiceImpl<CarBrandMapper,CarBrand> implements CarBrandService {

    @Autowired
    private CarBrandMapper brandMapper;
    
    @Autowired
    private CarModelMapper modelMapper;

    /**
     * 查询品牌列表
     *
     * @param brand 品牌查询条件
     * @return 品牌列表
     */
    @Override
    public List<CarBrand> selectBrandList(CarBrand brand) {
        return brandMapper.selectBrandList(brand);
    }

    @Override
    public IPage<? extends CarBrand> queryPage(CarBrandDTO dto) {
        IPage<CarBrand> iPage = new Page<>(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<CarBrand> queryWrapper = new QueryWrapper<>();
        brandMapper.selectPage(iPage, queryWrapper);
        return iPage;
    }

    /**
     * 根据ID查询品牌
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
    @Override
    public CarBrand selectBrandById(Long id) {
        return brandMapper.selectBrandById(id);
    }
    
    /**
     * 根据ID查询品牌详情（包含车型列表）
     *
     * @param id 品牌ID
     * @return 品牌详情
     */
    @Override
    public CarBrandDTO selectBrandDetailById(Long id) {
        // 查询品牌信息
        CarBrand brand = brandMapper.selectBrandById(id);
        if (brand == null) {
            return null;
        }
        
        // 转换为DTO
        CarBrandDTO brandDTO = new CarBrandDTO();
        BeanUtils.copyProperties(brand, brandDTO);
        
        // 查询品牌下的车型列表
        List<CarModel> models = modelMapper.selectModelsByBrandId(id);
        brandDTO.setModels(models);
        
        return brandDTO;
    }

    /**
     * 新增品牌
     *
     * @param brand 品牌信息
     * @return 结果
     */
    @Override
    public int insertBrand(CarBrand brand) {
        brand.setCreateTime(LocalDateTime.now());
        brand.setUpdateTime(LocalDateTime.now());
        return brandMapper.insertBrand(brand);
    }

    /**
     * 修改品牌
     *
     * @param brand 品牌信息
     * @return 结果
     */
    @Override
    public int updateBrand(CarBrand brand) {
        brand.setUpdateTime(LocalDateTime.now());
        return brandMapper.updateBrand(brand);
    }

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     * @return 结果
     */
    @Override
    public int deleteBrandById(Long id) {
        return brandMapper.deleteBrandById(id);
    }

    /**
     * 批量删除品牌
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBrandByIds(Long[] ids) {
        return brandMapper.deleteBrandByIds(ids);
    }
}