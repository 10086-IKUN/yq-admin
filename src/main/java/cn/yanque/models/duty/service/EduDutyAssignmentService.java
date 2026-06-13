package cn.yanque.models.duty.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.DutyAssignmentCreateReq;
import cn.yanque.common.pojo.vo.req.DutyAssignmentPageReq;
import cn.yanque.common.pojo.vo.req.DutyAssignmentUpdateReq;
import cn.yanque.common.pojo.vo.res.DutyAssignmentCreateRes;
import cn.yanque.common.pojo.vo.res.DutyAssignmentDeleteRes;
import cn.yanque.common.pojo.vo.res.DutyAssignmentDetailRes;
import cn.yanque.common.pojo.vo.res.DutyAssignmentPageRes;
import cn.yanque.common.pojo.vo.res.DutyAssignmentUpdateRes;

/**
 * 值班安排Service接口
 */
public interface EduDutyAssignmentService {

    DutyAssignmentCreateRes addDutyAssignment(DutyAssignmentCreateReq req);

    DutyAssignmentUpdateRes updateDutyAssignment(DutyAssignmentUpdateReq req);

    DutyAssignmentDeleteRes deleteDutyAssignment(Long id);

    DutyAssignmentDetailRes getDutyAssignmentById(Long id);

    PageResult<DutyAssignmentPageRes> pageDutyAssignment(DutyAssignmentPageReq req);
}
