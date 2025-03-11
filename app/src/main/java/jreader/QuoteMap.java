package jreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.coref.data.CorefChain.CorefMention;

public class QuoteMap {
    private final HashMap<String, List<Integer>> quotes;
    private HashMap<List<Integer>, CorefMention> mention;

    public QuoteMap(){ 
        this.quotes = new HashMap<>(); // Correct initialization
        this.mention = new HashMap<>(); // Correct initialization
    }

    public HashMap<String, List<Integer>> getQuotesHashMap() {
        return this.quotes;
    }

    public HashMap<List<Integer>, CorefMention> getMentionHashMap() {
        return this.mention;
    }

    public void addQuote(String quote, List<Integer> sentenceIndices) {
        this.quotes.put(quote.trim(), sentenceIndices);
    }

    public void addMention(List<Integer> sentenceIndices, CorefMention corefMention) {
        this.mention.put(sentenceIndices, corefMention);
    }

    public List<Integer> getQuoteToIndex(String quote) {
        quote = quote.trim();
        return this.quotes.get(quote);
    }

    public CorefMention getIndexToEntity(List<Integer> index) {
        return this.mention.get(index);
    }

    public List<Map.Entry<String, List<Integer>>> getAllQuoteEntries() {
        return new ArrayList<>(this.quotes.entrySet());
    }
}
