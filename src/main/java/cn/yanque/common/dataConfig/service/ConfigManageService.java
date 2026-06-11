package cn.yanque.common.dataConfig.service;

import cn.yanque.common.pojo.vo.req.ConfigCreateReq;
import cn.yanque.common.pojo.vo.req.ConfigUpdateReq;
import cn.yanque.common.pojo.vo.res.ConfigCreateRes;
import cn.yanque.common.pojo.vo.res.ConfigDeleteRes;
import cn.yanque.common.pojo.vo.res.ConfigDetailRes;
import cn.yanque.common.pojo.vo.res.ConfigPageRes;
import cn.yanque.common.pojo.vo.res.ConfigUpdateRes;

import java.util.List;

public interface ConfigManageService {

    ConfigCreateRes addConfig(ConfigCreateReq req);

    ConfigUpdateRes updateConfig(ConfigUpdateReq req);

    ConfigDeleteRes deleteConfig(Long id);

    ConfigDetailRes getConfigById(Long id);

    List<ConfigPageRes> listAll();
}
