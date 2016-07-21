package com.crossfive.secularWorld.user.action;

import com.crossfive.framework.annotation.Action;
import com.crossfive.framework.annotation.Command;
import com.crossfive.framework.annotation.RequestParam;
import com.crossfive.framework.annotation.ServiceMeta;
import com.crossfive.framework.common.dto.Result;
import com.crossfive.framework.common.enums.State;

@Action
public interface UserAction {
	@Command(value="user@login",
			description="用户登录")
	@ServiceMeta(timeout=3000,syn=true,retry=1)
	public Result login(@RequestParam("username")String username, 
			@RequestParam("password")String password);

	@Command(value="user@regist",
			description="用户注册",
			state=State.ACTIVE,version=3)
	@ServiceMeta(timeout=3000)
	public Result regist(@RequestParam("username") String username, 
			@RequestParam(value="password", opt=true) String password,
			@RequestParam("mobile") String mobile,
			@RequestParam("email") String email,
			@RequestParam("adult") int adult,
			@RequestParam("age") int age,
			@RequestParam("sex") int sex,
			@RequestParam("career") int career);

	@Command(value="user@read",
			version=2)
	public Result read(@RequestParam("id") int id);

}
