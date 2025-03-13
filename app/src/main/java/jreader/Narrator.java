package jreader;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Narrator {
    public Narrator(){

    }

    public void narrateText(String text) throws InterruptedException, ExecutionException{
        FindQuotes finder = new FindQuotes();  // Create an instance of FindQuotes
        SpeechSynthesis synthesiser = new SpeechSynthesis();
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        Pattern quotePattern = Pattern.compile("\"(.*?)\"");
        
        finder.processText(text);  // Call the method to process text
        finder.setNarrator();
        sentenceIterator.setText(text);

        int start = sentenceIterator.first();
        for (int end = sentenceIterator.next(); end != BreakIterator.DONE; start = end, end = sentenceIterator.next()) {
            String sentence = text.substring(start, end).trim();
            Matcher matcher = quotePattern.matcher(sentence);
            System.out.println("Original Sentence: " + sentence);

            int lastMatchEnd = 0; // Keeps track of where the last match ended

            while (matcher.find()) {
                int quoteStart = matcher.start();
                int quoteEnd = matcher.end();

                // Get the text before the quote
                String beforeQuote = sentence.substring(lastMatchEnd, quoteStart).trim();
                if (!beforeQuote.isEmpty()) {
                    System.out.println("Before Quote: " + beforeQuote);
                    synthesiser.GenerateTTS(beforeQuote, finder.getNarrator().getShortName());
                }

                // Extract the quote and process it
                String quote = matcher.group(1);
                System.out.println(" -> Quote: " + quote);
                synthesiser.GenerateTTS(quote, finder.getQuoteSpeaker("\"" + quote + "\"").getShortName());

                // Move past the quote
                lastMatchEnd = quoteEnd;
            }

            // Get the remaining part of the sentence after the last quote
            String afterQuote = sentence.substring(lastMatchEnd).trim();
            if (!afterQuote.isEmpty()) {
                System.out.println("After Quote: " + afterQuote);
                synthesiser.GenerateTTS(afterQuote, finder.getNarrator().getShortName());
            }

            System.out.println("-----"); // Separator for clarity
        
        /*for (VoiceInfo voice : speech.getNeutralSpeakers()) {
            System.out.println("Voice Name: " + voice.getName());
            System.out.println("Language: " + voice.getLocale());
            System.out.println("Gender: " + voice.getGender());
            System.out.println("======================");
        }*/
        }
    }
}
