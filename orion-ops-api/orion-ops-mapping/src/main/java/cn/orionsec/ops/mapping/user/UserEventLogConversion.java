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
package cn.orionsec.ops.mapping.user;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.entity.domain.UserEventLogDO;
import cn.orionsec.ops.entity.vo.user.UserEventLogVO;

/**
 * 用户操作日志 对象转换器
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/10 18:05
 */
public class UserEventLogConversion {

    static {
        TypeStore.STORE.register(UserEventLogDO.class, UserEventLogVO.class, p -> {
            UserEventLogVO vo = new UserEventLogVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUsername());
            vo.setClassify(p.getEventClassify());
            vo.setType(p.getEventType());
            vo.setLog(p.getLogInfo());
            vo.setParams(p.getParamsJson());
            vo.setResult(p.getExecResult());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
