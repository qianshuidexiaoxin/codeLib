package com.zixin.ssm.service;

import java.util.List;

import com.zixin.ssm.entity.User;

public interface UserService {

	List<User> getUserList(int offset, int limit);
	 
}
