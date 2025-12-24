package com.misyakuji;

import org.jasypt.util.text.AES256TextEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 生成加密数据库密码的测试类
 */
@SpringBootTest
class JasyptEncryptTests {

    // 测试方法：生成加密密码（直接运行该方法即可输出结果）
    @Test
    void generateEncryptedDbPassword() {
        // 1. 配置项：可根据需要修改
        String encryptKey = "SecretKey_jasper_mariadb"; // 加密密钥（后续需用该密钥解密，务必妥善保管）
        String plainDbPassword = "jasper_pass"; // 你的数据库明文密码（替换为实际密码）

        // 2. 创建 AES 加密器（与Spring Boot中Jasypt默认算法兼容）
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(encryptKey); // 设置加密密钥

        // 3. 生成加密密文
        String encryptedPassword = encryptor.encrypt(plainDbPassword);
        // 4. 解密验证（可选，用于确认密文有效性）
        String decryptedPassword = encryptor.decrypt(encryptedPassword);

        // 5. 打印结果（复制 ENC(加密密文) 到配置文件即可）
        System.out.println("=========== 数据库密码加密结果 ===========");
        System.out.println("加密密钥（后续需保留，用于解密）：" + encryptKey);
        System.out.println("数据库明文密码：" + plainDbPassword);
        System.out.println("加密后的密文：" + encryptedPassword);
        System.out.println("配置文件中需填写：ENC(" + encryptedPassword + ")");
        System.out.println("解密验证结果：" + decryptedPassword);
        System.out.println("========================================");
    }

    @Test
    void contextLoads() {
    }

}