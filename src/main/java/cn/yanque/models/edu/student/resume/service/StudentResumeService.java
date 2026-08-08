package cn.yanque.models.edu.student.resume.service;

import cn.yanque.models.edu.student.resume.pojo.StudentResumeDtos;

public interface StudentResumeService {
    StudentResumeDtos.Info get(Long studentId);

    StudentResumeDtos.Info save(Long studentId, StudentResumeDtos.SaveReq req);

    StudentResumeDtos.Info retry(Long studentId);

    String previewUrl(Long studentId);

    String downloadUrl(Long studentId);
}
