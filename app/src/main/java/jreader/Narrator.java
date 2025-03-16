package jreader;

import java.text.BreakIterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.cognitiveservices.speech.VoiceInfo;

public class Narrator {

    private VoiceInfo narrator;
    private static final Pattern quotePattern = Pattern.compile("\"([^\"]+)\"");
    public Narrator(){
        this.narrator = null;
    }

    public void narrateText(String text) throws InterruptedException, ExecutionException{
        FindQuotes finder = new FindQuotes();  // Create an instance of FindQuotes
        SpeechSynthesis synthesiser = new SpeechSynthesis();
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance();
        sentenceIterator.setText(text);

        boolean insideQuote = false;  // Track if we're inside a quote
        StringBuilder sentenceBuffer = new StringBuilder();

        int start = sentenceIterator.first();
        for (int end = sentenceIterator.next(); end != BreakIterator.DONE; start = end, end = sentenceIterator.next()) {
            String sentence = text.substring(start, end).trim();

            // Check if the sentence contains a quote
            Matcher matcher = quotePattern.matcher(sentence);

            if (matcher.find()) {
                insideQuote = !insideQuote;  // Toggle insideQuote on/off
            }

            if (insideQuote) {
                // If inside a quote, add to the buffer without breaking
                sentenceBuffer.append(" ").append(sentence);
            } else {
                // If not inside a quote, print the buffer (if any) and reset it
                if (sentenceBuffer.length() > 0) {
                    sentenceBuffer.append(" ").append(sentence);
                    System.out.println("Quote: " + sentenceBuffer.toString().trim());
                    sentenceBuffer.setLength(0);
                } else {
                    System.out.println("Sentence: " + sentence);
                }
            }
        }

            //synthesiser.GenerateTTS(beforeQuote, this.narrator.getShortName());

                // Extract the quote and process it
                
            //synthesiser.GenerateTTS(quote, finder.getQuoteSpeaker("\"" + quote + "\"").getShortName());

           
            
            //synthesiser.GenerateTTS(afterQuote, this.narrator.getShortName());


            System.out.println("-----"); // Separator for clarity
        
        /*for (VoiceInfo voice : speech.getNeutralSpeakers()) {
            System.out.println("Voice Name: " + voice.getName());
            System.out.println("Language: " + voice.getLocale());
            System.out.println("Gender: " + voice.getGender());
            System.out.println("======================");
        }*/
    }

    private static String extractQuotes(String text, List<String> extractedQuotes) {
        Matcher matcher = quotePattern.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            extractedQuotes.add(matcher.group()); // Store full quote
            matcher.appendReplacement(sb, "[QUOTE]"); // Replace with placeholder
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
