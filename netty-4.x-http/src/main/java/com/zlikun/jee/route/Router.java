package com.zlikun.jee.route;

import com.zlikun.jee.request.RequestHandler;
import com.zlikun.jee.request.impl.DefaultRequestHandler;
import com.zlikun.jee.request.impl.UserRequestHandler;

/**
 * 路由接口
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 20:47
 */
public class Router {

    /**
     * 请求路由逻辑
     *
     * @param uri
     * @return
     */
    public RequestHandler route(String uri) {

        if (uri.contains("/user")) {
            // 仅用于测试：用户
            return new UserRequestHandler();
        } else {
            // 其它所有请求
            return new DefaultRequestHandler();
        }
    }

}
