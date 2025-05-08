package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SignatureSmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 助通短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ZhutongSmsFieldConfigs extends SignatureSmsFieldConfigs {
    /** 模板变量名称 <a href="https://mix2.zthysms.com/index.html#/TemplateManagement">查看地址</a>： 允许为空，为空，使用无模板形式，发送短信 */
    private FieldConfig<String> templateName;
    /** 默认请求地址 不同区域，可切换请求地址，也可以不修改，<a href="https://doc.zthysms.com/web/#/1/236">请参考官方文档</a> */
    private FieldConfig<String> requestUrl;

    public ZhutongSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .useInput()
            .name("accessKey")
            .help("访问键标识")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .useInput()
            .name("accessKeySecret")
            .help("访问键秘钥")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.signature = FieldConfig.<String>builder()
            .useInput()
            .name("短信签名")
            .help("短信签名可以为空，为空发送【自定义短信】无需要提前创建短信模板; 不为空发送:【模板短信】")
            .required(false)
            .build();
        this.requestUrl = FieldConfig.<String>builder()
            .value("https://api.mix2.zthysms.com/")
            .useInput()
            .name("默认请求地址")
            .help("默认请求地址 不同区域，可切换请求地址，也可以不修改，请参考官方文档：https://doc.zthysms.com/web/#/1/236")
            .required(true)
            .build();
        this.templateName = FieldConfig.<String>builder()
            .useInput()
            .name("模板变量名称")
            .help("模板变量名称 查看地址：https://mix2.zthysms.com/index.html#/TemplateManagement 允许为空，为空，使用无模板形式，发送短信")
            .required(false)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, true);
    }
}
