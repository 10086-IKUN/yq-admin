package cn.yanque.models.exam.pojo.vo.res;

import cn.yanque.models.exam.pojo.entity.ExamPaperEntity;
import cn.yanque.models.exam.pojo.entity.ExamPaperQuestionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 试卷详情响应。
 */
@Data
@Schema(description = "试卷详情")
public class ExamPaperDetailRes {

    /** 试卷基础信息。 */
    @Schema(description = "试卷信息")
    private ExamPaperEntity paper;

    /** 试卷题目列表。 */
    @Schema(description = "试卷题目")
    private List<ExamPaperQuestionEntity> questions;
}
