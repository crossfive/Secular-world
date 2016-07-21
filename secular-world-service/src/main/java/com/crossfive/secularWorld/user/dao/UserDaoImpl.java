package com.crossfive.secularWorld.user.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.crossfive.framework.jdbc.BaseDao;
import com.crossfive.secularWorld.user.domain.User;


@Component
public class UserDaoImpl extends BaseDao<User> implements UserDao {
	
	final static String regist_sql = "insert into user(id,username,state,password,adult) value(null,?,0,?,0) ";
	final static String findUser_sql = "select * from user where username = ?";
	
	@Override
	public void regist(String username, String password) {
		Object[] params = new Object[] {username, password};
		jdbcTemplate.update(regist_sql, params);
	}
	
	@Override
	public List<User> findAllUser() {
		return this.querySql("select * from user", null);
	}
	
	@Override
	public User findUserByUsername(String username) {
		Object[] params = new Object[] {username};
		return this.queryOne(findUser_sql, params);
	}
	

}
