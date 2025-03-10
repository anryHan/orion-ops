/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.utils;

import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.ops.constant.PropertiesConst;
import cn.orionsec.ops.constant.ResultCode;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.entity.dto.user.UserDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 环境工具类
 *
 * @author 
 * @version 1.0.0
 * @since 2021/4/2 9:52
 */
public class Currents {

    private Currents() {
    }

    /**
     * 获取当前登录token
     *
     * @param request request
     * @return token
     */
    public static String getLoginToken(HttpServletRequest request) {
        return getToken(request, PropertiesConst.LOGIN_TOKEN_HEADER);
    }

    /**
     * 获取token
     *
     * @param request request
     * @param token   tokenKey
     * @return token
     */
    public static String getToken(HttpServletRequest request, String token) {
        return request.getHeader(token);
    }

    /**
     * 获取当前登录用户
     * <p>
     * 可以匿名登录的接口并且用户未登录获取的是null
     *
     * @return 用户
     */
    public static UserDTO getUser() {
        return UserHolder.get();
    }

    /**
     * 获取当前登录用户id
     *
     * @return id
     */
    public static Long getUserId() {
        return Optional.ofNullable(UserHolder.get())
                .map(UserDTO::getId)
                .orElse(null);
    }

    /**
     * 获取当前登录用户username
     *
     * @return username
     */
    public static String getUserName() {
        return Optional.ofNullable(UserHolder.get())
                .map(UserDTO::getUsername)
                .orElse(null);
    }

    /**
     * 是否为 管理员
     *
     * @return true 管理员
     */
    public static boolean isAdministrator() {
        UserDTO user = UserHolder.get();
        if (user == null) {
            return false;
        }
        Integer roleType = user.getRoleType();
        return RoleType.isAdministrator(roleType);
    }

    /**
     * 检查是否为管理员权限
     */
    public static void requireAdministrator() {
        if (!isAdministrator()) {
            throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.NO_PERMISSION));
        }
    }

}
