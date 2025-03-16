package org.dromara.system.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.common.core.enums.NormalDisableEnum;
import org.dromara.common.core.enums.YesNoEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.OssRuleService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.spring.SpringExpressionCreated;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.oss.constant.OssConstant;
import org.dromara.common.redis.utils.RedisLockUtil;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.system.domain.SysOssRule;
import org.dromara.system.domain.bo.SysOssRuleBo;
import org.dromara.system.domain.query.SysOssRuleQuery;
import org.dromara.system.domain.vo.SysOssRuleVo;
import org.dromara.system.mapper.SysOssRuleMapper;
import org.dromara.system.service.ISysOssRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OSS处理规则Service业务层处理
 *
 * @author hexm
 * @date 2023-05-05
 */
@Service
public class SysOssRuleServiceImpl extends ServiceImpl<SysOssRuleMapper, SysOssRule> implements ISysOssRuleService, OssRuleService {

    /**
     * 查询OSS处理规则
     *
     * @param ossRuleId 主键
     * @return SysOssRuleVo
     */
    @Override
    public SysOssRuleVo queryById(Long ossRuleId) {
        return baseMapper.selectVoById(ossRuleId);
    }

    /**
     * 查询OSS处理规则列表
     *
     * @param query 查询对象
     * @return SysOssRuleVo
     */
    @Override
    public TableDataInfo<SysOssRuleVo> queryPageList(SysOssRuleQuery query) {
        return PageQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 查询OSS处理规则列表
     *
     * @param query 查询对象
     * @return SysOssRuleVo
     */
    @Override
    public List<SysOssRuleVo> queryList(SysOssRuleQuery query) {
        return baseMapper.queryList(query);
    }

    /**
     * 根据新增业务对象插入OSS处理规则
     *
     * @param bo OSS处理规则新增业务对象
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysOssRuleBo bo) {
        checkRepeat(bo);
        SysOssRule add = MapstructUtils.convert(bo, SysOssRule.class);
        add.setIsOverwrite(YesNoEnum.NO.getCodeStr());
        boolean save = save(add);
        removeCache();
        return save;
    }

    /**
     * 根据编辑业务对象修改OSS处理规则
     *
     * @param bo OSS处理规则编辑业务对象
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysOssRuleBo bo) {
        checkRepeat(bo);
        SysOssRule rule = getById(bo.getOssRuleId());
        SysOssRule update = MapstructUtils.convert(bo, SysOssRule.class);
        // 从非默认改为默认的过程，需要将覆盖字段值关闭
        if (YesNoEnum.NO.getCodeStr().equals(rule.getIsDefault())
            && YesNoEnum.YES.getCodeStr().equals(update.getIsDefault())) {
            update.setIsOverwrite(YesNoEnum.NO.getCodeStr());
        }
        boolean b = updateById(update);
        removeCache();
        return b;
    }

    /**
     * 校验并批量删除OSS处理规则信息
     *
     * @param ids 主键集合
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        boolean b = removeByIds(ids);
        removeCache();
        return b;
    }

    /**
     * 删除缓存
     */
    @Override
    public void removeCache() {
        RedisUtils.deleteObject(OssConstant.OSS_RULE_KEY);
    }

    /**
     * 规则覆盖字段值修改
     *
     * @param ossRuleId   规则id
     * @param isOverwrite 是否覆盖字段值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOverwrite(Long ossRuleId, String isOverwrite) {
        SysOssRule rule = getById(ossRuleId);
        // 当前覆盖字段值，并且是默认规则
        if (YesNoEnum.YES.getCodeStr().equals(isOverwrite)
            && YesNoEnum.YES.getCodeStr().equals(rule.getIsDefault())) {
            // 设置其他相同域名并且为默认的规则为关闭覆盖字段值
            lambdaUpdate()
                .eq(SysOssRule::getDomain, rule.getDomain())
                .eq(SysOssRule::getIsDefault, YesNoEnum.YES.getCodeStr())
                .set(SysOssRule::getIsOverwrite, YesNoEnum.NO.getCodeStr())
                .update();
        }
        // 设置当前规则
        lambdaUpdate().eq(SysOssRule::getOssRuleId, ossRuleId)
            .set(SysOssRule::getIsOverwrite, isOverwrite)
            .update();
        removeCache();
    }

    /**
     * 检查重复规则
     *
     * @param bo 业务对象
     */
    private void checkRepeat(SysOssRuleBo bo) {
        boolean exists = lambdaQuery()
            .ne(bo.getOssRuleId() != null, SysOssRule::getOssRuleId, bo.getOssRuleId())
            .eq(SysOssRule::getRuleName, bo.getRuleName())
            .eq(SysOssRule::getDomain, bo.getDomain())
            .exists();
        if (exists) {
            throw new ServiceException("该域名下已存在相同的规则");
        }
    }

    /**
     * 返回多字段url
     *
     * @param fieldName   字段名称
     * @param originalUrl url
     * @param ruleNames   限定使用规则，为空则不限制
     * @param join        字段与规则的连接符
     * @param useDefault  指定规则时，是否使用默认规则。 指定的规则不存在时，即使是false也将使用默认规则
     * @return
     */
    @Override
    @SuppressWarnings("unchecked cast")
    public Map<String, String> getUrls(String fieldName, String originalUrl, String[] ruleNames, String join, boolean useDefault) {
        // 二级缓存
        List<SysOssRule> rules = null;
        if (SpringMVCUtil.isWeb()) {
            rules = (List<SysOssRule>) SaHolder.getStorage().get(OssConstant.OSS_RULE_KEY);
        }
        if (rules == null) {
            // 如果二级缓存为空，则从redis缓存中读取
            rules = RedisLockUtil.getOrSaveList(OssConstant.OSS_RULE_KEY, () ->
                // 如果redis中为空，则从database中读取
                lambdaQuery()
                    .eq(SysOssRule::getStatus, NormalDisableEnum.NORMAL.getCode())
                    .orderByAsc(SysOssRule::getRuleSort)
                    .orderByAsc(SysOssRule::getCreateTime)
                    .list()
            );
            if (SpringMVCUtil.isWeb()) {
                SaHolder.getStorage().set(OssConstant.OSS_RULE_KEY, rules);
            }
        }
        Map<String, String> result = new LinkedHashMap<>();
        // 多个url拆分
        String[] urls = splitUrl(originalUrl);
        boolean existRules = ArrayUtil.isNotEmpty(ruleNames);
        for (String url : urls) {
            String fileName = getFileName(originalUrl);
            String mimeType = FileUtil.getMimeType(fileName);
            // 过滤命中的规则
            List<SysOssRule> list = rules.stream()
                .filter(sysOssRule -> {
                    if (!originalUrl.contains(sysOssRule.getDomain())) {
                        return false;
                    }
                    String[] mimeTypes = sysOssRule.getMimeType().split(",");
                    if (!StrUtil.containsAny(mimeType, mimeTypes) && !StrUtil.endWithAny(fileName, mimeTypes)) {
                        return false;
                    }
                    if (!existRules || useDefault) {
                        return YesNoEnum.YES.getCodeStr().equals(sysOssRule.getIsDefault());
                    }
                    return ArrayUtil.contains(ruleNames, sysOssRule.getRuleName());
                }).toList();

            // 设置覆盖默认字段值
            Optional<SysOssRule> overwriteRuleOpt = StreamUtils.findFirstOptional(list, o -> YesNoEnum.YES.getCodeStr().equals(o.getIsOverwrite()));
            String realUrl = url;
            // 如果存在覆盖默认字段值规则，则覆盖默认值
            HashMap<String, Object> urlVariable = getUrlVariable(url);
            if (overwriteRuleOpt.isPresent()) {
                realUrl = getRealUrl(overwriteRuleOpt.get().getRule(), urlVariable);
            }
            result.merge(fieldName, realUrl, (s, s2) -> String.join(",", s, s2));

            // 设置规则值
            List<SysOssRule> noOverwriteRule = list
                .stream()
                .filter(ossRule -> YesNoEnum.NO.getCodeStr().equals(ossRule.getIsOverwrite()))
                .toList();
            for (SysOssRule rule : noOverwriteRule) {
                String key = fieldName + join + rule.getRuleName();
                realUrl = getRealUrl(rule.getRule(), urlVariable);
                result.merge(key, realUrl, (s, s2) -> String.join(",", s, s2));
            }
        }
        return result;
    }

    /**
     * 得到渲染后的url
     *
     * @param rule        规则
     * @param urlVariable
     * @return
     */
    private String getRealUrl(String rule, HashMap<String, Object> urlVariable) {
        if (rule.contains("#{") && rule.contains("}")) {
            return SpringExpressionCreated.createSimpleTemplate(urlVariable).getValueString(rule);
        }
        return SpringExpressionCreated.createSimple(urlVariable).getValueString(rule);
    }

    private static HashMap<String, Object> getUrlVariable(String url) {
        // 解析url
        UrlBuilder builder = UrlBuilder.ofHttp(url, CharsetUtil.CHARSET_UTF_8);
        HashMap<String, Object> variable = new HashMap<>(8);
        String domain = builder.getScheme() + "://" + builder.getHost();
        if (builder.getPort() > 0) {
            domain += ":" + builder.getPort();
        }
        variable.put("domain", domain);
        List<String> segments = builder.getPath().getSegments();
        variable.put("path", String.join("/", segments.subList(0, segments.size() - 1)));
        variable.put("fullPath", builder.getPathStr());
        variable.put("filename", segments.get(segments.size() - 1));
        variable.put("url", domain + builder.getPathStr());
        variable.put("fullUrl", url);
        variable.put("query", builder.getQueryStr());
        return variable;
    }

    /**
     * 获取文件名称
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        // 解析url
        UrlBuilder builder = UrlBuilder.ofHttp(url, CharsetUtil.CHARSET_UTF_8);
        List<String> segments = builder.getPath().getSegments();
        return segments.isEmpty() ? null : segments.get(segments.size() - 1);
    }

    /**
     * 对原始url拆分
     *
     * @param url 原始url
     * @return
     */
    private String[] splitUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return new String[0];
        }
        return url.split(",(?=http)");
    }
}
