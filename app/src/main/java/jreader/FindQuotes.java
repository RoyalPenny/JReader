package jreader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.microsoft.cognitiveservices.speech.VoiceInfo;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Dictionaries.Gender;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class FindQuotes {
    private final StanfordCoreNLP pipeline;
    private final QuoteMap quoteMap;
    private final SpeechSynthesis speechSynthesiser;


        // Constructor to initialize CoreNLP pipeline
    public FindQuotes() {
        Properties props = new Properties();
    
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,entitymentions,parse,depparse,coref,quote");
        props.setProperty("coref.algorithm", "neural");
    
        this.pipeline = new StanfordCoreNLP(props);
        this.quoteMap = new QuoteMap();
        this.speechSynthesiser = new SpeechSynthesis();
        speechSynthesiser.generateSpeakerLists();
    }

    // Method to process text and extract quotes

    public void processText(String text) {
        CoreDocument coreDocument = pipeline.processToCoreDocument(text);
        for (CoreSentence sentence : coreDocument.sentences()) {
            for(Tree tree : sentence.constituencyParse()){
                System.out.println("Constituency Parse Tree:");
                tree.pennPrint(); // Print tree in Penn Treebank format
            };

            // Get phrase structure
            TreebankLanguagePack tlp = new PennTreebankLanguagePack();
            System.out.println("\nBracketed Structure: " + tree);
        }
        Map<Integer, CorefChain> corefChains = coreDocument.corefChains();

        this.quoteMap.clearQuotes();
        this.quoteMap.clearAssociatedEntity();
        
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
     
            System.out.println("Entity: " + chain.getRepresentativeMention().mentionSpan + " || Gender: " + entityGender.toString());
            System.out.println("============");
            VoiceInfo voice;

            if(!this.quoteMap.getEntitiesHashMap().containsKey(chain.getRepresentativeMention().mentionSpan)){
                    voice = speechSynthesiser.getSpeakerVoice(entityGender);
                	this.quoteMap.addEntity((chain.getRepresentativeMention()).mentionSpan, voice);
            }

            for (CorefMention mention : chain.getMentionsInTextualOrder()){
                System.out.println(mention);
                System.out.println(mention.corefClusterID);
                System.out.println(mention.sentNum + ", " + mention.headIndex);

                this.quoteMap.addAssociatedEntity(Arrays.asList(mention.sentNum, mention.headIndex), chain.getRepresentativeMention().mentionSpan);
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

        //System.out.println("Entities Hash: " + this.quoteMap.getEntitiesHashMap());
        //System.out.println("Quotes Hash: " + this.quoteMap.getQuotesHashMap());
        //System.out.println("Voices Hash: " + this.quoteMap.getVoicesHashMap());
    }

    public SpeakerEntity getQuoteSpeaker(String quote){
        List<Integer> quoteIndex = this.quoteMap.getQuoteToIndex(quote);
        String entityName = this.quoteMap.getIndexToAssocciatedEntity(quoteIndex);
        SpeakerEntity entity = this.quoteMap.getEntityNameToEntity(entityName);
        VoiceInfo voice = entity.getVoice();
        System.out.println(quote + " is voiced by " + voice.getName());
        return entity;
    }

    public SpeakerEntity getNarrator(){
        SpeakerEntity narrator = new SpeakerEntity(speechSynthesiser.getSpeakerVoice(Gender.MALE), "narration-relaxed");
        return narrator;
    }
}
