package com.lihua.car.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lihua.car.dto.CarBrandDTO;
import com.lihua.car.entity.CarBrand;

import java.util.List;

/**
 * <p>
 * 汽车品牌表 服务接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
public interface CarBrandService {

    /**
     * 查询品牌列表
     *
     * @param brand 品牌查询条件
     * @return 品牌列表
     */
    List<CarBrand> selectBrandList(CarBrand brand);

    /**
     * 根据ID查询品牌
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
    CarBrand selectBrandById(Long id);
    
    /**
     * 根据ID查询品牌详情（包含车型列表）
     *
     * @param id 品牌ID
     * @return 品牌详情
     */
    CarBrandDTO selectBrandDetailById(Long id);

    /**
     * 新增品牌
     *
     * @param brand 品牌信息
     * @return 结果
     */
    int insertBrand(CarBrand brand);

    /**
     * 修改品牌
     *
     * @param brand 品牌信息
     * @return 结果
     */
    int updateBrand(CarBrand brand);

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     * @return 结果
     */
    int deleteBrandById(Long id);

    /**
     * 批量删除品牌
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBrandByIds(Long[] ids);

    IPage<? extends CarBrand> queryPage(CarBrandDTO dto);
}