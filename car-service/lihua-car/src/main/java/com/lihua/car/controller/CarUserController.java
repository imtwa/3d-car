package com.lihua.car.controller;

import com.lihua.car.dto.CarUserDTO;
import com.lihua.car.service.CarUserService;
import com.lihua.model.web.BaseController;
import com.lihua.enums.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}