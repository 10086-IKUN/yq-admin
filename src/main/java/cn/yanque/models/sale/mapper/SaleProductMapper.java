package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SaleProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper

/**
 * 销售产品表访问接口。
 *
 * <p>管理端可按关键词分页维护产品，学生端使用 selectAll 获取可购买产品列表。</p>
 */
public interface SaleProductMapper {

    void insert(SaleProductEntity entity);

    void updateById(SaleProductEntity entity);

    SaleProductEntity selectById(@Param("id") Long id);

    List<SaleProductEntity> selectAll();

    List<SaleProductEntity> selectPage(@Param("keyword") String keyword);
}
