package cn.yanque.models.studentFront.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学员端个人资料修改请求。
 *
 * <p>学员只能维护自己的展示资料，不能通过个人中心修改班级、课程、学习方式等业务绑定字段。
 * 这些绑定字段由管理端维护，避免前端提交空值时误清空数据库里的班级信息。</p>
 */
@Data
@Schema(description = "学员端个人资料修改请求")
public class StudentProfileUpdateReq {

    /** 学员姓名。 */
    @Schema(description = "学员姓名")
    private String studentName;

    /** 学校名称。 */
    @Schema(description = "学校名称")
    private String school;

    /** 学历。 */
    @Schema(description = "学历")
    private String education;
}
