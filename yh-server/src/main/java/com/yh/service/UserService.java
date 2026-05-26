package com.yh.service;

import com.yh.dto.UserLoginDTO;
import com.yh.entity.User;

public interface UserService {

    User wxLogin(UserLoginDTO userLoginDTO);
}
