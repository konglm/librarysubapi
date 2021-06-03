package com.school.library.heartbeat;


import com.jfnice.core.JFniceBaseController;

/**
 * 心跳控制器（使用场景：某些前端页面一开始进入的时候，不会马上请求后端接口，此时，如果登录过期，前端将无法感知，所以使用此接口
 * 告诉前端页面，该登录是否有效）
 */
public class HeartBeatController extends JFniceBaseController {

	public void index(){
		ok("success");
	}

}