package cn.yanque.models.studentTag.service;

import cn.yanque.models.studentTag.pojo.entity.StudentTagEntity;
import cn.yanque.models.studentTag.pojo.vo.StudentTagVO;

import java.util.List;

public interface StudentTagService {

    /**
     * 计算单个学员标签
     */
    void calculateTag(Long studentId);

    /**
     * 批量计算所有学员标签
     */
    void calculateAllTags();

    /**
     * 根据学员ID查询标签
     */
    StudentTagEntity getByStudentId(Long studentId);

    /**
     * 查询标签列表（含学员信息）
     */
    List<StudentTagVO> list(String tagType, String keyword);

    /**
     * 班主任确认标签
     */
    void confirm(Long id, Long confirmedBy, String tagType);

    /**
     * 查询某教师班级下所有学员标签
     */
    List<StudentTagVO> listByTeacherId(Long teacherId);
}
