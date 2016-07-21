package com.crossfive.secularWorld.user.dao;

import java.util.List;

import com.crossfive.framework.jdbc.IBaseDao;
import com.crossfive.secularWorld.user.domain.User;

public interface UserDao extends IBaseDao<User> {

	void regist(String username, String password);

	List<User> findAllUser();

	User findUserByUsername(String username);

}
