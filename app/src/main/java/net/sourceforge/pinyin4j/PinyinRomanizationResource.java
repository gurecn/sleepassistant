package net.sourceforge.pinyin4j;

import com.p000hp.hpl.sparta.Document;
import com.p000hp.hpl.sparta.ParseException;
import com.p000hp.hpl.sparta.Parser;
import java.io.FileNotFoundException;
import java.io.IOException;

/* access modifiers changed from: package-private */
public class PinyinRomanizationResource {
    private Document pinyinMappingDoc;

    private void setPinyinMappingDoc(Document pinyinMappingDoc2) {
        this.pinyinMappingDoc = pinyinMappingDoc2;
    }

    /* access modifiers changed from: package-private */
    public Document getPinyinMappingDoc() {
        return this.pinyinMappingDoc;
    }

    private PinyinRomanizationResource() {
        initializeResource();
    }

    private void initializeResource() {
        try {
            setPinyinMappingDoc(Parser.parse("", ResourceHelper.getResourceInputStream("pinyindb/pinyin_mapping.xml")));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static PinyinRomanizationResource getInstance() {
        return PinyinRomanizationSystemResourceHolder.theInstance;
    }

    /* access modifiers changed from: private */
    public static class PinyinRomanizationSystemResourceHolder {
        static final PinyinRomanizationResource theInstance = new PinyinRomanizationResource();

        private PinyinRomanizationSystemResourceHolder() {
        }
    }
}
