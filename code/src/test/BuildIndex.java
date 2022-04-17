package test;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class BuildIndex {
    static int n = 0;
    Analyzer textAnalyzer;
    IndexWriter writer;

    public static void main(String args[]) throws Exception {
        System.setProperty("file.encoding", "utf-8");
        String fileReadPath = "F:/Web Mining/www/2012/proceedings";
        String indexPath = "index";
        BuildIndex build = new BuildIndex();
        build.buildIndex(fileReadPath,indexPath);
    }

    public void buildIndex(String fileReadPath, String indexPath) throws Exception{
        textAnalyzer = new SimpleAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(textAnalyzer);
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        writer = new IndexWriter(dir, conf);
        File file = new File(fileReadPath);
        System.out.println(file.getAbsolutePath());
        getFile(file);
        writer.close();
    }

    public File getFile(File file) throws Exception {
        if(!file.exists()) {
            System.out.println("The file doesn't exist!");
            return null;
        }
        else {
            if(file.isDirectory()) {
                for (File f: file.listFiles()) {
                    getFile(f);
                }
            }
            else if(file.getName().endsWith(".pdf")){
                n++;
                System.out.println("file " + n);
                System.out.println(file.getAbsolutePath());
                Document doc = buildDoc(file);
                writer.addDocument(doc);
            }
        }
        return null;
    }

    private static Document buildDoc(File f) throws Exception{
        String fname=f.getPath();
        Document doc=new Document();

        FieldType offsetsType = new FieldType(TextField.TYPE_STORED);
        offsetsType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        String content[]=new String[2];
        content=getContent(f);
        Field body = new Field("contents", content[0], offsetsType);
        Field title=new Field("title",content[1], offsetsType);
        doc.add(body);
        doc.add(title);

        doc.add(new StringField("path",fname,Field.Store.YES));
        return doc;
    }


    private static String[] getContent(File file) throws Exception{

        PDDocument document = PDDocument.load(file);
        PDDocument document_new=new PDDocument();
        PDFTextStripper stripper_body=new PDFTextStripper();
        GetCharFont stripper_title=new GetCharFont();

        stripper_body.setSortByPosition(false);
        String result[]=new String[2];
        result[0]= stripper_body.getText(document);


        String str1="Abstract";
        String str2="ABSTRACT";
        int index=0;
        int len=str1.length();
        if(result[0].contains(str1)) {
            index=result[0].indexOf(str1);
            result[0]=result[0].substring(index+len);
        }
        else if(result[0].contains(str2)) {
            index=result[0].indexOf(str2);
            result[0]=result[0].substring(index+len);
        }

        //找出首页最大字号的文字作为标题；只用检查page0里面，找出最大的字体
        stripper_title.getText(document);
        stripper_title.setStartPage(0);
        stripper_title.setEndPage(2);
        Writer dummy=new OutputStreamWriter(new ByteArrayOutputStream());
        stripper_title.writeText(document_new,dummy);

        List<String> list_text=stripper_title.getList_text();
        List<Float> list_fontsize=stripper_title.getList_fontsize();

        int length_fontsize=list_fontsize.size();
        float max_fontsize=list_fontsize.get(0);
        int length=0;    //标题长度
        for(int i=0;i<length_fontsize;i++) {
            if(list_fontsize.get(i)>max_fontsize) {
                max_fontsize=list_fontsize.get(i);
                length=1;
            }
            else if(list_fontsize.get(i)==max_fontsize|| list_fontsize.get(i)==(float)-1.0) {
                length++;
            }
            else if(list_fontsize.get(i)<max_fontsize) {
                break;
            }

        }

        String title=new String();
        for(int i=0;i<length;i++) {
            title=title.concat(list_text.get(i));
        }
        result[1]=title;

        document_new.close();
        document.close();

        return result;
    }

    public static String readHtml(File file) {

        StringBuffer content = new StringBuffer("");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    fis,"utf-8"));

            String line = null;

            while ((line = reader.readLine()) != null) {
                content.append(line + "\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String contentString = content.toString();
        return contentString;
    }

}
