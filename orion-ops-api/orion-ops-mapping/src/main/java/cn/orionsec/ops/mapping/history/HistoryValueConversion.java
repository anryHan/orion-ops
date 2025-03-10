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
package cn.orionsec.ops.mapping.history;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.entity.domain.HistoryValueSnapshotDO;
import cn.orionsec.ops.entity.vo.history.HistoryValueVO;

/**
 * 历史值 对象转换器
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/10 18:08
 */
public class HistoryValueConversion {

    static {
        TypeStore.STORE.register(HistoryValueSnapshotDO.class, HistoryValueVO.class, p -> {
            HistoryValueVO vo = new HistoryValueVO();
            vo.setId(p.getId());
            vo.setType(p.getOperatorType());
            vo.setBeforeValue(p.getBeforeValue());
            vo.setAfterValue(p.getAfterValue());
            vo.setUpdateUserId(p.getUpdateUserId());
            vo.setUpdateUserName(p.getUpdateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
