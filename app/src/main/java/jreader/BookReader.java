package jreader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class BookReader {

    Narrator narrator;

    public BookReader(){
        this.narrator = new Narrator();
    }

    public void readBook() throws InterruptedException, ExecutionException {
    String epubPath = "src\\main\\resources\\The Engineer ReConditioned.epub"; // Change this to your EPUB file path

        try {
            // Load the EPUB file
            FileInputStream epubInputStream = new FileInputStream(epubPath);
            Book book = new EpubReader().readEpub(epubInputStream);
            
            // Print out the contents of the book (debug)
            EpubReader.getAllUniqueResources()

            // Extract and print text from all chapters
            System.out.println("\n--- Book Content ---\n");
            for (Resource resource : book.getContents()) {
                String html = new String(resource.getData());
                String text = Jsoup.parse(html).text(); // Parse and clean HTML content
                this.narrator.narrateText(text);
            }

        } catch (IOException e) {
            System.err.println("Error reading EPUB file: " + e.getMessage());
        }
    }
}


