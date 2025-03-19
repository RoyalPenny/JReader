package jreader;

import com.microsoft.cognitiveservices.speech.VoiceInfo;

public class SpeakerEntity {

    private VoiceInfo voice;
    private String style;

    public SpeakerEntity(VoiceInfo voice, String style) {
        this.voice = voice;
        this.style = style;
    }

    public VoiceInfo getVoice() {
        return voice;
    }

    public String getStyle() {
        return style;
    }

    public void setVoice(VoiceInfo voice) {
        this.voice = voice;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
