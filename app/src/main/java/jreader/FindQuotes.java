package jreader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.QuoteAttributionAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class FindQuotes {
    private StanfordCoreNLP pipeline;
    private List<String> speakers;

    // Constructor to initialize CoreNLP pipeline
    public FindQuotes() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,entitymentions,depparse,coref,quote");

        this.pipeline = new StanfordCoreNLP(props);
        this.speakers = new ArrayList<>();
    }

    // Method to process text and extract quotes
    public void processText(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
        for (CoreMap quote : quotes) {
            System.out.println("Quote: " + quote.get(CoreAnnotations.TextAnnotation.class));

            /*
            if (quote.get(QuoteAttributionAnnotator.MentionAnnotation.class) != null) {
                System.out.println("Predicted Mention: " + quote.get(QuoteAttributionAnnotator.MentionAnnotation.class) +
                        " Predictor: " + quote.get(QuoteAttributionAnnotator.MentionSieveAnnotation.class));
            } else {
                System.out.println("Predicted Mention: none");
            }
                */

            if (quote.get(QuoteAttributionAnnotator.SpeakerAnnotation.class) != null) {
                System.out.println("Predicted Speaker: " + quote.get(QuoteAttributionAnnotator.SpeakerAnnotation.class) +
                        " Predictor: " + quote.get(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class));
                    
                speakers.add(quote.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
                
            } else {
                System.out.println("Predicted Speaker: none");
            }

            System.out.println("====");
        }
    }

    public List<String> getSpeakers(){
        return speakers;
    }
}
