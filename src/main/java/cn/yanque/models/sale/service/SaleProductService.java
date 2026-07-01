package cn.yanque.models.sale.service;

import cn.yanque.models.sale.entity.SaleProductEntity;
import java.util.List;

public interface SaleProductService {

    void create(SaleProductEntity entity);

    void update(SaleProductEntity entity);

    SaleProductEntity getById(Long id);

    List<SaleProductEntity> list(String keyword);

    List<SaleProductEntity> listAll();
}
