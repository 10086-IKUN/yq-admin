package cn.yanque.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StageNameEnum {

    BASIC("基础"),

    SPRING("spring"),

    PYTHON("python"),

    AI("ai");

    @Getter
    private final String desc;

    /**
     * 根据描述获取枚举值
     * @param desc 阶段描述（如："基础"、"spring"）
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果描述不存在
     */
    public static StageNameEnum fromDesc(String desc) {
        for (StageNameEnum stage : values()) {
            if (stage.getDesc().equals(desc)) {
                return stage;
            }
        }
        throw new IllegalArgumentException("无效的阶段名称: " + desc);
    }
}