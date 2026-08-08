package cn.yanque.models.ai.texttosql.mapper;

import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlEvalRunQueryBo;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TextToSqlEvalRunMapper {

    void insert(TextToSqlEvalRunEntity entity);

    int updateById(TextToSqlEvalRunEntity entity);

    int updateSummaryById(@Param("entity") TextToSqlEvalRunEntity entity,
                          @Param("clearFinishedAt") boolean clearFinishedAt);

    TextToSqlEvalRunEntity selectById(@Param("id") Long id);

    List<TextToSqlEvalRunEntity> selectPage(TextToSqlEvalRunQueryBo query);
}

