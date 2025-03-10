package jreader;

import java.util.HashMap;
import java.util.List;

public class QuoteMap {
    private HashMap<String, List<String>> quotes;

    public QuoteMap(){ 
        this.quotes = new HashMap<>(); // Correct initialization
    }

    public HashMap<String, List<String>> getHashMap() {
        return this.quotes;
    }
}
