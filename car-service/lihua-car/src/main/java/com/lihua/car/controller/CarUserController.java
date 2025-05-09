package com.lihua.car.controller;

import com.lihua.car.dto.CarUserDTO;
import com.lihua.car.service.CarUserService;
import com.lihua.car.vo.CarLoginResult;
import com.lihua.model.web.BaseController;
import com.lihua.enums.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前台用户表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@RestController
@RequestMapping("/car/user")
public class CarUserController extends BaseController {

    @Autowired
    private CarUserService carUserService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public String register(@RequestBody CarUserDTO carUserDTO) {
        return success(carUserService.register(carUserDTO));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public String login(@RequestBody CarUserDTO carUserDTO) {
        return success(carUserService.login(carUserDTO));
    }

    /**
     * 验证用户名是否存在
     */
    @GetMapping("/check/{username}")
    public String checkUsernameExist(@PathVariable("username") String username) {
        return success(carUserService.checkUsernameExist(username));
    }

    /**
     * 验证滑块验证码
     */
    @GetMapping("/verify/{slideVerifyFlag}")
    public String verifySlideCode(@PathVariable("slideVerifyFlag") String slideVerifyFlag) {
        return success(carUserService.verifySlideCode(slideVerifyFlag));
    }
    
    /**
     * 分页查询前台用户
     */
    @PostMapping("/page")
    public String page(@RequestBody CarUserDTO carUserDTO) {
        return success(carUserService.page(carUserDTO));
    }
    
    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id) {
        return success(carUserService.getCarUserById(id));
    }
    
    /**
     * 新增或修改用户
     */
    @PostMapping
    public String save(@RequestBody CarUserDTO carUserDTO) {
        return success(carUserService.saveOrUpdate(carUserDTO));
    }
    
    /**
     * 修改用户状态
     */
    @PostMapping("/updateStatus/{id}/{status}")
    public String updateStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return success(carUserService.updateStatus(id, status));
    }
    
    /**
     * 批量删除用户
     */
    @DeleteMapping
    public String delete(@RequestBody List<Long> ids) {
        return success(carUserService.deleteByIds(ids));
    }
    
    /**
     * 重置用户密码
     */
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody CarUserDTO carUserDTO) {
        return success(carUserService.resetPassword(carUserDTO));
    }
    
    /**
     * 导出用户数据
     */
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody CarUserDTO carUserDTO) {
        byte[] data = carUserService.export(carUserDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "前台用户数据.xlsx");
        return ResponseEntity.ok().headers(headers).body(data);
    }
}