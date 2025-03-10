package jreader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.SynthesisVoicesResult;
import com.microsoft.cognitiveservices.speech.VoiceInfo;

public class SpeechSynthesis {
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static final String speechKey = System.getenv("LANGUAGE_KEY");
    private static final String speechRegion = System.getenv("LANGUAGE_REGION");
    private List<VoiceInfo> maleSpeakers;
    private List<VoiceInfo> femaleSpeakers;
    private List<VoiceInfo> neutralSpeakers;

    public SpeechSynthesis() {
        this.maleSpeakers = new ArrayList<>();
        this.femaleSpeakers = new ArrayList<>();
        this.neutralSpeakers = new ArrayList<>();

    }

    public void GenerateTTS(String text, String speaker) throws InterruptedException, ExecutionException {

        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);

        speechConfig.setSpeechSynthesisVoiceName(speaker); 

         
        try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig)) {
            if (text.isEmpty())
            {
                return;
            }

            SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();

            if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Speech synthesized to speaker for text [" + text + "]");
            }
            else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    System.out.println("CANCELED: Did you set the speech resource key and region values?");
                }
            }
        }
    }

    public void getSpeakers(){
        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
             SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig)) {

            // Retrieve available voices
            SynthesisVoicesResult synthesisVoicesResult = speechSynthesizer.getVoicesAsync().get();

            for (VoiceInfo voice : synthesisVoicesResult.getVoices()) {
                if (voice.getLocale().matches("^en-[A-Za-z]{2}$")) {
                    if (voice.getGender().toString().matches("Male")){
                        maleSpeakers.add(voice);
                    } else if(voice.getGender().toString().matches("Female")) {
                        femaleSpeakers.add(voice);
                    } else {
                        neutralSpeakers.add(voice);
                    }
                }
                
            }

        } catch (Exception e) {
            System.out.println("Failed to get speakers");
        }
    }

    public List<VoiceInfo> getMaleSpeakers(){
        return maleSpeakers;
    }

    public List<VoiceInfo> getFemaleSpeakers(){
        return femaleSpeakers;
    }

    public List<VoiceInfo> getNeutralSpeakers(){
        return neutralSpeakers;
    }
}
