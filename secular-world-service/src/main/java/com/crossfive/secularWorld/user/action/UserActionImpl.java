package com.crossfive.secularWorld.user.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.rpc.RpcContext;
import com.crossfive.framework.common.Constants;
import com.crossfive.framework.common.dto.Result;
import com.crossfive.framework.common.dto.UserDto;
import com.crossfive.framework.util.MD5Util;
import com.crossfive.framework.util.ResultUtil;
import com.crossfive.secularWorld.user.domain.User;
import com.crossfive.secularWorld.user.service.UserService;


@Component
public class UserActionImpl implements UserAction {

	@Autowired
	private UserService userService;
	
	
	@Override
	public Result login(String username, String password) {
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return ResultUtil.buildResultFail("Username and password can't be empty");
		}
		Result result = userService.login(username, password);
		if (result.getCode() != ResultUtil.SUCCESS) {
			return result;
		}
		User user = (User)result.getData();
		UserDto dto = UserDto.getNewUserDto(String.valueOf(user.getId()), user.getId(), "poem");
//		Session session = request.getNewSession();
//		session.setAttribute("user", dto);
		return ResultUtil.buildResultSucc(dto);
	}
	
	@Override
	public Result regist(String username, String password, String mobile,
			String email, int adult, int age, int sex, int career) {
		String password_md5 = MD5Util.getMD5Code(password, MD5Util.ENCODE_SIMPLE);
		User user = new User();
		user.setPassword(password_md5);
		user.setUsername(username);
		user.setAdult(adult);
		user.setMobile(mobile);
		user.setEmail(email);
		user.setCareer(career);
		user.setSex(sex);
		user.setAge(age);
		long now = System.currentTimeMillis();
		user.setCreateTime(now);
		user.setModifyTime(now);
		userService.regist(user);
		return ResultUtil.buildResultSucc();
	}
	
	@Override
	public Result read(int id) {
		String userIdStr = RpcContext.getContext().getAttachment(Constants._USER_ID);
		if (StringUtils.isEmpty(userIdStr)) {
			return ResultUtil.buildResultFail("未获取身份信息！");
		}
		if (id != Integer.valueOf(userIdStr)) {
			return ResultUtil.buildResultFail("不能查看其它人信息！");
		}
		User user = userService.getInfo(id);
		return ResultUtil.buildResultSucc(user);
	}
	
//	@Command("user@findAll")
//	public Result findAll() {
//		List<User> list = userService.findAll();
//		return ResultUtil.buildResultSucc(list);
//	}
	
}
