package cn.yanque.models.studentFront.client;

public interface DoubaoRealtimeVoiceEventListener {

    void onEvent(String voiceSessionId, String eventText);
}

