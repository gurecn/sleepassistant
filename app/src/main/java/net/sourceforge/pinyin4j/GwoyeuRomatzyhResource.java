package net.sourceforge.pinyin4j;

import com.p000hp.hpl.sparta.Document;
import com.p000hp.hpl.sparta.ParseException;
import com.p000hp.hpl.sparta.Parser;
import java.io.FileNotFoundException;
import java.io.IOException;

class GwoyeuRomatzyhResource {
    private Document pinyinToGwoyeuMappingDoc;

    private void setPinyinToGwoyeuMappingDoc(Document pinyinToGwoyeuMappingDoc2) {
        this.pinyinToGwoyeuMappingDoc = pinyinToGwoyeuMappingDoc2;
    }

    /* access modifiers changed from: package-private */
    public Document getPinyinToGwoyeuMappingDoc() {
        return this.pinyinToGwoyeuMappingDoc;
    }

    private GwoyeuRomatzyhResource() {
        initializeResource();
    }

    private void initializeResource() {
        try {
            setPinyinToGwoyeuMappingDoc(Parser.parse("", ResourceHelper.getResourceInputStream(
                "pinyindb/pinyin_gwoyeu_mapping.xml")));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static GwoyeuRomatzyhResource getInstance() {
        return GwoyeuRomatzyhSystemResourceHolder.theInstance;
    }

    private static class GwoyeuRomatzyhSystemResourceHolder {
        static final GwoyeuRomatzyhResource theInstance = new GwoyeuRomatzyhResource();

        private GwoyeuRomatzyhSystemResourceHolder() {
        }
    }
}
