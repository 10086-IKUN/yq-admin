package cn.yanque.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于抛出业务逻辑错误，包含错误码和错误信息
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public static final BusinessException UserExist = new BusinessException(10001, "用户已存在");
    public static final BusinessException UserNotExist = new BusinessException(10002, "用户不存在");
    public static final BusinessException PermissionExist = new BusinessException(11001, "权限已存在");
    public static final BusinessException PermissionNotExist = new BusinessException(11002, "权限不存在");
    public static final BusinessException PasswordError = new BusinessException(11003, "密码错误");
    public static final BusinessException DataError = new BusinessException(11003, "数据有误");
    public static final BusinessException RoleExist = new BusinessException(12001, "角色已存在");
    public static final BusinessException RoleNotExist = new BusinessException(12002, "角色不存在");
    public static final BusinessException ClassExist = new BusinessException(14001, "班级已存在");
    public static final BusinessException ClassNotExist = new BusinessException(14002, "班级不存在");
    public static final BusinessException CampusExist = new BusinessException(15001, "校区已存在");
    public static final BusinessException CampusNotExist = new BusinessException(15002, "校区不存在");
    public static final BusinessException StudentExist = new BusinessException(16001, "学员已存在");
    public static final BusinessException StudentNotExist = new BusinessException(16002, "学员不存在");
    public static final BusinessException CourseExist = new BusinessException(17001, "课程已存在");
    public static final BusinessException CourseNotExist = new BusinessException(17002, "课程不存在");
    public static final BusinessException CourseDetailExist = new BusinessException(18001, "课程详情已存在");
    public static final BusinessException CourseDetailNotExist = new BusinessException(18002, "课程详情不存在");



    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }


    public BusinessException newInstance(String message) {
        return new BusinessException(this.getCode(), message);
    }
}
