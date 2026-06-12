package cn.yanque.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果包装类
 * @param <T> 分页数据类型
 */
/**
 * 分页查询结果封装
 * @param <T> 记录数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> records;
}
