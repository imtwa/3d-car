package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.dto.CarUserDTO;
import com.lihua.car.entity.CarUser;
import com.lihua.car.mapper.CarUserMapper;
import com.lihua.car.service.CarUserService;
import com.lihua.enums.ResultCodeEnum;
import com.lihua.utils.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * <p>
 * 前台用户表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Service
public class CarUserServiceImpl extends ServiceImpl<CarUserMapper, CarUser> implements CarUserService {

    @Override
    public String register(CarUserDTO carUserDTO) {
        // 验证滑块验证码
        Boolean verifyResult = verifySlideCode(carUserDTO.getSlideVerifyFlag());
        if (!verifyResult) {
            return null;
        }
        
        // 验证用户名是否存在
        Boolean checkResult = checkUsernameExist(carUserDTO.getUsername());
        if (checkResult) {
            return null; // 用户名已存在
        }
        
        // 创建用户
        CarUser carUser = new CarUser();
        BeanUtils.copyProperties(carUserDTO, carUser);
        
        // 设置默认值
        carUser.setId(IdWorker.getId(carUser));
        carUser.setPassword(SecurityUtils.encryptPassword(carUserDTO.getPassword()));
        carUser.setStatus("0"); // 正常状态
        carUser.setDelFlag("0"); // 未删除
        carUser.setRegisterType("1"); // 用户自助注册
        carUser.setCreateTime(LocalDateTime.now());
        
        // 保存用户
        save(carUser);
        
        return "注册成功";
    }

    @Override
    public String login(CarUserDTO carUserDTO) {
        // 验证滑块验证码
        Boolean verifyResult = verifySlideCode(carUserDTO.getSlideVerifyFlag());
        if (!verifyResult) {
            return null;
        }
        
        // 查询用户
        LambdaQueryWrapper<CarUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarUser::getUsername, carUserDTO.getUsername())
                .eq(CarUser::getDelFlag, "0");
        CarUser carUser = getOne(queryWrapper);
        
        // 验证用户是否存在
        if (carUser == null) {
            return null; // 用户名或密码错误
        }
        
        // 验证密码
        if (!SecurityUtils.matchesPassword(carUserDTO.getPassword(), carUser.getPassword())) {
            return null; // 用户名或密码错误
        }
        
        // 验证用户状态
        if ("1".equals(carUser.getStatus())) {
            return null; // 账号已被停用
        }
        
        // 更新登录时间
        carUser.setLastLoginTime(LocalDateTime.now());
        updateById(carUser);
        
        // TODO: 生成token
        String token = "car_user_token";
        
        return token;
    }

    @Override
    public Boolean checkUsernameExist(String username) {
        if (StringUtils.isEmpty(username)) {
            return null; // 用户名不能为空
        }
        
        LambdaQueryWrapper<CarUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarUser::getUsername, username)
                .eq(CarUser::getDelFlag, "0");
        long count = count(queryWrapper);
        
        return count > 0;
    }

    @Override
    public Boolean verifySlideCode(String slideVerifyFlag) {
        // 模拟滑块验证，实际项目中应该调用验证服务
        if (StringUtils.isEmpty(slideVerifyFlag)) {
            return null; // 滑块验证标识不能为空
        }
        
        // TODO: 实现真实的滑块验证逻辑
        return true;
    }
}