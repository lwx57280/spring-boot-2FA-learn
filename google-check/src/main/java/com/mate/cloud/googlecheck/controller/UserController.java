package com.mate.cloud.googlecheck.controller;


import com.mate.cloud.googlecheck.annotation.NeedLogin;
import com.mate.cloud.googlecheck.base.BaseController;
import com.mate.cloud.googlecheck.common.Result;
import com.mate.cloud.googlecheck.dto.GoogleDTO;
import com.mate.cloud.googlecheck.dto.LoginDTO;
import com.mate.cloud.googlecheck.service.UserService;
import com.mate.cloud.googlecheck.utils.QRCodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;

@RestController
@RequestMapping("/user")
@Tag(name = "用户模块")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result register(@RequestBody LoginDTO dto) throws Exception {
        return userService.register(dto);
    }


    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result login(@RequestBody LoginDTO dto)throws Exception{
        return userService.login(dto);
    }




    @GetMapping("/generateGoogleSecret")
    @NeedLogin
    @Operation(summary = "生成google密钥")
    public Result generateGoogleSecret()throws Exception{
        return userService.generateGoogleSecret(this.getUser());
    }

    /**
     * 注意：这个需要地址栏请求,因为返回的是一个流
     * 注意：这个需要地址栏请求,因为返回的是一个流
     * 注意：这个需要地址栏请求,因为返回的是一个流
     * 显示一个二维码图片
     * @param secretQrCode   generateGoogleSecret接口返回的：secretQrCode
     * @param response
     * @throws Exception
     */
    @GetMapping("/genQrCode")
    @Operation(summary = "生成二维码，这个去地址栏请求，不要用Swagger-ui请求")
    public void genQrCode(String secretQrCode, HttpServletResponse response) throws Exception{
        response.setContentType("image/png");
        OutputStream stream = response.getOutputStream();
        QRCodeUtil.encode(secretQrCode,stream);
    }


    @GetMapping("/bindGoogle")
    @NeedLogin
    @Operation(summary = "绑定google验证")
    public Result bindGoogle(GoogleDTO dto)throws Exception{
        return userService.bindGoogle(dto,this.getUser(),this.getRequest());
    }

    @GetMapping("/googleLogin")
    @NeedLogin
    @Operation(summary = "google登录")
    public Result googleLogin(Long code) throws Exception{
        return userService.googleLogin(code,this.getUser(),this.getRequest());
    }


    @GetMapping("/getData")
    @NeedLogin(google = true)
    @Operation(summary = "获取数据")
    public Result getData()throws Exception{
        return userService.getData();
    }

}
