package cn.yanque.common.pojo.info;

import lombok.Data;

/**
 * 节假日信息
 */
@Data
public class HolidayInfo {
    private Boolean holiday;
    private String name;
    private Boolean after;
    private String target;
}