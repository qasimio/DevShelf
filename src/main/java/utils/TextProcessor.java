package utils;
/* Whole Pipeline of text processing.
1. Tokenization
2. Normalization - lowering
3. Filtering - stop word Removal
4. Stemming - root word
*/


import org.tartarus.snowball.ext.englishStemmer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Collections;

public class TextProcessor {

   private final Set<String> stopWords;
   private final englishStemmer stemmer = new englishStemmer();

   public TextProcessor(Set<String> stopWords) {
       this.stopWords = stopWords;
   }

   public List<String> process(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList(); // Return empty list if text is null or empty
        }
       String[] rawTokens = text.toLowerCase().split("[^a-zA-Z0-9']+");
       List<String> tokens = Arrays.asList(rawTokens);

       List<String> filteredTokens = new ArrayList<>();
       for(String token : tokens) {
           if (token.isEmpty()) continue;
           if(!stopWords.contains(token)) {
               filteredTokens.add(token);
           }
       }

       List<String> stemmedTokens = new ArrayList<>();
       for (String token : filteredTokens) {
           stemmer.setCurrent(token);
           stemmer.stem();
           stemmedTokens.add(stemmer.getCurrent());
       }

       return stemmedTokens;
   }

}
