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
package cn.orionsec.ops.constant.system;

import cn.orionsec.kit.lang.exception.argument.InvalidArgumentException;
import cn.orionsec.kit.lang.function.Conversion;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.Valid;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.common.EnableType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 系统配置项
 *
 * @author 
 * @version 1.0.0
 * @since 2022/2/18 21:16
 */
@AllArgsConstructor
@Getter
public enum SystemConfigKey {

    /**
     * 文件清理阈值 (天)
     */
    FILE_CLEAN_THRESHOLD(10, SystemEnvAttr.FILE_CLEAN_THRESHOLD) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) >= 0 && Integer.parseInt(s) <= 1000;
        }

        @Override
        protected String validTips() {
            return FILE_CLEAN_THRESHOLD_TIPS;
        }
    },

    /**
     * 是否启用自动清理
     */
    ENABLE_AUTO_CLEAN_FILE(20, SystemEnvAttr.ENABLE_AUTO_CLEAN_FILE) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否允许多端登录
     */
    ALLOW_MULTIPLE_LOGIN(30, SystemEnvAttr.ALLOW_MULTIPLE_LOGIN) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否启用登录失败锁定
     */
    LOGIN_FAILURE_LOCK(40, SystemEnvAttr.LOGIN_FAILURE_LOCK) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否启用登录IP绑定
     */
    LOGIN_IP_BIND(50, SystemEnvAttr.LOGIN_IP_BIND) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否启用凭证自动续签
     */
    LOGIN_TOKEN_AUTO_RENEW(60, SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 登录凭证有效期 (时)
     */
    LOGIN_TOKEN_EXPIRE(70, SystemEnvAttr.LOGIN_TOKEN_EXPIRE) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) > 0 && Integer.parseInt(s) <= 720;
        }

        @Override
        protected String validTips() {
            return LOGIN_TOKEN_EXPIRE_TIPS;
        }
    },

    /**
     * 登录失败锁定阈值 (次)
     */
    LOGIN_FAILURE_LOCK_THRESHOLD(80, SystemEnvAttr.LOGIN_FAILURE_LOCK_THRESHOLD) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) > 0 && Integer.parseInt(s) <= 100;
        }

        @Override
        protected String validTips() {
            return LOGIN_FAILURE_LOCK_THRESHOLD_TIPS;
        }
    },

    /**
     * 登录自动续签阈值 (时)
     */
    LOGIN_TOKEN_AUTO_RENEW_THRESHOLD(90, SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW_THRESHOLD) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) > 0 && Integer.parseInt(s) <= 720;
        }

        @Override
        protected String validTips() {
            return LOGIN_TOKEN_AUTO_RENEW_THRESHOLD_TIPS;
        }
    },

    /**
     * 自动恢复启用的调度任务
     */
    RESUME_ENABLE_SCHEDULER_TASK(100, SystemEnvAttr.RESUME_ENABLE_SCHEDULER_TASK) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * SFTP 上传文件最大阈值 (MB)
     */
    SFTP_UPLOAD_THRESHOLD(110, SystemEnvAttr.SFTP_UPLOAD_THRESHOLD) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) >= 10 && Integer.parseInt(s) <= 2048;
        }

        @Override
        protected String validTips() {
            return SFTP_UPLOAD_THRESHOLD_TIPS;
        }
    },

    /**
     * 统计缓存有效时间 (分)
     */
    STATISTICS_CACHE_EXPIRE(120, SystemEnvAttr.STATISTICS_CACHE_EXPIRE) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) > 0 && Integer.parseInt(s) <= 10080;
        }

        @Override
        protected String validTips() {
            return STATISTICS_CACHE_EXPIRE_TIPS;
        }
    },

    /**
     * 终端后台主动推送心跳
     */
    TERMINAL_ACTIVE_PUSH_HEARTBEAT(130, SystemEnvAttr.TERMINAL_ACTIVE_PUSH_HEARTBEAT) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    ;

    private static final String FILE_CLEAN_THRESHOLD_TIPS = "文件清理阈值需要在 10 ~ 2048 之间";

    private static final String LOGIN_TOKEN_EXPIRE_TIPS = "登录凭证有效期需要在 1 ~ 720 之间";

    private static final String LOGIN_FAILURE_LOCK_THRESHOLD_TIPS = "登录失败锁定阈值需要在 1 ~ 100 之间";

    private static final String LOGIN_TOKEN_AUTO_RENEW_THRESHOLD_TIPS = "登录自动续签阈值需要在 1 ~ 720 之间";

    private static final String SFTP_UPLOAD_THRESHOLD_TIPS = "上传文件阈值需要在 10 ~ 2048 之间";

    private static final String STATISTICS_CACHE_EXPIRE_TIPS = "统计缓存有效时间需要在 1 ~ 10080 之间";

    private static final Conversion<String, String> ENABLED_TYPE = s -> EnableType.of(Boolean.valueOf(s)).getLabel();

    private final Integer type;

    private final SystemEnvAttr env;

    /**
     * 验证
     *
     * @param s s
     * @return value
     */
    protected boolean valid(String s) {
        return true;
    }

    /**
     * 验证失败提示
     *
     * @return 验证失败提示
     */
    protected String validTips() {
        return MessageConst.INVALID_CONFIG;
    }

    /**
     * 转化值
     *
     * @param s s
     * @return value
     */
    protected String conversionValue(String s) {
        return s;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public String getValue(String s) {
        // 验证
        try {
            Valid.isTrue(this.valid(s), this.validTips());
        } catch (InvalidArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw Exceptions.argument(this.validTips());
        }
        // 转化
        return this.conversionValue(s);
    }

    public static SystemConfigKey of(Integer type) {
        if (type == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.type.equals(type))
                .findFirst()
                .orElse(null);
    }

}
