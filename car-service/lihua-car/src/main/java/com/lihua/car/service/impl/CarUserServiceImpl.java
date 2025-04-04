package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.dto.CarUserDTO;
import com.lihua.car.entity.CarUser;
import com.lihua.car.mapper.CarUserMapper;
import com.lihua.car.service.CarUserService;
import com.lihua.car.vo.CarLoginResult;
import com.lihua.enums.ResultCodeEnum;
import com.lihua.utils.security.JwtUtils;
import com.lihua.utils.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
public class CarUserServiceImpl extends ServiceImpl<CarUserMapper, CarUser> implements CarUserService {

    @Override
    @Transactional
    public String register(CarUserDTO carUserDTO) {
        log.info("开始用户注册流程，用户名: {}", carUserDTO.getUsername());
        
        try {
            // // 验证滑块验证码
            // Boolean verifyResult = verifySlideCode(carUserDTO.getSlideVerifyFlag());
            // if (!verifyResult) {
            //     log.warn("滑块验证失败，用户名: {}", carUserDTO.getUsername());
            //     return null;
            // }
            
            // 验证用户名是否存在
            Boolean checkResult = checkUsernameExist(carUserDTO.getUsername());
            if (checkResult) {
                log.warn("用户名已存在: {}", carUserDTO.getUsername());
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
            
            log.info("准备保存用户数据到数据库，用户ID: {}", carUser.getId());
            
            // 保存用户
            boolean saveResult = save(carUser);
            
            if (!saveResult) {
                log.error("用户数据保存失败，用户名: {}", carUserDTO.getUsername());
                throw new RuntimeException("用户数据保存失败");
            }
            
            log.info("用户注册成功，用户ID: {}", carUser.getId());
            return "注册成功";
        } catch (Exception e) {
            log.error("用户注册过程发生异常，用户名: {}, 错误信息: {}", carUserDTO.getUsername(), e.getMessage(), e);
            throw new RuntimeException("用户注册失败: " + e.getMessage(), e);
        }
    }

    @Override
    public CarLoginResult login(CarUserDTO carUserDTO) {
        // // 验证滑块验证码
        // Boolean verifyResult = verifySlideCode(carUserDTO.getSlideVerifyFlag());
        // if (!verifyResult) {
        //     return null;
        // }
        
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
        
        // 生成token
        String redisKey = "car_user:" + carUser.getId();
        String token = JwtUtils.create(redisKey);
        
        // 清除密码信息，避免返回给前端
        carUser.setPassword(null);
        
        // 封装登录结果
        CarLoginResult loginResult = new CarLoginResult()
                .setToken(token)
                .setUser(carUser);
        
        return loginResult;
    }

    @Override
    public Boolean checkUsernameExist(String username) {
        if (StringUtils.isEmpty(username)) {
            return false; // 用户名不能为空，返回false而不是null
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
            return false; // 滑块验证标识不能为空，返回false而不是null
        }
        
        // TODO: 实现真实的滑块验证逻辑
        return true;
    }
}