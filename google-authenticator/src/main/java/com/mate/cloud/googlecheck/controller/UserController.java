package com.mate.cloud.googlecheck.controller;


import com.mate.cloud.googlecheck.dto.LoginDTO;
import com.mate.cloud.googlecheck.service.UserService;
import com.mate.cloud.protocol.response.BaseResponse;
import com.mate.cloud.protocol.web.controller.AdminBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends AdminBaseController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册（自动生成谷歌秘钥）
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @PostMapping("/register")
    public BaseResponse register(@RequestBody LoginDTO dto) throws Exception {
        return successBodyResponse(userService.register(dto.getUsername(), dto.getPassword()));
    }

    /**
     * 获取谷歌绑定二维码
     *
     * @param userName
     * @return
     */
    @GetMapping("/getQr")
    public BaseResponse getQr(String userName) {
        return successBodyResponse(userService.getQr(userName));
    }

    /**
     * 开启谷歌验证（校验第一次验证码）
     *
     * @param username
     * @param code
     * @return
     */
    @PostMapping("/enableGoogle")
    public BaseResponse enableGoogle(String username, Integer code) {
        return successBodyResponse(userService.enableGoogle(username, code));
    }

    /**
     * @param username
     * @return
     */
    @PostMapping("/disableGoogle")
    public BaseResponse disableGoogle(String username) {
        return successBodyResponse(userService.disableGoogle(username));
    }

    /**
     * 登录：密码 + 谷歌验证码 双重验证
     *
     * @param username
     * @param password
     * @param code
     * @return
     */
    @PostMapping("/login")
    public BaseResponse login(String username, String password, Integer code) {
        return successBodyResponse(userService.login(username, password, code));
    }
}
