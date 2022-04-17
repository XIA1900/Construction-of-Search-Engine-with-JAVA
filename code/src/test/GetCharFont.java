package test;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetCharFont extends PDFTextStripper {

    public List<String> list_text=new ArrayList<String>();
    public List<Float> list_fontsize=new ArrayList<Float>();
    int i=0;
    public GetCharFont() throws IOException{

    }

    public void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for(TextPosition text:textPositions) {
            list_fontsize.add(text.getFontSizeInPt());
            list_text.add(text.getUnicode());
        }
        list_text.add(" ");
        list_fontsize.add((float) -1.0);
    }

    public List<String> getList_text() {
        return list_text;
    }

    public List<Float> getList_fontsize() {
        return list_fontsize;
    }
}
