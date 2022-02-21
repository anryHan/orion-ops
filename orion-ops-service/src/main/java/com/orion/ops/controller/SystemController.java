package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.ConfigIpListRequest;
import com.orion.ops.entity.vo.IpListConfigVO;
import com.orion.ops.service.api.SystemService;
import com.orion.servlet.web.Servlets;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 系统配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:07
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/system")
public class SystemController {

    @Resource
    private SystemService systemService;

    /**
     * 获取 ip 配置
     */
    @RequestMapping("/ip-info")
    public IpListConfigVO getIpInfo(HttpServletRequest request) {
        return systemService.getIpInfo(Servlets.getRemoteAddr(request));
    }

    /**
     * 配置 ip 列表
     */
    @RequestMapping("/config-ip")
    @EventLog(EventType.CONFIG_IP_LIST)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> configIpList(@RequestBody ConfigIpListRequest request) {
        systemService.configIpList(request);
        return HttpWrapper.ok();
    }

}
