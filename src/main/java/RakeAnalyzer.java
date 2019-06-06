import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.*;

public class RakeAnalyzer {
    private final Rake RAKE = new Rake("en");
    private ArrayList<ArrayList<Pair>> keywords = new ArrayList<ArrayList<Pair>>();
    private String sourceText;
    private ArrayList<Pair> keyPhrases = new ArrayList<Pair>();

    public RakeAnalyzer(String source) {
        this.sourceText = source;
    }

    public void generateKeywords() {
        LinkedHashMap<String, Double> rakeOutput = RAKE.getKeywordsFromText(sourceText);
        Iterator iter = rakeOutput.entrySet().iterator();
//        int score;
        int div;
        int len = 5;
        int counter = 0;
        ArrayList<Pair> temp = new ArrayList<Pair>();
        while (iter.hasNext()) {
        	if(counter==5) {
        		keywords.add(temp);
        		temp = new ArrayList<Pair>();
        		counter = 0;
        	}
            Map.Entry word = (Map.Entry) iter.next();

            String w = (String) word.getKey();
            double v = (Double) word.getValue();
            Pair keyword = new Pair(w, v);
            
//            score = Integer.parseInt(w.substring(w.lastIndexOf('(')+1, w.length()-1));
            
            temp.add(keyword);
            counter++;
            
//            keywords.add(keyword);
            System.out.println(keyword);
        }
    }

    public void generateKeyPhrases(int max_limit) {
        int[][] cooccurrences = new int[keywords.size()][keywords.size()];
        String[] split = sourceText.split("[^a-zA-Z0-9_\\\\+/-\\\\]");
        ArrayList<String> phrases = new ArrayList<String>();

        for (ArrayList<Pair> a: keywords) {
        	for(Pair p: a) {
        		p.genIndices(split);        		
        	}
        }

        // find cooccurrences of the keywords
        for (int i = 0; i < keywords.size(); i++) {
        	for(Pair p:keywords.get(i)) {
	            for (int j = 0; j < keywords.size(); j++) {
	            	for(Pair q:keywords.get(i)) {
		                int[][] windows = p.windows(q);
		                ArrayList<Integer> smallWindows = new ArrayList<Integer>();
		                for (int[] window: windows) {
		                    if (window[2] < max_limit) {
		                        cooccurrences[i][j] += 1;
		                        smallWindows.add(Math.min(window[0], window[1]));
		                        smallWindows.add(Math.max(window[0], window[1]));
		                    }
		                }
		                // for the largest cooccurrences, find important transition words/related words
		                if (cooccurrences[i][j] >= 3) {
		                    ArrayList<String> context = new ArrayList<String>();
		                    ArrayList<Integer> contextVals = new ArrayList<Integer>();
		                    for (int w = 0; w < smallWindows.size(); w += 2) {
		                        for (String word: Arrays.copyOfRange(split, smallWindows.get(w) - 5, smallWindows.get(w + 1) + 5)) {
		                            if (context.contains(word)) {
		                                int index = context.indexOf(word);
		                                contextVals.set(index, contextVals.get(index) + 1);
		                            } else {
		                                context.add(word);
		                                contextVals.add(1);
		                            }
		                        }
		                    }
		                    String phrase = "";
		                    for (int w = 0; w < context.size(); w++) {
		                        if (contextVals.get(w) >= 2) {
		                            phrase += " " + context.get(w);
		                        }
		                    }
		                    phrases.add(phrase);
		                }
	            	}
	            }
        	}
        }

        // rank phrases
        String[] all_phrases = new String[phrases.size()];
        for (int i = 0; i < phrases.size(); i++) {
            all_phrases[i] = phrases.get(i);
        }

        Rake rake = new Rake("en");
        LinkedHashMap<String, Double> wordScores = rake.calculateWordScores(all_phrases);
        LinkedHashMap<String, Double> keywordCandidates = rake.getCandidateKeywordScores(all_phrases, wordScores);

        LinkedHashMap<String, Double> rakeOutput = rake.sortHashMap(keywordCandidates);
        Iterator iter = rakeOutput.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry word = (Map.Entry) iter.next();

            String w = (String) word.getKey();
            double v = (int) Integer.parseInt((String) word.getValue());
            Pair keyword = new Pair(w, v);

            keyPhrases.add(keyword);
            System.out.println(keyword + "\n");
        }
    }

    public ArrayList<String> keywords() {
//        Collections.sort(keywords, new Comparator<ArrayList<Pair>>() {
//        	public int compare(ArrayList<Pair> a1, ArrayList<Pair> a2) {
//        		Collections.sort(a1);
//        		Collections.sort(a2);
//        		return 0;
//        	}
//        });
        ArrayList<String> justWords = new ArrayList<String>();
        for (ArrayList<Pair> a: keywords) {
        	for(Pair p: a) {
        		justWords.add(p.word);
        		System.out.println(p.word);
        	}
        }
        return new ArrayList<String>(justWords.subList(0, 5));
    }

    public ArrayList<String> keyPhrases() {
        Collections.sort(keyPhrases);
        ArrayList<String> justPhrases = new ArrayList<String>();
        for (Pair p: keyPhrases) {
            justPhrases.add(p.word());
        }
        return justPhrases;
    }

    class Pair implements Comparable<Pair> {
        private String word;
        private double val;
        private ArrayList<Integer> indices;

        Pair(String w, double v) {
            word = w;
            val = v;
        }

        String word() {
            return word;
        }

        double value() {
            return val;
        }

        void genIndices(String[] sourceText) {
            ArrayList<String> text = new ArrayList<String>();
            for (String word: sourceText) {
                text.add(word);
            }
            indices = new ArrayList<Integer>();
            while (text.contains(word)) {
                int index = text.indexOf(word);
                indices.add(index);
                text = new ArrayList<String> (text.subList(index + 1, text.size()));
            }
        }

        ArrayList<Integer> getIndices() {
            return indices;
        }

        int[][] windows(Pair other) {
            ArrayList<Integer> other_indices = other.getIndices();
            int list_position = 0;
            int[][] windows = new int[indices.size()][3];
            for (int i = 0; i < indices.size(); i++) {
                int this_index = indices.get(i);
                while ((list_position + 2 < other_indices.size()) && ((other_indices.get(list_position + 1) - this_index) < (other_indices.get(list_position) - this_index))) {
                    list_position++;
                }
                windows[i][0] = this_index;
                windows[i][1] = other_indices.get(list_position);
                windows[i][2] = Math.abs(windows[i][1] - windows[i][0]);
            }
            return windows;
        }

        @Override
        public String toString() {
            return word + " (" + val + ")";
        }
        
        public int compareTo(Pair pair) {
            if (this.val == pair.value()) {
                return 0;
            }
            return this.val > pair.value() ? 1 : -1;
        }
    }
}