package com.crossfive.secularWorld.user.service;

import com.crossfive.framework.common.dto.Result;
import com.crossfive.secularWorld.user.domain.User;

public interface UserService {

	public User getInfo(long id);

	public Result login(String username, String password);

	public void regist(User user);
	
}
