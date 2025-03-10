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
package cn.orionsec.ops.handler.http;

import cn.orionsec.kit.http.ok.OkRequest;
import cn.orionsec.kit.lang.constant.StandardContentType;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * http api 请求
 *
 * @author 
 * @version 1.0.0
 * @since 2022/8/1 10:57
 */
public class HttpApiRequest extends OkRequest {

    public HttpApiRequest(String url, HttpApiDefined api) {
        this.url = url + api.getPath();
        this.method = api.getMethod().method();
    }

    /**
     * 设置 json body
     *
     * @param body body
     * @return this
     */
    public HttpApiRequest jsonBody(Object body) {
        this.contentType = StandardContentType.APPLICATION_JSON;
        this.body(JSON.toJSONString(body));
        return this;
    }

    /**
     * 请求获取 httpWrapper
     *
     * @param dataClass T
     * @param <T>       T
     * @return httpWrapper
     */
    public <T> HttpWrapper<T> getHttpWrapper(Class<T> dataClass) {
        return this.await().getJsonObjectBody(new TypeReference<HttpWrapper<T>>(dataClass) {
        });
    }

    /**
     * 请求获取 json
     *
     * @param type type
     * @param <T>  T
     * @return T
     */
    public <T> T getJson(TypeReference<T> type) {
        return this.await().getJsonObjectBody(type);
    }

}
