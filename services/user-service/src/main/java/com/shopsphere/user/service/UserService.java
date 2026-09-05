package com.shopsphere.user.service;

import com.shopsphere.user.dto.LoginRequest;
import com.shopsphere.user.dto.LoginResponse;
import com.shopsphere.user.dto.RegisterRequest;
import com.shopsphere.user.dto.UserResponse;
import com.shopsphere.user.entity.User;

public interface UserService {

    UserResponse registerUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request);
}
