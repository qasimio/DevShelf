package utils;
// Only knows how to Load stopword.txt

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StopWordLoader {

    // Static method - because - so anyone can call it - without creating object.
    public static Set<String> loadStopWords(String filePath) {

       Set<String> stopWords = new HashSet<>();

       try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

           String line;
           while( (line = reader.readLine()) != null ) {

               String word = line.trim().toLowerCase();
               if(!word.isEmpty()) {
                   stopWords.add(word);
               }

           }

       } catch (IOException e) {
           System.err.println("Error loading stopwords file: " + e.getMessage());
       }

       return stopWords;
    }




}
