package org.dromara.common.translation.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.dto.FileDTO;
import org.dromara.common.core.service.FileService;
import org.dromara.common.core.utils.StreamUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 富文本值处理
 *
 * @author hexm
 * @date 2025/08/11
 */
@Slf4j
public class EditorValueHandler extends JsonSerializer<String> implements ContextualSerializer {
    private final static Pattern pattern = Pattern.compile("(src=\")[^\"]*(sid=(\\d+))[^\"]*?(\")");
    private static final FileService fileService = SpringUtil.getBean(FileService.class);

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            try {
                int groupId = 3;
                Matcher matcher = pattern.matcher(value);
                Set<Long> ids = matcher
                    .results()
                    .map(matchResult -> matchResult.group(groupId))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());

                if (CollUtil.isEmpty(ids)) {
                    gen.writeObject(value);
                } else {
                    List<FileDTO> dtoList = fileService.selectByIds(ids);
                    Map<Long, FileDTO> fileDTOMap = StreamUtils.toIdentityMap(dtoList, FileDTO::getFileId);

                    StringBuilder sb = new StringBuilder();
                    matcher.reset();
                    while (matcher.find()) {
                        long id = Long.parseLong(matcher.group(groupId));
                        FileDTO dto = fileDTOMap.get(id);
                        if (dto != null) {
                            String previewUrl = dto.getPreviewUrl();
                            if (previewUrl.contains("?")) {
                                int index = previewUrl.indexOf("?");
                                previewUrl = previewUrl.substring(0, index + 1) + "$2&" + previewUrl.substring(index + 1);
                            } else {
                                previewUrl += "?$2";
                            }
                            String replacement = "$1%s$4".formatted(previewUrl);
                            matcher.appendReplacement(sb, replacement);
                        }
                    }
                    matcher.appendTail(sb);

                    gen.writeObject(sb.toString());
                }
            } catch (Exception e) {
                gen.writeObject(value);
                log.error(e.getMessage(), e);
            }
        } else {
            gen.writeObject(null);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (Objects.equals(String.class, property.getType().getRawClass())) {
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
