package textSearch;

import java.io.*;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


/*
创建索引
*/
public class BuildIndex {
    static int n = 0;
    static String fileReadPath = "F:/Web Mining/www/2012/proceedings";
    static String indexPath = "index";
    Analyzer textAnalyzer;
    IndexWriter writer;

    public static void main(String[] args) throws Exception {

        BuildIndex build = new BuildIndex();
        build.buildIndex();

    }

    private void buildIndex() throws Exception{
        textAnalyzer = new SmartChineseAnalyzer();

//        索引配置文件
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
        }else {
            if(file.isDirectory()) {
//                递归调用getFile()
                for (File f: file.listFiles()) {
                    getFile(f);
                }
            }
            else if(file.getName().endsWith(".pdf")){
//                如果是文件，输出绝对路径
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
        Field body = new Field("contents", getContent(f), offsetsType);

        doc.add(body);
        doc.add(new StringField("path",fname,Field.Store.YES));
        return doc;
    }

//    public static String readPdf(String path) throws Exception {
//        StringBuffer content = new StringBuffer("");// 文档内容
//        FileInputStream fis = new FileInputStream(path);
//        PDFParser p = new PDFParser(fis);
//        p.parse();
//        PDFTextStripper ts = new PDFTextStripper();
//        content.append(ts.getText(p.getPDDocument()));
//        fis.close();
//        return content.toString().trim();
//    }

    private static String getContent(File file) throws Exception{

        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(false);
        String result = stripper.getText(document);
        document.close();
        return result;
    }

    public static String readHtml(File file) {

        StringBuffer content = new StringBuffer("");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // 读取页面
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    fis,"utf-8"));//这里的字符编码要注意，要对上html头文件的一致，否则会出乱码

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
