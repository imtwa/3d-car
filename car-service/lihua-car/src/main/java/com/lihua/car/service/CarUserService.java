package com.lihua.car.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lihua.car.dto.CarUserDTO;
import com.lihua.car.entity.CarUser;
import com.lihua.car.vo.CarLoginResult;
import com.lihua.enums.ResultCodeEnum;
import java.util.List;

/**
 * <p>
 * 前台用户表 服务类
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
public interface CarUserService extends IService<CarUser> {

    /**
     * 用户注册
     *
     * @param carUserDTO 用户信息
     * @return 注册结果
     */
    String register(CarUserDTO carUserDTO);

    /**
     * 用户登录
     *
     * @param carUserDTO 登录信息
     * @return 登录结果，包含token和用户信息
     */
    CarLoginResult login(CarUserDTO carUserDTO);

    /**
     * 验证用户名是否存在
     *
     * @param username 用户名
     * @return 验证结果
     */
    Boolean checkUsernameExist(String username);

    /**
     * 验证滑块验证码
     *
     * @param slideVerifyFlag 滑块验证标识
     * @return 验证结果
     */
    Boolean verifySlideCode(String slideVerifyFlag);
    
    /**
     * 分页查询前台用户
     *
     * @param carUserDTO 查询条件
     * @return 分页结果
     */
    Page<CarUser> page(CarUserDTO carUserDTO);
    
    /**
     * 根据ID获取用户详细信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    CarUserDTO getCarUserById(Long id);
    
    /**
     * 保存或更新用户信息
     *
     * @param carUserDTO 用户信息
     * @return 操作结果
     */
    Boolean saveOrUpdate(CarUserDTO carUserDTO);
    
    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态值
     * @return 操作结果
     */
    Boolean updateStatus(Long id, String status);
    
    /**
     * 批量删除用户
     *
     * @param ids 用户ID集合
     * @return 操作结果
     */
    Boolean deleteByIds(List<Long> ids);
    
    /**
     * 重置用户密码
     *
     * @param carUserDTO 包含用户ID和新密码的信息
     * @return 操作结果
     */
    Boolean resetPassword(CarUserDTO carUserDTO);
    
    /**
     * 导出用户数据
     *
     * @param carUserDTO 查询条件
     * @return 导出结果，返回Excel文件的字节数组或文件路径
     */
    byte[] export(CarUserDTO carUserDTO);
}