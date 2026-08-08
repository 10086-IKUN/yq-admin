package cn.yanque.models.loganalysis.service;

import cn.yanque.models.loganalysis.pojo.vo.req.LogSearchReq;
import cn.yanque.models.loganalysis.pojo.vo.res.LogSearchRes;

public interface LogAnalysisService {

    LogSearchRes search(LogSearchReq request);
}
