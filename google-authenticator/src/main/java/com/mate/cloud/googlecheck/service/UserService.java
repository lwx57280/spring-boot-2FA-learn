package com.mate.cloud.googlecheck.service;

import java.util.Map;

public interface UserService {


    Boolean register(String userName, String password);

    String getQr(String userName);

    String enableGoogle(String userName, Integer code);


    String disableGoogle(String userName);

    Map<String, Object> login(String userName, String password, Integer googleCode);


}
