package cn.yanque.models.system.config.pojo.vo.res;

import lombok.Data;

@Data

/**
 * ConfigDetailRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class ConfigDetailRes {
    private Long id;
    private String k;
    private String v;
}
