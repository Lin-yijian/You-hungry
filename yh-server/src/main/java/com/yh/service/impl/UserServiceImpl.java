package com.yh.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yh.constant.MessageConstant;
import com.yh.dto.UserLoginDTO;
import com.yh.entity.User;
import com.yh.exception.LoginFailedException;
import com.yh.mapper.UserMapper;
import com.yh.properties.WeChatProperties;
import com.yh.service.UserService;
import com.yh.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前用户是否为新用户
        User user = userMapper.login(openid);

        //如果是新用户，自动完成注册
        if(user == null){
             user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

             userMapper.insert(user);
        }
        //返回这个用户对象
        return user;




    }

    //调用微信接口服务，获取当前微信用户的openid
    public String getOpenid(String code){
        //调用微信接口服务，获取当前微信用户的openid
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid",weChatProperties.getAppid());
        paramMap.put("secret",weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, paramMap);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
