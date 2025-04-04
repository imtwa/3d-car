package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarBrand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 汽车品牌表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Mapper
public interface CarBrandMapper extends BaseMapper<CarBrand> {

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
    CarBrand selectBrandById(@Param("id") Long id);

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
}