package com.crossfive.secularWorld.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crossfive.framework.common.dto.Result;
import com.crossfive.framework.util.MD5Util;
import com.crossfive.framework.util.ResultUtil;
import com.crossfive.secularWorld.user.dao.UserDao;
import com.crossfive.secularWorld.user.domain.User;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	
	public String test() {
		return "hey,Man2!";
	}
	
	public void regist(User user) {
		userDao.save(user);
	}

	public User getInfo(long id) {
		long startTime = System.currentTimeMillis();
		User user = userDao.read(id);
		System.out.println("@@@@@query time:"+(System.currentTimeMillis() - startTime)+"ms");
//		Result result = ResultUtil.buildResultPush(user);
//		Users.push("123", "push@test", JSON.toJSONBytes(result));
		return user;
	}
	
	public List<User> findAll() {
		long startTime = System.currentTimeMillis();
		List<User> list = userDao.findAllUser();
		System.out.println("@@@@@query time:"+(System.currentTimeMillis() - startTime)+"ms");
		return list;
	}
	
	public Result login(String username, String password) {
		User user = userDao.findUserByUsername(username);
		if (user == null) {
			return ResultUtil.buildResultFail("No such user!");
		}
		if (user.getPassword().equals(MD5Util.getMD5Code(password, MD5Util.ENCODE_SIMPLE))) {
			return ResultUtil.buildResultSucc(user);
		}
		return ResultUtil.buildResultFail("Password error!");
	}

}
