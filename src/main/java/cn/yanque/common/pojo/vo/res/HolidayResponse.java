package cn.yanque.common.pojo.vo.res;

import cn.yanque.common.pojo.info.HolidayInfo;
import lombok.Data;

/**
 * 节假日API响应根对象
 */
@Data
public class HolidayResponse {
    private Integer code;
    private java.util.Map<String, HolidayInfo> holiday;
}