package jreader;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.QuoteAttributionAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class FindQuotes {
    private final StanfordCoreNLP pipeline;

    // Constructor to initialize CoreNLP pipeline
    public FindQuotes() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,entitymentions,depparse,coref,quote");

        this.pipeline = new StanfordCoreNLP(props);
    }

    // Method to process text and extract quotes
    public void processText(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
        for (CoreMap quote : quotes) {
            System.out.println("Quote: " + quote.get(CoreAnnotations.TextAnnotation.class));

            if (quote.get(QuoteAttributionAnnotator.MentionAnnotation.class) != null) {
                System.out.println("Predicted Speaker: " + quote.get(QuoteAttributionAnnotator.MentionAnnotation.class) +
                        " Predictor: " + quote.get(QuoteAttributionAnnotator.MentionSieveAnnotation.class));
            } else {
                System.out.println("Predicted Mention: none");
            }

            System.out.println("====");
        }

        for (Mention m : document.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
            if("PERSON".equals(m.nerString)) {
                System.out.println("Speaker: " + m.toString() + " Gender: " + m.gender);
            }
        }
    }
}
