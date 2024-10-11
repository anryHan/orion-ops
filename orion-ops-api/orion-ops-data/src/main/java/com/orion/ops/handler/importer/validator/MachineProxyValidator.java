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
package com.orion.ops.handler.importer.validator;

import com.orion.lang.utils.Strings;
import com.orion.ops.constant.machine.ProxyType;
import com.orion.ops.entity.importer.MachineProxyImportDTO;
import com.orion.ops.utils.Valid;
import com.orion.ops.utils.ValueMix;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 机器代理表 数据验证器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/31 10:12
 */
public class MachineProxyValidator implements DataValidator {

    private MachineProxyValidator() {
    }

    public static final MachineProxyValidator INSTANCE = new MachineProxyValidator();

    public static final int HOST_MAX_LEN = 128;

    public static final int USER_NAME_MAX_LEN = 128;

    public static final int PASSWORD_MAX_LEN = 128;

    public static final int PORT_MIN_RANGE = 2;

    public static final int PORT_MAX_RANGE = 65534;

    public static final int DESCRIPTION_MAX_LEN = 64;

    public static final String HOST_EMPTY_MESSAGE = "主机不能为空";

    public static final String HOST_LEN_MESSAGE = "主机长度不能大于 " + HOST_MAX_LEN + "位";

    public static final String PROXY_TYPE_EMPTY_MESSAGE = "代理类型不能为空";

    public static final String PROXY_TYPE_MESSAGE = "代理类型只能为 " + Arrays.stream(ProxyType.values()).map(ProxyType::getLabel).collect(Collectors.toList());

    public static final String USER_NAME_LEN_MESSAGE = "用户名长度不能大于 " + USER_NAME_MAX_LEN + "位";

    public static final String PASSWORD_DECRYPT_MESSAGE = "密码密文解密失败";

    public static final String PASSWORD_LEN_MESSAGE = "密码明文长度不能大于 " + PASSWORD_MAX_LEN + "位";

    public static final String USERNAME_PASSWORD_ABSENT_MESSAGE = "用户名密码需要同时存在";

    public static final String PORT_EMPTY_MESSAGE = "端口不能为空";

    public static final String PORT_LEN_MESSAGE = "端口必须在" + PORT_MIN_RANGE + "~" + PORT_MAX_RANGE + "之间";

    public static final String DESCRIPTION_LEN_MESSAGE = "描述长度不能大于 " + DESCRIPTION_MAX_LEN + "位";

    @Override
    public void validData(Object o) {
        if (o instanceof MachineProxyImportDTO) {
            validImport((MachineProxyImportDTO) o);
        }
    }

    /**
     * 验证导入数据
     *
     * @param row row
     */
    private void validImport(MachineProxyImportDTO row) {
        String host = row.getHost();
        Integer port = row.getPort();
        String proxyType = row.getProxyType();
        String username = row.getUsername();
        String encryptPassword = row.getEncryptPassword();
        String password = row.getImportPassword();
        String description = row.getDescription();
        Valid.notBlank(host, HOST_EMPTY_MESSAGE);
        Valid.validLengthLte(host, HOST_MAX_LEN, HOST_LEN_MESSAGE);
        Valid.notNull(port, PORT_EMPTY_MESSAGE);
        Valid.inRange(port, PORT_MIN_RANGE, PORT_MAX_RANGE, PORT_LEN_MESSAGE);
        Valid.notBlank(proxyType, PROXY_TYPE_EMPTY_MESSAGE);
        Valid.notNull(ProxyType.of(proxyType), PROXY_TYPE_MESSAGE);
        if (!Strings.isBlank(username)) {
            Valid.validLengthLte(username, USER_NAME_MAX_LEN, USER_NAME_LEN_MESSAGE);
        }
        if (!Strings.isBlank(password)) {
            Valid.validLengthLte(password, PASSWORD_MAX_LEN, PASSWORD_LEN_MESSAGE);
        }
        if (!Strings.isBlank(encryptPassword)) {
            String decryptPassword = ValueMix.decrypt(encryptPassword);
            Valid.notNull(decryptPassword, PASSWORD_DECRYPT_MESSAGE);
            row.setDecryptPassword(decryptPassword);
        }
        if (!Strings.isBlank(username)) {
            Valid.isTrue(!Strings.isBlank(row.getImportPassword()) || !Strings.isBlank(row.getDecryptPassword()), USERNAME_PASSWORD_ABSENT_MESSAGE);
        }
        if (!Strings.isBlank(description)) {
            Valid.validLengthLte(description, DESCRIPTION_MAX_LEN, DESCRIPTION_LEN_MESSAGE);
        }
    }
}
