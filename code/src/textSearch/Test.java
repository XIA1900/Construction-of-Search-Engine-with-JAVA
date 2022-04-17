package textSearch;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.io.RandomAccessRead;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        File file = new File("F:\\Web Mining\\www\\2012\\proceedings\\companion\\p687.pdf");
//        File file = new File("F:\\Web Mining\\www\\2012\\proceedings\\companion\\p931.pdf");
        System.out.println(pdfToString(file));
    }
    public static String pdfToString(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(false);
        String result = stripper.getText(document);
        document.close();
        return result;
    }

//    public static String xpdf(File file){
//
//        String PATH_TO_XPDF="C:Program Filesxpdfpdftotext.exe";
//        String filename="c:a.pdf";
//        String[] cmd = new String[] { PATH_TO_XPDF, "-enc", "UTF-8", "-q", filename, "-"};
//        Process p = Runtime.getRuntime().exec(cmd);
//        BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
//        InputStreamReader reader = new InputStreamReader(bis, "UTF-8");
//        StringWriter out = new StringWriter();
//        char [] buf = new char[10000];
//        int len;
//        while((len = reader.read(buf))>= 0) {
//            //out.write(buf, 0, len);
//             System.out.println("the length is"+len);
//        }
//        reader.close();
//        String ts=new String(buf);
//        System.out.println("the str is"+ts);
//        return "";
//    }


}
