package cn.yanque.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor

/**
 * ActiveEnum 枚举定义。
 *
 * <p>用于约束业务代码中的固定取值，避免魔法字符串散落在各处。</p>
 */
public enum ActiveEnum {

    ACTIVE("生效"),

    INACTIVE("失效");

    @Getter
    private String desc;
}
