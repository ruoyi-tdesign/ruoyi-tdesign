package org.dromara.system.config.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.system.config.FieldConfig;
import org.dromara.system.config.FormRule;
import org.dromara.system.config.SupplierFieldConfigs;
import org.dromara.system.config.TemplateMode;

import java.util.List;

/**
 * 邮箱配置对象
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MailFieldConfigs extends SupplierFieldConfigs {
    /** SMTP服务器域名 */
    private FieldConfig<String> host;
    /** SMTP服务端口 */
    private FieldConfig<Integer> port;
    /** 是否需要用户名密码验证 */
    private FieldConfig<Boolean> auth;
    /** 用户名 */
    private FieldConfig<String> user;
    /** 密码 */
    private FieldConfig<String> pass;
    /** 发送方，遵循RFC-822标准 */
    private FieldConfig<String> from;
    /** 是否打开调试模式，调试模式会显示与邮件服务器通信过程，默认不开启 */
    private FieldConfig<Boolean> debug;
    /** 对于超长参数是否切分为多份，默认为false（国内邮箱附件不支持切分的附件名） */
    private FieldConfig<Boolean> splitlongparameters;
    /** 对于文件名是否使用UTF8编码，默认为true */
    private FieldConfig<Boolean> encodefilename;
    /** 使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。 */
    private FieldConfig<Boolean> starttlsEnable;
    /** 使用 SSL安全连接 */
    private FieldConfig<Boolean> sslEnable;
    /** SSL协议，多个协议用空格分隔 */
    private FieldConfig<String> sslProtocols;
    /** 指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字 */
    private FieldConfig<String> socketFactoryClass;
    /** 如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true */
    private FieldConfig<Boolean> socketFactoryFallback;
    /** 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口 */
    private FieldConfig<Integer> socketFactoryPort;
    /** SMTP超时时长，单位毫秒，缺省值不超时 */
    private FieldConfig<Long> timeout;
    /** Socket连接超时值，单位毫秒，缺省值不超时 */
    private FieldConfig<Long> connectionTimeout;
    /** Socket写出超时值，单位毫秒，缺省值不超时 */
    private FieldConfig<Long> writeTimeout;

    public MailFieldConfigs() {
        this.host = FieldConfig.<String>builder()
            .component("input")
            .name("SMTP服务器域名")
            .required(true)
            .build();
        this.port = FieldConfig.<Integer>builder()
            .value(465)
            .component("input-number")
            .name("SMTP服务端口")
            .required(true)
            .min(0)
            .max(65535)
            .build();
        this.auth = FieldConfig.<Boolean>builder()
            .value(false)
            .component("switch")
            .name("用户名密码验证")
            .required(true)
            .build();
        this.user = FieldConfig.<String>builder()
            .component("input")
            .name("用户名")
            .help("发件人邮箱地址（如果使用foxmail邮箱，此处为qq号）")
            .required(true)
            .visible("auth")
            .build();
        this.pass = FieldConfig.<String>builder()
            .component("input")
            .name("密码")
            .help("注意，某些邮箱需要为SMTP服务单独设置密码，详情查看相关帮助")
            .required(true)
            .visible("auth")
            .type("password")
            .build();
        this.from = FieldConfig.<String>builder()
            .component("input")
            .name("发送方")
            .help("邮件中显示的发件人姓名，遵循RFC-822标准。支持以下形式：1.user@xxx.xx 2.name&nbsp;&lt;user@xxx.xx&gt;")
            .rules(List.of(
                FormRule.builder()
                    .pattern("^([^ \f\n\r\t\\v<]+@[^ \f\n\r\t\\v<>]+\\.[^ \f\n\r\t\\v>]+$)|(\\S+ <[^ \f\n\r\t\\v<]+@[^ \f\n\r\t\\v<>]+\\.[^ \f\n\r\t\\v>]+>$)")
                    .message("发送方格式错误")
                    .trigger("change")
                    .build())
            )
            .required(true)
            .build();
        this.debug = FieldConfig.<Boolean>builder()
            .value(false)
            .component("switch")
            .name("调试模式")
            .help("是否打开调试模式，调试模式会显示与邮件服务器通信过程，默认不开启")
            .required(true)
            .build();
        this.splitlongparameters = FieldConfig.<Boolean>builder()
            .value(false)
            .component("switch")
            .name("超长参数是否切分")
            .help("对于超长参数是否切分为多份，默认为false（国内邮箱附件不支持切分的附件名）")
            .required(true)
            .build();
        this.encodefilename = FieldConfig.<Boolean>builder()
            .value(true)
            .component("switch")
            .name("使用charset编码")
            .help("对于文件名是否使用charset编码，默认为 true")
            .required(true)
            .build();
        this.starttlsEnable = FieldConfig.<Boolean>builder()
            .value(true)
            .component("switch")
            .name("STARTTLS安全连接")
            .help("使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。")
            .required(true)
            .build();
        this.sslEnable = FieldConfig.<Boolean>builder()
            .value(true)
            .component("switch")
            .name("SSL安全连接")
            .required(true)
            .build();
        this.sslProtocols = FieldConfig.<String>builder()
            .component("input")
            .name("SSL协议")
            .help("SSL协议，多个协议用空格分隔")
            .required(false)
            .build();
        this.socketFactoryClass = FieldConfig.<String>builder()
            .value("javax.net.ssl.SSLSocketFactory")
            .component("input")
            .name("socketFactoryClass")
            .help("指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字")
            .required(true)
            .build();
        this.socketFactoryFallback = FieldConfig.<Boolean>builder()
            .value(true)
            .component("switch")
            .name("创建套接字工厂类")
            .help("如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true")
            .required(true)
            .build();
        this.socketFactoryPort = FieldConfig.<Integer>builder()
            .value(465)
            .component("input-number")
            .name("Socket端口")
            .help("指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口")
            .required(true)
            .min(0)
            .max(65535)
            .build();
        this.timeout = FieldConfig.<Long>builder()
            .component("input-number")
            .name("SMTP超时时长")
            .help("SMTP超时时长，单位毫秒，缺省值不超时")
            .required(false)
            .min(-1L)
            .build();
        this.connectionTimeout = FieldConfig.<Long>builder()
            .component("input-number")
            .name("Socket连接超时值")
            .help("Socket连接超时值，单位毫秒，缺省值不超时")
            .required(false)
            .min(-1L)
            .build();
        this.writeTimeout = FieldConfig.<Long>builder()
            .component("input-number")
            .name("Socket写出超时值")
            .help("Socket写出超时值，单位毫秒，缺省值不超时")
            .required(false)
            .min(-1L)
            .build();
    }

    /** 获取消息类型 */
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.MAIL;
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(false, true);
    }
}
