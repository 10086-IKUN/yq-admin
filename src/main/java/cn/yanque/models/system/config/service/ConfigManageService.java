package cn.yanque.models.system.config.service;

import cn.yanque.models.system.config.pojo.vo.req.ConfigCreateReq;
import cn.yanque.models.system.config.pojo.vo.req.ConfigUpdateReq;
import cn.yanque.models.system.config.pojo.vo.res.ConfigCreateRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigDeleteRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigDetailRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigPageRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigUpdateRes;

import java.util.List;


/**
 * ConfigManageService 服务接口。
 *
 * <p>定义对应模块对外暴露的业务能力，控制层和其他服务通过该接口调用。</p>
 */
public interface ConfigManageService {

    ConfigCreateRes addConfig(ConfigCreateReq req);

    ConfigUpdateRes updateConfig(ConfigUpdateReq req);

    ConfigDeleteRes deleteConfig(Long id);

    ConfigDetailRes getConfigById(Long id);

    List<ConfigPageRes> listAll();
}
