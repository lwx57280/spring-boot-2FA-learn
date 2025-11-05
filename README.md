
## 集成google authenticator 2FA 验证
https://github.com/wstrange/GoogleAuth

https://segmentfault.com/a/1190000043395409

https://cloud.tencent.com/developer/article/2310461
## 开源技术
https://github.com/la3rence/MFA/blob/master/src/test/java/com/auth/www/MultiFactorAuthenticatorApplicationTests.java

## 登录与2FA认证流程
![2FA.png](img-folder/2FA.png)

1、登录成功,由服务端程序生成随机秘钥,通过二维码返回给客户端 
2、authenticator客户端扫描二维码或者手动输入秘钥进行绑定
3、应用程序使用authenticator生成的验证码请求服务端验证



## **springboot集成authenticator**



```xml
<!-- GoogleAuth -->
<dependency>
  <groupId>com.warrenstrange</groupId>
  <artifactId>googleauth</artifactId>
  <version>1.5.0</version>
</dependency>
<dependency>
  <groupId>com.google.zxing</groupId>
  <artifactId>javase</artifactId>
  <version>3.4.1</version>
</dependency>
```



## **.使用方式** 

1. 用户下载authenticator,如果已经下载可跳过
2. 使用账密登录系统,如果没有绑定过authenticator,弹出二维码
3. 使用authenticator扫描二维码进行秘钥绑定,如果已经绑定过跳过
4. 使用authenticator生成的6为数字输入到系统进行验证



## 双因素认证（2FA）登录接口设计规范

**版本：v1.0**
 **作者：架构组**
 **适用范围：Spring Boot Auth 模块**
 **认证方式：用户名+密码 + Google Authenticator (TOTP)**

