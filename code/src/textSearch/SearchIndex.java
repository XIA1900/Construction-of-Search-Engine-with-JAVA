package textSearch;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.postingshighlight.PostingsHighlighter;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.Scanner;

public class SearchIndex {

    static String indexPath = "index";
    Analyzer textAnalyzer;

    public static void main(String[] args) throws Exception {

        SearchIndex search = new SearchIndex();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input index words");
        String s = scanner.nextLine();
//        search.searchIndex(s);
        search.run(indexPath, s, 10);


    }



    private void searchIndex(String s) throws Exception{
        String[] m = s.split("\\s+");
        for(String i: m){
            System.out.println(i);
            i = "contents";
        }
//        Term[] terms = new Term[m.length];
//        for(int i=0; i<m.length; i++){
//            System.out.println(m[i]);
//            m[i] = "contents";
//
////            terms[i] = new Term("name", m[i]);
//        }
//        MultiPhraseQuery multiPhraseQuery = new MultiPhraseQuery();
//        multiPhraseQuery.add(terms);



        IndexReader reader = DirectoryReader.open(
                FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher is=new IndexSearcher(reader);
        textAnalyzer = new SmartChineseAnalyzer();

//        QueryParser parser = new QueryParser("contents", textAnalyzer);
//        Query query = parser.parse(s);
        Query query = MultiFieldQueryParser.parse(s.split(" "), m, textAnalyzer);
        TopDocs docs = is.search(query, 10);
        ScoreDoc[] sdoc = docs.scoreDocs;
        Document doc;

        for(int i=0;i<sdoc.length;i++){
            doc = is.doc(sdoc[i].doc);
            String name = doc.get("path");
            System.out.println(name+" socre:"+sdoc[i].score);
            System.out.println(doc.getField("contents"));
        }
    }


    public void run(String indexPath, String query, int n) throws Exception{
        String[] m = query.split("\\s+");
        for(int i=0; i<m.length; i++){
            System.out.println(m[i]);
            m[i] = "contents";
        }



        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher is=new IndexSearcher(reader);
        ScoreDoc sdoc[];
        textAnalyzer = new SmartChineseAnalyzer();

        PostingsHighlighter highlighter = new PostingsHighlighter();
//        Query q = new TermQuery(new Term("contents", query));
        Query q = MultiFieldQueryParser.parse(query.split(" "), m, textAnalyzer);
        TopDocs topDocs = is.search(q, n);
        String highlights[] = highlighter.highlight("contents", q, is, topDocs, 3);
        sdoc=topDocs.scoreDocs;
        Document doc;
        System.out.println("\n"+highlights.length);
        for(int i=0;i<highlights.length;i++){
            doc = is.doc(sdoc[i].doc);
            String name = doc.get("path");
            System.out.println(name);
//            System.out.println(highlights[i]);
        }
    }

}
