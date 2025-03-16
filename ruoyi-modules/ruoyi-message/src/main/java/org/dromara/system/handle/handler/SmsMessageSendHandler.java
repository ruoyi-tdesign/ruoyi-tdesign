package org.dromara.system.handle.handler;

import cn.hutool.core.util.StrUtil;
import org.dromara.common.core.enums.CommonStatusEnum;
import org.dromara.sms4j.qiniu.config.QiNiuConfig;
import org.dromara.sms4j.qiniu.config.QiNiuFactory;
import org.dromara.common.core.enums.SmsMessageSupplierType;
import org.dromara.common.core.enums.MessageTemplateMode;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.aliyun.config.AlibabaFactory;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.cloopen.config.CloopenConfig;
import org.dromara.sms4j.cloopen.config.CloopenFactory;
import org.dromara.sms4j.ctyun.config.CtyunConfig;
import org.dromara.sms4j.ctyun.config.CtyunFactory;
import org.dromara.sms4j.dingzhong.config.DingZhongConfig;
import org.dromara.sms4j.dingzhong.config.DingZhongFactory;
import org.dromara.sms4j.emay.config.EmayConfig;
import org.dromara.sms4j.emay.config.EmayFactory;
import org.dromara.sms4j.huawei.config.HuaweiConfig;
import org.dromara.sms4j.huawei.config.HuaweiFactory;
import org.dromara.sms4j.jdcloud.config.JdCloudConfig;
import org.dromara.sms4j.jdcloud.config.JdCloudFactory;
import org.dromara.sms4j.lianlu.config.LianLuConfig;
import org.dromara.sms4j.lianlu.config.LianLuFactory;
import org.dromara.sms4j.netease.config.NeteaseConfig;
import org.dromara.sms4j.netease.config.NeteaseFactory;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.tencent.config.TencentFactory;
import org.dromara.sms4j.unisms.config.UniConfig;
import org.dromara.sms4j.unisms.config.UniFactory;
import org.dromara.sms4j.yunpian.config.YunPianFactory;
import org.dromara.sms4j.yunpian.config.YunpianConfig;
import org.dromara.sms4j.zhutong.config.ZhutongConfig;
import org.dromara.sms4j.zhutong.config.ZhutongFactory;
import org.dromara.system.domain.SysMessageConfig;
import org.dromara.system.domain.SysMessageTemplate;
import org.dromara.system.handle.BaseMessageSendHandler;
import org.dromara.system.service.ISysMessageLogService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信消息发送处理类
 *
 * @author hexm
 * @date 2023/07/24 11:00
 */
@Component
public class SmsMessageSendHandler extends BaseMessageSendHandler {

    public SmsMessageSendHandler(ISysMessageLogService messageLogService) {
        super(messageLogService);
    }

    /**
     * 获取消息类型
     */
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.SMS;
    }

    /**
     * 发送消息
     *
     * @param account  账号
     * @param message  消息变量
     * @param template 模板对象
     * @param config   消息配置
     */
    @Override
    public void send(Collection<String> account, Map<String, Object> message, SysMessageTemplate template, SysMessageConfig config) {
        LinkedHashMap<String, String> outputVars = getOutputVars(template, message);
        String content = getContent(template, outputVars);
        SmsMessageSupplierType supplierType;
        try {
            supplierType = SmsMessageSupplierType.valueOf(config.getSupplierType());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("不支持的短信消息类型");
        }
        SmsBlend smsBlend = switch (supplierType) {
            case ALIBABA -> {
                AlibabaConfig alibabaConfig = JsonUtils.parseObject(config.getConfigJson(), AlibabaConfig.class);
                if (alibabaConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    alibabaConfig.setSignature(template.getSignature());
                }
                yield AlibabaFactory.instance().createSms(alibabaConfig);
            }
            case HUAWEI -> {
                HuaweiConfig huaweiConfig = JsonUtils.parseObject(config.getConfigJson(), HuaweiConfig.class);
                if (huaweiConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    huaweiConfig.setSignature(template.getSignature());
                }
                yield HuaweiFactory.instance().createSms(huaweiConfig);
            }
            case TENCENT -> {
                TencentConfig tencentConfig = JsonUtils.parseObject(config.getConfigJson(), TencentConfig.class);
                if (tencentConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    tencentConfig.setSignature(template.getSignature());
                }
                yield TencentFactory.instance().createSms(tencentConfig);
            }
            case YUNPIAN -> {
                YunpianConfig yunpianConfig = JsonUtils.parseObject(config.getConfigJson(), YunpianConfig.class);
                if (yunpianConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    yunpianConfig.setSignature(template.getSignature());
                }
                yield YunPianFactory.instance().createSms(yunpianConfig);
            }
            case UNI_SMS -> {
                UniConfig uniConfig = JsonUtils.parseObject(config.getConfigJson(), UniConfig.class);
                if (uniConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    uniConfig.setSignature(template.getSignature());
                }
                yield UniFactory.instance().createSms(uniConfig);
            }
            case JD_CLOUD -> {
                JdCloudConfig jdCloudConfig = JsonUtils.parseObject(config.getConfigJson(), JdCloudConfig.class);
                if (jdCloudConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    jdCloudConfig.setSignature(template.getSignature());
                }
                yield JdCloudFactory.instance().createSms(jdCloudConfig);
            }
            case CLOOPEN -> {
                CloopenConfig cloopenConfig = JsonUtils.parseObject(config.getConfigJson(), CloopenConfig.class);
                if (cloopenConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    cloopenConfig.setSignature(template.getSignature());
                }
                yield CloopenFactory.instance().createSms(cloopenConfig);
            }
            case EMAY -> {
                EmayConfig emayConfig = JsonUtils.parseObject(config.getConfigJson(), EmayConfig.class);
                if (emayConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    emayConfig.setSignature(template.getSignature());
                }
                yield EmayFactory.instance().createSms(emayConfig);
            }
            case CTYUN -> {
                CtyunConfig ctyunConfig = JsonUtils.parseObject(config.getConfigJson(), CtyunConfig.class);
                if (ctyunConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    ctyunConfig.setSignature(template.getSignature());
                }
                yield CtyunFactory.instance().createSms(ctyunConfig);
            }
            case NETEASE -> {
                NeteaseConfig neteaseConfig = JsonUtils.parseObject(config.getConfigJson(), NeteaseConfig.class);
                if (neteaseConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    neteaseConfig.setSignature(template.getSignature());
                }
                yield NeteaseFactory.instance().createSms(neteaseConfig);
            }
            case ZHUTONG -> {
                ZhutongConfig zhutongConfig = JsonUtils.parseObject(config.getConfigJson(), ZhutongConfig.class);
                if (zhutongConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    zhutongConfig.setSignature(template.getSignature());
                }
                yield ZhutongFactory.instance().createSms(zhutongConfig);
            }
            case DING_ZHONG -> {
                DingZhongConfig dingZhongConfig = JsonUtils.parseObject(config.getConfigJson(), DingZhongConfig.class);
                if (dingZhongConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    dingZhongConfig.setSignature(template.getSignature());
                }
                yield DingZhongFactory.instance().createSms(dingZhongConfig);
            }
            case LIAN_LU -> {
                LianLuConfig lianLuConfig = JsonUtils.parseObject(config.getConfigJson(), LianLuConfig.class);
                if (lianLuConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    lianLuConfig.setSignature(template.getSignature());
                }
                yield LianLuFactory.instance().createSms(lianLuConfig);
            }
            case QI_NIU -> {
                QiNiuConfig qiNiuConfig = JsonUtils.parseObject(config.getConfigJson(), QiNiuConfig.class);
                if (qiNiuConfig != null && StrUtil.isNotBlank(template.getSignature())) {
                    qiNiuConfig.setSignature(template.getSignature());
                }
                yield QiNiuFactory.instance().createSms(qiNiuConfig);
            }
        };
        MessageTemplateMode templateMode = MessageTemplateMode.valueOf(template.getTemplateMode());
        for (String mobile : account) {
            // 消息发送方式
            SmsResponse response;
            switch (templateMode) {
                case TEMPLATE_ID -> response = smsBlend.sendMessage(mobile, template.getTemplateId(), outputVars);
                case TEMPLATE_CONTENT -> response = smsBlend.sendMessage(mobile, content);
                default -> throw new ServiceException("不支持的消息模板模式：" + templateMode);
            }
            // 记录发送记录
            saveLog(mobile, template, config, content, log -> {
                log.setIsSuccess(response.isSuccess() ? CommonStatusEnum.SUCCESS.getCodeNum() : CommonStatusEnum.FAIL.getCodeNum());
                log.setResponseBody(StrUtil.maxLength(JsonUtils.toJsonString(response.getData()), 1000));
            });
        }
    }
}
