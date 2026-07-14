package cn.yanque.models.ai.knowledge.mapper;

import cn.yanque.models.ai.knowledge.pojo.bo.KnowledgeDocumentQueryBo;
import cn.yanque.models.ai.knowledge.pojo.entity.KnowledgeDocumentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeDocumentMapper {

    void insert(KnowledgeDocumentEntity entity);

    int updateIndexing(@Param("id") Long id, @Param("updatedAt") java.util.Date updatedAt);

    int updateIndexed(KnowledgeDocumentEntity entity);

    int updateFailed(KnowledgeDocumentEntity entity);

    int deleteById(@Param("id") Long id);

    KnowledgeDocumentEntity selectById(@Param("id") Long id);

    List<KnowledgeDocumentEntity> selectPage(KnowledgeDocumentQueryBo query);

    int countByKnowledgeBaseId(@Param("knowledgeBaseId") Long knowledgeBaseId);
}
