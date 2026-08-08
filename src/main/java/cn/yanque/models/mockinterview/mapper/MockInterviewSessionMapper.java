package cn.yanque.models.mockinterview.mapper;

import cn.yanque.models.mockinterview.pojo.entity.MockInterviewSessionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface MockInterviewSessionMapper {

    int insert(MockInterviewSessionEntity entity);

    MockInterviewSessionEntity selectById(@Param("id") Long id);

    MockInterviewSessionEntity selectLatestByStudentId(@Param("studentId") Long studentId);

    int updateProfileSuccess(@Param("id") Long id,
                             @Param("profileJson") String profileJson,
                             @Param("now") Date now);

    int updateProfileFailed(@Param("id") Long id,
                            @Param("errorMessage") String errorMessage,
                            @Param("now") Date now);

    int updateStarted(@Param("id") Long id,
                      @Param("studentId") Long studentId,
                      @Param("voiceSessionId") String voiceSessionId,
                      @Param("now") Date now);

    int updateFinished(@Param("id") Long id,
                       @Param("studentId") Long studentId,
                       @Param("now") Date now);
}

