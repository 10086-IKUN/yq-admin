package cn.yanque.models.ai.texttosql.mapper;

import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlRunStepEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TextToSqlRunStepMapper {

    void insertBatch(@Param("list") List<TextToSqlRunStepEntity> list);

    int deleteByRunId(@Param("runId") Long runId);

    List<TextToSqlRunStepEntity> selectByRunId(@Param("runId") Long runId);
}

