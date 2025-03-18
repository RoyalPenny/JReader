package jreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.cognitiveservices.speech.VoiceInfo;

public class QuoteMap {
    private HashMap<String, List<Integer>> quotes;
    private HashMap<List<Integer>, String> associatedEntity;
    private final HashMap<String, VoiceInfo> entites;

    public QuoteMap() {
        this.quotes = new HashMap<>();
        this.associatedEntity = new HashMap<>();
        this.entites = new HashMap<>();
    }

    public HashMap<String, List<Integer>> getQuotesHashMap() {
        return this.quotes;
    }

    public HashMap<List<Integer>, String> getassociatedEntityHashMap() {
        return this.associatedEntity;
    }

    public HashMap<String, VoiceInfo> getEntitiesHashMap() {
        return this.entites;
    }

    public void addQuote(String quote, List<Integer> sentenceIndices) {
        this.quotes.put(quote.trim(), sentenceIndices);
    }

    public void addAssociatedEntity(List<Integer> sentenceIndices, String entity) {
        this.associatedEntity.put(sentenceIndices, entity);
    }

    public void addEntity(String entity, VoiceInfo voice) {
        this.entites.put(entity, voice);
    }

    public List<Integer> getQuoteToIndex(String quote) {
        quote = quote.trim();
        return this.quotes.get(quote);
    }

    public String getIndexToAssocciatedEntity(List<Integer> index) {
        return this.associatedEntity.get(index);
    }

    public VoiceInfo getEntityToVoice(String entity) {
        return this.entites.get(entity);
    }

    public void clearAssociatedEntity() {
        this.associatedEntity = new HashMap<>();
    }

    public void clearQuotes() {
        this.quotes = new HashMap<>();
    }

    public void changeVoice(String entity, VoiceInfo voice) {
        this.entites.put(entity, voice);
    }

    public List<Map.Entry<String, List<Integer>>> getAllQuoteEntries() {
        return new ArrayList<>(this.quotes.entrySet());
    }
}
