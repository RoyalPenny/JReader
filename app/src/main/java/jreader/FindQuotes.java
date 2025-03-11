package jreader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Dictionaries.Gender;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class FindQuotes {
    private final StanfordCoreNLP pipeline;
    private QuoteMap quoteMap;

        // Constructor to initialize CoreNLP pipeline
    public FindQuotes() {
        Properties props = new Properties();
    
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,entitymentions,depparse,coref,quote");
    
        this.pipeline = new StanfordCoreNLP(props);
        this.quoteMap = new QuoteMap();

    }

    // Method to process text and extract quotes

    public void processText(String text) {
        CoreDocument coreDocument = pipeline.processToCoreDocument(text);

        Map<Integer, CorefChain> corefChains = coreDocument.corefChains();
        
        for (CorefChain chain : corefChains.values()) {        
            //chain.getMentionsWithSameHead(sentenceNumber, headIndex)
            Gender entityGender = Gender.UNKNOWN;
            for(CorefMention mention : chain.getMentionsInTextualOrder()){
                if(mention.gender.equals(Gender.FEMALE)){
                    entityGender = Gender.FEMALE;
                    break;
                }

                if(mention.gender.equals(Gender.MALE)){
                    entityGender = Gender.MALE;
                    break;
                }

                if(mention.gender.equals(Gender.NEUTRAL)){
                    entityGender = Gender.NEUTRAL;
                }
            }
     
            System.out.println("============");
            System.out.println("Entity: " + chain.getChainID() + " Gender: " + entityGender.toString());
            //ToDo
            //Set random azure voice based on gender

            for (CorefMention mention : chain.getMentionsInTextualOrder()){
                System.out.println(mention);
                System.out.println(mention.corefClusterID);
                System.out.println(mention.sentNum + ", " + mention.headIndex);
                this.quoteMap.addVoice(Arrays.asList(mention.sentNum, mention.headIndex), "Azure Synthesised Voice");
            }
        }

        System.out.println("xxxxxxxxxxxxxxxxxx");
        List<CoreQuote> coreQuotes = coreDocument.quotes();
        for(CoreQuote quote : coreQuotes){ 
            quote.speakerTokens().ifPresent(tokens -> {
                for(CoreLabel token : tokens){
                    System.out.println(token.toString());
                    System.out.println(token.sentIndex() + 1 + ", " + token.index());
                    System.out.println(quote.toString());
                    
                    String quoteString = quote.toString().trim();
                    this.quoteMap.addQuote(quoteString, Arrays.asList(token.sentIndex() + 1, token.index()));
                }
            });

        }
    }

    public String getQuoteSpeaker(String quote){
        List<Integer> quoteIndex = this.quoteMap.getQuoteToIndex(quote);
        String voice = this.quoteMap.getIndexToVoice(quoteIndex);
        
        System.out.println(quote + " is voiced by " + voice);
        return voice;
    }
}
