package test;

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
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchIndex {

    static int number;
    static Analyzer textAnalyzer;

    public static void main(String args[]) throws Exception {
        String indexPath = "index";
        SearchIndex search=new SearchIndex();
        System.out.println("Please input index words");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        List<String[]> snippets=search.run(indexPath, s);
        System.out.println("共有搜索记录"+search.number+"条");
        for(String[] str:snippets) {
            for(String ss:str) {
                    System.out.println(ss);
            }
        }
    }

    public static List<String[]> run(String indexPath, String query) throws Exception{
        String[] m = query.split("\\s+");
        int l=m.length;
        for(int i=0;i<l;i++) {
            System.out.println(m[i]);
            m[i] = "contents";
        }

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher is=new IndexSearcher(reader);
        ScoreDoc sdoc[];
        textAnalyzer = new SmartChineseAnalyzer();

        PostingsHighlighter highlighter = new PostingsHighlighter();
        Query q = MultiFieldQueryParser.parse(query.split(" "), m, textAnalyzer);
        TopDocs topDocs=is.search(q, Integer.MAX_VALUE);   //返回查询到的前n个

        String highlights[] = highlighter.highlight("contents", q, is, topDocs, 3); //返回的snippet中最多包含1段含有该关键词的片段
        int length = highlights.length;
        Document[] doc = new Document[length];
        sdoc=topDocs.scoreDocs;
        number=sdoc.length;
        for(int i=0;i<number;i++) {
            System.out.println("sdoc:"+i+":"+sdoc[i]);
        }
        for(int i=0;i<number;i++){
            doc[i] = is.doc(sdoc[i].doc);
        }

        List<String[]> snippets = getSnippets(doc,highlights);
        return snippets;
    }

    public static List<String[]> getSnippets(Document[] doc,String highlights[]) {
        List<String[]> snippets=new ArrayList<String[]>();
        for (int i=0;i<doc.length;i++) {
            String[] snippet=new String[3];
            snippet[0]=doc[i].get("title");
            snippet[1]=highlights[i];
            snippet[2]=doc[i].get("path");
            snippets.add(snippet);
        }
        return snippets;
    }
    
}
