package jreader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
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

        /* 
        for(CoreEntityMention mention : coreDocument.entityMentions()){
            for(CoreLabel token : mention.tokens()){
                System.out.println(token.toString());
                System.out.println(token.sentIndex() + 1 + ", " + token.index());
            }
        }
        */

        Map<Integer, CorefChain> corefChains = coreDocument.corefChains();
        
        for (CorefChain chain : corefChains.values()) {
            System.out.println("============");
            System.out.println("Entity " + chain.getChainID());
            
            //chain.getMentionsWithSameHead(sentenceNumber, headIndex)


            for (CorefMention mention : chain.getMentionsInTextualOrder()){
                System.out.println(mention);
                System.out.println(mention.gender);
                System.out.println(mention.sentNum + ", " + mention.headIndex);
                this.quoteMap.addMention(Arrays.asList(mention.sentNum, mention.headIndex), mention);
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
                    this.quoteMap.addQuote(quote.toString(), Arrays.asList(token.sentIndex() + 1, token.index()));
                }
            });

        }
    }

    public void getQuoteSpeaker(){
        System.out.println(this.quoteMap.getQuoteIndex("I will be there soon."));
    }
}
