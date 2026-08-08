package cn.yanque.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "doubao.realtime-voice")
public class DoubaoRealtimeVoiceProperties {

    /** 是否启用火山实时语音 WebSocket。未启用时只返回本地准备态，方便先联调前后端流程。 */
    private Boolean enabled = false;

    /** 火山实时语音 WebSocket 地址。 */
    private String websocketUrl;

    /** 火山控制台 App ID。 */
    private String appId;

    /** 火山控制台鉴权 Token 或 API Key。 */
    private String accessToken;

    /** 文档固定值：PlgvMymc7f3tQnJ6。 */
    private String appKey = "PlgvMymc7f3tQnJ6";

    /** 资源 ID，按火山实时语音控制台配置。 */
    private String resourceId = "volc.speech.dialog";

    /** 输入音频格式。前端会上传 16kHz 单声道 int16 little-endian PCM。 */
    private String inputAudioFormat = "pcm_s16le";

    private Integer inputAudioSampleRate = 16000;

    private Integer inputAudioChannel = 1;

    /** 输出音频格式。实时前端播放优先使用 PCM，避免 Ogg/Opus 容器起播丢字。 */
    private String outputAudioFormat = "pcm_s16le";

    private Integer outputAudioSampleRate = 24000;

    /** 端到端模型规范版本号：1.2.1.1 对应 O2.0，2.2.0.0 对应 SC2.0。 */
    private String modelVersion = "1.2.1.1";

    /** 交互模式，例如 push_to_talk。 */
    private String inputMod = "push_to_talk";

    /** 实时语音会话的开场白。 */
    private String helloContent = "你好，欢迎来参加今天的面试。先简单做个1分钟的自我介绍吧。";

    private String speaker = "zh_female_vv_jupiter_bigtts";

    private Integer connectTimeoutSeconds = 10;
}
