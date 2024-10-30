/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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
package cn.orionsec.ops.constant.ws;

/**
 * ws服务端关闭reason
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/16 15:21
 */
public class WsCloseReason {

    private WsCloseReason() {
    }

    public static final String CLOSED_CONNECTION = "closed connection...";

    public static final String IDENTITY_MISMATCH = "identity mismatch...";

    public static final String AUTHENTICATION_FAILURE = "authentication failure...";

    public static final String REMOTE_SERVER_UNREACHABLE = "remote server unreachable...";

    public static final String CONNECTION_TIMEOUT = "connection timeout...";

    public static final String REMOTE_SERVER_AUTHENTICATION_FAILURE = "remote server authentication failure...";

    public static final String MACHINE_DISABLED = "machine disabled...";

    public static final String UNABLE_TO_CONNECT_REMOTE_SERVER = "unable to connect remote server...";

    public static final String FORCED_OFFLINE = "forced offline...";

}
