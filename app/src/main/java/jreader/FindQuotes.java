package jreader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.microsoft.cognitiveservices.speech.VoiceInfo;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Dictionaries.Gender;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class FindQuotes {
    private final StanfordCoreNLP pipeline;
    private final QuoteMap quoteMap;
    private final SpeechSynthesis speechSynthesiser;
    private VoiceInfo narrator;

        // Constructor to initialize CoreNLP pipeline
    public FindQuotes() {
        Properties props = new Properties();
    
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,entitymentions,parse,depparse,coref,quote");
        props.setProperty("coref.algorithm", "neural");
    
        this.pipeline = new StanfordCoreNLP(props);
        this.quoteMap = new QuoteMap();
        this.speechSynthesiser = new SpeechSynthesis();
        this.narrator = null;
    }

    // Method to process text and extract quotes

    public void processText(String text) {
        CoreDocument coreDocument = pipeline.processToCoreDocument(text);

        Map<Integer, CorefChain> corefChains = coreDocument.corefChains();
        speechSynthesiser.generateSpeakerLists();
        
        for (CorefChain chain : corefChains.values()) {        
            //chain.getMentionsWithSameHead(sentenceNumber, headIndex)
            Gender entityGender = Gender.UNKNOWN;

            for(CorefMention mention : chain.getMentionsInTextualOrder()){
                if(mention.gender.equals(Gender.FEMALE)){
                    entityGender = Gender.FEMALE;
                }

                if(mention.gender.equals(Gender.MALE)){
                    entityGender = Gender.MALE;
                }

                if(mention.gender.equals(Gender.NEUTRAL)){
                    entityGender = Gender.NEUTRAL;
                }
            }
     
            System.out.println("============");
            System.out.println("Entity: " + chain.getChainID() + " Gender: " + entityGender.toString());
            //ToDo
            //Set random azure voice based on gender

            VoiceInfo voice = speechSynthesiser.getSpeakerVoice(entityGender);

            for (CorefMention mention : chain.getMentionsInTextualOrder()){
                System.out.println(mention);
                System.out.println(mention.corefClusterID);
                System.out.println(mention.sentNum + ", " + mention.headIndex);

                this.quoteMap.addVoice(Arrays.asList(mention.sentNum, mention.headIndex), voice);
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

    public VoiceInfo getQuoteSpeaker(String quote){
        List<Integer> quoteIndex = this.quoteMap.getQuoteToIndex(quote);
        VoiceInfo voice = this.quoteMap.getIndexToVoice(quoteIndex);
        System.out.println(quote + " is voiced by " + voice.getName());
        return voice;
    }

    public void setNarrator(){
        this.narrator = speechSynthesiser.getSpeakerVoice(Gender.MALE);
    }

    public VoiceInfo getNarrator(){
        return this.narrator;
    }
}
