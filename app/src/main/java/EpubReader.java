/* 
import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.domain.Resource;

public class EpubReader {
    String epubPath = "example.epub"; // Change this to your EPUB file path

        try {
            // Load the EPUB file
            FileInputStream epubInputStream = new FileInputStream(epubPath);
            Book book = new EpubReader().readEpub(epubInputStream);

            // Print metadata
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author(s): " + book.getMetadata().getAuthors());

            // Extract and print text from all chapters
            System.out.println("\n--- Book Content ---\n");
            for (Resource resource : book.getContents()) {
                String text = new String(resource.getData());
                System.out.println(text);
                System.out.println("\n----------------------\n");
            }

        } catch (IOException e) {
            System.err.println("Error reading EPUB file: " + e.getMessage());
        }
}
        */
