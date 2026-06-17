package cn.yanque.models.studentFront.util;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.service.StudentFrontService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 学员端认证工具类
 * 从请求中获取学员信息
 */
@Component
public class StudentAuthUtil {

    private static StudentFrontService studentFrontService;

    @Autowired
    public void setStudentFrontService(StudentFrontService studentFrontService) {
        StudentAuthUtil.studentFrontService = studentFrontService;
    }

    /**
     * 获取当前登录学员ID
     * @param request HTTP请求
     * @return 学员ID
     */
    public static Long getStudentId(HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("studentId");
        if (studentId == null) {
            throw new BusinessException(401, "未登录");
        }
        return studentId;
    }

    /**
     * 获取当前登录学员编号
     * @param request HTTP请求
     * @return 学员编号
     */
    public static String getStudentNo(HttpServletRequest request) {
        Long studentId = getStudentId(request);
        EduStudentEntity student = studentFrontService.getStudentById(studentId);
        if (student == null) {
            throw new BusinessException(404, "学员不存在");
        }
        return student.getStudentCode();
    }

    /**
     * 获取当前登录学员姓名
     * @param request HTTP请求
     * @return 学员姓名
     */
    public static String getStudentName(HttpServletRequest request) {
        Long studentId = getStudentId(request);
        EduStudentEntity student = studentFrontService.getStudentById(studentId);
        if (student == null) {
            throw new BusinessException(404, "学员不存在");
        }
        return student.getStudentName();
    }
}
