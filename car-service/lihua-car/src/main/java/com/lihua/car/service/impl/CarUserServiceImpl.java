package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        if (!StringUtils.hasText(username)) {
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
        if (!StringUtils.hasText(slideVerifyFlag)) {
            return false; // 滑块验证标识不能为空，返回false而不是null
        }
        
        // TODO: 实现真实的滑块验证逻辑
        return true;
    }
    
    @Override
    public Page<CarUser> page(CarUserDTO carUserDTO) {
        // 创建查询条件
        LambdaQueryWrapper<CarUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 仅查询前台用户，不查询已删除用户
        queryWrapper.eq(CarUser::getDelFlag, "0");
        
        // 添加查询条件
        queryWrapper.like(StringUtils.hasText(carUserDTO.getUsername()), CarUser::getUsername, carUserDTO.getUsername())
                .like(StringUtils.hasText(carUserDTO.getNickname()), CarUser::getNickname, carUserDTO.getNickname())
                .eq(StringUtils.hasText(carUserDTO.getStatus()), CarUser::getStatus, carUserDTO.getStatus());
        
        // 时间范围查询
        if (StringUtils.hasText(carUserDTO.getCreateTimeStart()) && StringUtils.hasText(carUserDTO.getCreateTimeEnd())) {
            queryWrapper.between(CarUser::getCreateTime, 
                    LocalDateTime.parse(carUserDTO.getCreateTimeStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.parse(carUserDTO.getCreateTimeEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(CarUser::getCreateTime);
        
        // 分页查询
        Page<CarUser> page = new Page<>(carUserDTO.getPageNum(), carUserDTO.getPageSize());
        return page(page, queryWrapper);
    }
    
    @Override
    public CarUserDTO getCarUserById(Long id) {
        if (id == null) {
            return null;
        }
        
        CarUser carUser = getById(id);
        if (carUser == null || "1".equals(carUser.getDelFlag())) {
            return null;
        }
        
        CarUserDTO carUserDTO = new CarUserDTO();
        BeanUtils.copyProperties(carUser, carUserDTO);
        
        // 不返回密码
        carUserDTO.setPassword(null);
        
        return carUserDTO;
    }
    
    @Override
    @Transactional
    public Boolean saveOrUpdate(CarUserDTO carUserDTO) {
        if (carUserDTO == null) {
            return false;
        }
        
        CarUser carUser = new CarUser();
        BeanUtils.copyProperties(carUserDTO, carUser);
        
        // 新增用户
        if (carUser.getId() == null) {
            // 检查用户名是否存在
            if (checkUsernameExist(carUser.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            
            carUser.setId(IdWorker.getId(carUser));
            carUser.setPassword(SecurityUtils.encryptPassword(carUserDTO.getPassword()));
            carUser.setDelFlag("0");
            carUser.setCreateTime(LocalDateTime.now());
            carUser.setRegisterType("0"); // 管理员新增
            
            return save(carUser);
        } 
        // 更新用户
        else {
            // 获取原有用户数据
            CarUser oldUser = getById(carUser.getId());
            if (oldUser == null || "1".equals(oldUser.getDelFlag())) {
                return false;
            }
            
            // 用户名不能修改
            carUser.setUsername(oldUser.getUsername());
            // 密码不变
            carUser.setPassword(oldUser.getPassword());
            carUser.setDelFlag(oldUser.getDelFlag());
            carUser.setCreateTime(oldUser.getCreateTime());
            carUser.setRegisterType(oldUser.getRegisterType());
            carUser.setUpdateTime(LocalDateTime.now());
            
            return updateById(carUser);
        }
    }
    
    @Override
    @Transactional
    public Boolean updateStatus(Long id, String status) {
        if (id == null || !StringUtils.hasText(status)) {
            return false;
        }
        
        CarUser carUser = getById(id);
        if (carUser == null || "1".equals(carUser.getDelFlag())) {
            return false;
        }
        
        carUser.setStatus(status);
        carUser.setUpdateTime(LocalDateTime.now());
        
        return updateById(carUser);
    }
    
    @Override
    @Transactional
    public Boolean deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        // 逻辑删除
        return lambdaUpdate()
                .set(CarUser::getDelFlag, "1")
                .set(CarUser::getUpdateTime, LocalDateTime.now())
                .in(CarUser::getId, ids)
                .update();
    }
    
    @Override
    @Transactional
    public Boolean resetPassword(CarUserDTO carUserDTO) {
        if (carUserDTO == null || carUserDTO.getId() == null || !StringUtils.hasText(carUserDTO.getPassword())) {
            return false;
        }
        
        CarUser carUser = getById(carUserDTO.getId());
        if (carUser == null || "1".equals(carUser.getDelFlag())) {
            return false;
        }
        
        // 加密新密码
        String encryptedPassword = SecurityUtils.encryptPassword(carUserDTO.getPassword());
        
        // 更新密码
        return lambdaUpdate()
                .set(CarUser::getPassword, encryptedPassword)
                .set(CarUser::getUpdateTime, LocalDateTime.now())
                .eq(CarUser::getId, carUserDTO.getId())
                .update();
    }
    
    @Override
    public byte[] export(CarUserDTO carUserDTO) {
        try {
            // 查询用户数据
            LambdaQueryWrapper<CarUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CarUser::getDelFlag, "0");
            
            // 添加查询条件
            queryWrapper.like(StringUtils.hasText(carUserDTO.getUsername()), CarUser::getUsername, carUserDTO.getUsername())
                    .like(StringUtils.hasText(carUserDTO.getNickname()), CarUser::getNickname, carUserDTO.getNickname())
                    .eq(StringUtils.hasText(carUserDTO.getStatus()), CarUser::getStatus, carUserDTO.getStatus());
            
            // 时间范围查询
            if (StringUtils.hasText(carUserDTO.getCreateTimeStart()) && StringUtils.hasText(carUserDTO.getCreateTimeEnd())) {
                queryWrapper.between(CarUser::getCreateTime, 
                        LocalDateTime.parse(carUserDTO.getCreateTimeStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        LocalDateTime.parse(carUserDTO.getCreateTimeEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            
            // 按创建时间降序排序
            queryWrapper.orderByDesc(CarUser::getCreateTime);
            
            // 查询数据
            List<CarUser> userList = list(queryWrapper);
            
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("前台用户数据");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"用户名", "昵称", "性别", "状态", "邮箱", "手机号码", "注册类型", "创建时间", "最后登录时间", "备注"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (CarUser user : userList) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(user.getUsername());
                row.createCell(1).setCellValue(user.getNickname() != null ? user.getNickname() : "");
                
                // 性别
                String gender = "";
                if ("0".equals(user.getGender())) {
                    gender = "男";
                } else if ("1".equals(user.getGender())) {
                    gender = "女";
                } else {
                    gender = "保密";
                }
                row.createCell(2).setCellValue(gender);
                
                // 状态
                String status = "0".equals(user.getStatus()) ? "正常" : "停用";
                row.createCell(3).setCellValue(status);
                
                row.createCell(4).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(5).setCellValue(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
                
                // 注册类型
                String registerType = "0".equals(user.getRegisterType()) ? "管理员新增" : "用户自助注册";
                row.createCell(6).setCellValue(registerType);
                
                // 创建时间
                row.createCell(7).setCellValue(user.getCreateTime() != null ? user.getCreateTime().format(formatter) : "");
                
                // 最后登录时间
                row.createCell(8).setCellValue(user.getLastLoginTime() != null ? user.getLastLoginTime().format(formatter) : "");
                
                // 备注
                row.createCell(9).setCellValue(user.getRemark() != null ? user.getRemark() : "");
            }
            
            // 调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("导出用户数据失败", e);
            throw new RuntimeException("导出用户数据失败: " + e.getMessage(), e);
        }
    }
}