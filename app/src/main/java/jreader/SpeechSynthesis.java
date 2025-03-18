package jreader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.SynthesisVoicesResult;
import com.microsoft.cognitiveservices.speech.VoiceInfo;

import edu.stanford.nlp.coref.data.Dictionaries.Gender;

public class SpeechSynthesis {
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static final String speechKey = System.getenv("LANGUAGE_KEY");
    private static final String speechRegion = System.getenv("LANGUAGE_REGION");
    private List<VoiceInfo> maleSpeakers;
    private List<VoiceInfo> femaleSpeakers;
    private List<VoiceInfo> neutralSpeakers;
    private CompletableFuture<Void> audio;

    public SpeechSynthesis() {
        this.maleSpeakers = new ArrayList<>();
        this.femaleSpeakers = new ArrayList<>();
        this.neutralSpeakers = new ArrayList<>();
        this.audio = CompletableFuture.completedFuture(null);
    }

    public void GenerateTTS(String text, String speaker) throws InterruptedException, ExecutionException {
        GenerateTTS(text, speaker, "");
    }

    public void GenerateTTS(String text, String speaker, String style) throws InterruptedException, ExecutionException {

        SpeechSynthesisResult speechSynthesisResult;
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);

        speechConfig.setSpeechSynthesisVoiceName(speaker); 
         
        try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null)) {
            if (text.isEmpty())
            {
                return;
            }

            if(style.isBlank())
            {
                speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
            } else {
                String ssml = "<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' " +
                          "xmlns:mstts='http://www.w3.org/2001/mstts' xml:lang='en-US'>" +
                          "<voice name='" + speaker + "'>" +
                          "<mstts:express-as style='" + style + "'>" +
                          text +
                          "</mstts:express-as>" +
                          "</voice></speak>";

                // Use SpeakSsmlAsync to synthesize SSML input
                speechSynthesisResult = speechSynthesizer.SpeakSsmlAsync(ssml).get();
            }
            

            if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Speech synthesized to speaker for text [" + text + "]");
                byte[] audioData = speechSynthesisResult.getAudioData();
                this.audio.get();
                this.audio = playAudioAsync(audioData);
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

    public void generateSpeakerLists(){
        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
             SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig)) {

            // Retrieve available voices
            SynthesisVoicesResult synthesisVoicesResult = speechSynthesizer.getVoicesAsync().get();
            this.femaleSpeakers = new ArrayList<>();
            this.maleSpeakers = new ArrayList<>();
            this.neutralSpeakers = new ArrayList<>();

            for (VoiceInfo voice : synthesisVoicesResult.getVoices()) {
                if (voice.getLocale().matches("^en-[A-Za-z]{2}$")) {
                    if (voice.getGender().toString().matches("Male")){
                        this.maleSpeakers.add(voice);
                    } else if(voice.getGender().toString().matches("Female")) {
                        this.femaleSpeakers.add(voice);
                    } else {
                        this.neutralSpeakers.add(voice);
                    }
                }
                
            }
        } catch (Exception e) {
            System.out.println("Failed to get speakers");
        }
    }

    public List<VoiceInfo> getMaleSpeakers(){
        return this.maleSpeakers;
    }

    public List<VoiceInfo> getFemaleSpeakers(){
        return this.femaleSpeakers;
    }

    public List<VoiceInfo> getNeutralSpeakers(){
        return this.neutralSpeakers;
    }

    public VoiceInfo getSpeakerVoice(Gender gender){
        int index;
        VoiceInfo speaker;

        switch(gender){
            case Gender.MALE -> {
                index = ThreadLocalRandom.current().nextInt(this.maleSpeakers.size());
                speaker = this.maleSpeakers.get(index);
                this.maleSpeakers.remove(index);
                System.out.println("Voice set as Male");
            }
            case Gender.FEMALE -> {
                index = ThreadLocalRandom.current().nextInt(this.femaleSpeakers.size());
                speaker = this.femaleSpeakers.get(index);
                this.femaleSpeakers.remove(index);
                System.out.println("Voice set as Female");
            }
            default -> {
                index = ThreadLocalRandom.current().nextInt(this.neutralSpeakers.size());
                speaker = this.neutralSpeakers.get(index);
                System.out.println("Voice set as Neutral");
            } 
        }
        
        return speaker;
    }

    private CompletableFuture<Void> playAudioAsync(byte[] audioData) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Create an input stream from the audio data
                InputStream inputStream = new ByteArrayInputStream(audioData);
        
                // Create an audio stream from the input stream
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
        
                // Get the audio format
                AudioFormat format = audioStream.getFormat();
        
                // Get a line to play the audio
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
        
                // Open the audio line
                audioLine.open(format);
        
                // Start playing the audio
                audioLine.start();
        
                byte[] buffer = new byte[4096];
                int bytesRead;
        
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    audioLine.write(buffer, 0, bytesRead);
                }
        
                // Finish playing the audio
                audioLine.drain();
                audioLine.close();
                audioStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> getAudio(){
        return this.audio;
    }
}
