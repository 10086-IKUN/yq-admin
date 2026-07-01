package cn.yanque.models.sale.service.impl;

import cn.yanque.models.sale.entity.SaleProductEntity;
import cn.yanque.models.sale.mapper.SaleProductMapper;
import cn.yanque.models.sale.service.SaleProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleProductServiceImpl implements SaleProductService {

    @Autowired
    private SaleProductMapper saleProductMapper;

    @Override
    public void create(SaleProductEntity entity) {
        saleProductMapper.insert(entity);
    }

    @Override
    public void update(SaleProductEntity entity) {
        saleProductMapper.updateById(entity);
    }

    @Override
    public SaleProductEntity getById(Long id) {
        return saleProductMapper.selectById(id);
    }

    @Override
    public List<SaleProductEntity> list(String keyword) {
        return saleProductMapper.selectPage(keyword);
    }

    @Override
    public List<SaleProductEntity> listAll() {
        return saleProductMapper.selectAll();
    }
}
