package jreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuoteMap {
    private final HashMap<String, List<Integer>> quotes;
    private HashMap<List<Integer>, String> voices;

    public QuoteMap(){ 
        this.quotes = new HashMap<>(); // Correct initialization
        this.voices = new HashMap<>(); // Correct initialization
    }

    public HashMap<String, List<Integer>> getQuotesHashMap() {
        return this.quotes;
    }

    public HashMap<List<Integer>, String> getVoicesHashMap() {
        return this.voices;
    }

    public void addQuote(String quote, List<Integer> sentenceIndices) {
        this.quotes.put(quote.trim(), sentenceIndices);
    }

    public void addVoice(List<Integer> sentenceIndices, String voice) {
        this.voices.put(sentenceIndices, voice);
    }

    public List<Integer> getQuoteToIndex(String quote) {
        quote = quote.trim();
        return this.quotes.get(quote);
    }

    public String getIndexToVoice(List<Integer> index) {
        return this.voices.get(index);
    }

    public List<Map.Entry<String, List<Integer>>> getAllQuoteEntries() {
        return new ArrayList<>(this.quotes.entrySet());
    }
}
