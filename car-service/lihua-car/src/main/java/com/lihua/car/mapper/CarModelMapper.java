package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 汽车车型表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Mapper
public interface CarModelMapper extends BaseMapper<CarModel> {

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
    CarModel selectModelById(@Param("id") Long id);

    /**
     * 根据品牌ID查询车型列表
     *
     * @param brandId 品牌ID
     * @return 车型列表
     */
    List<CarModel> selectModelsByBrandId(@Param("brandId") Long brandId);

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
}