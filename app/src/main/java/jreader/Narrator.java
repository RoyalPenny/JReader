package jreader;

import java.text.BreakIterator;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Narrator {

    private SpeakerEntity narrator;
    private FindQuotes finder;
    
    public Narrator(){
        this.finder = new FindQuotes();  // Create an instance of FindQuotes
        this.narrator = finder.getNarrator();
    }

    public void narrateText(String text) throws InterruptedException, ExecutionException{
        SpeechSynthesis synthesiser = new SpeechSynthesis();
        
        Pattern quotePattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = quotePattern.matcher(text);

        BreakIterator wordIterator = BreakIterator.getWordInstance();

        finder.processText(text);  // Call the method to process text

        wordIterator.setText(text);

        StringBuilder sentenceBuilder = new StringBuilder();
        int start = wordIterator.first();
        boolean printQuote = true;

        for (int end = wordIterator.next(); end != BreakIterator.DONE; start = end, end = wordIterator.next()) {
            // Skip quoted text ranges
            boolean insideQuote = false;
            matcher.reset();

            while (matcher.find()) {  
                if (start > matcher.start() && end < matcher.end()) {
                    if(printQuote){
                        System.out.println("Quoted text: " + matcher.group());
                        synthesiser.GenerateTTS(matcher.group(), finder.getQuoteSpeaker(matcher.group()).getVoice().getShortName());
                        printQuote = false;
                    }
                    insideQuote = true;
                    break;
                }
            }

            if (!insideQuote) {
                printQuote = true;
                String word = text.substring(start, end);
                if (!word.isEmpty()) { 
                    if(!word.equals("\"")){
                        sentenceBuilder.append(word);
                    }
                    
                    // Stop at a period and print the collected sentence
                    if ((word.equals(".") || word.equals("\"")) && !sentenceBuilder.toString().trim().equals("")) {
                        System.out.println("Sentence: " + sentenceBuilder.toString().trim());
                        synthesiser.GenerateTTS(sentenceBuilder.toString().trim(), this.narrator.getVoice().getShortName(), this.narrator.getStyle());
                        sentenceBuilder.setLength(0); // Reset for the next sentence
                    }
                }
            }
        }

        // Print the last sentence if it doesn't end with a period
        if (sentenceBuilder.length() > 0) {
            System.out.println("Last sentence: " + sentenceBuilder.toString().trim());
        }

        synthesiser.getAudio().get();  // Wait for the audio to finish playing

            //synthesiser.GenerateTTS(beforeQuote, this.narrator.getShortName());

                // Extract the quote and process it
                
            //synthesiser.GenerateTTS(quote, finder.getQuoteSpeaker("\"" + quote + "\"").getShortName());

           
            
            //synthesiser.GenerateTTS(afterQuote, this.narrator.getShortName());
        
        /*for (VoiceInfo voice : speech.getNeutralSpeakers()) {
            System.out.println("Voice Name: " + voice.getName());
            System.out.println("Language: " + voice.getLocale());
            System.out.println("Gender: " + voice.getGender());
            System.out.println("======================");
        }*/
    }
}
