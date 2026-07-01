package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SaleProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SaleProductMapper {

    void insert(SaleProductEntity entity);

    void updateById(SaleProductEntity entity);

    SaleProductEntity selectById(@Param("id") Long id);

    List<SaleProductEntity> selectAll();

    List<SaleProductEntity> selectPage(@Param("keyword") String keyword);
}
