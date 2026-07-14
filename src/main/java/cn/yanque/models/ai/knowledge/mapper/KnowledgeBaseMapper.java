package cn.yanque.models.ai.knowledge.mapper;

import cn.yanque.models.ai.knowledge.pojo.bo.KnowledgeBaseQueryBo;
import cn.yanque.models.ai.knowledge.pojo.entity.KnowledgeBaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {

    void insert(KnowledgeBaseEntity entity);

    int updateById(KnowledgeBaseEntity entity);

    int deleteById(@Param("id") Long id);

    KnowledgeBaseEntity selectById(@Param("id") Long id);

    KnowledgeBaseEntity selectByName(@Param("knowledgeBaseName") String knowledgeBaseName);

    KnowledgeBaseEntity selectByNameExcludeId(@Param("knowledgeBaseName") String knowledgeBaseName, @Param("id") Long id);

    List<KnowledgeBaseEntity> selectPage(KnowledgeBaseQueryBo query);
}
