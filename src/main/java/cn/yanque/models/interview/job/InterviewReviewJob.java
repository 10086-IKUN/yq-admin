package cn.yanque.models.interview.job;

import cn.yanque.models.interview.service.InterviewReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InterviewReviewJob {
    @Autowired
    private InterviewReviewService service;

    @Scheduled(fixedDelayString = "${doubao.asr.query-fixed-delay-ms:60000}")
    public void poll() {
        try {
            service.pollTranscribing();
        } catch (Exception ex) {
            log.warn("poll interview transcription failed", ex);
        }
    }
}
