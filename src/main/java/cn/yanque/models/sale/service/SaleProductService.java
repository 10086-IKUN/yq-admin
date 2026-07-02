package cn.yanque.models.sale.service;

import cn.yanque.models.sale.entity.SaleProductEntity;
import java.util.List;


/**
 * SaleProductService 服务接口。
 *
 * <p>定义对应模块对外暴露的业务能力，控制层和其他服务通过该接口调用。</p>
 */
public interface SaleProductService {

    void create(SaleProductEntity entity);

    void update(SaleProductEntity entity);

    SaleProductEntity getById(Long id);

    List<SaleProductEntity> list(String keyword);

    List<SaleProductEntity> listAll();
}
