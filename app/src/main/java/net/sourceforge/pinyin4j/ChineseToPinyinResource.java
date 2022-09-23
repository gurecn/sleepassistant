package net.sourceforge.pinyin4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import net.sourceforge.pinyin4j.multipinyin.Trie;

class ChineseToPinyinResource {
    private Trie unicodeToHanyuPinyinTable;

    private void setUnicodeToHanyuPinyinTable(Trie unicodeToHanyuPinyinTable2) {
        this.unicodeToHanyuPinyinTable = unicodeToHanyuPinyinTable2;
    }

    /* access modifiers changed from: package-private */
    public Trie getUnicodeToHanyuPinyinTable() {
        return this.unicodeToHanyuPinyinTable;
    }

    private ChineseToPinyinResource() {
        this.unicodeToHanyuPinyinTable = null;
        initializeResource();
    }

    private void initializeResource() {
        try {
            setUnicodeToHanyuPinyinTable(new Trie());
            getUnicodeToHanyuPinyinTable().load(ResourceHelper.getResourceInputStream("pinyindb/unicode_to_hanyu_pinyin.txt"));
            getUnicodeToHanyuPinyinTable().loadMultiPinyin(ResourceHelper.getResourceInputStream("pinyindb/multi_pinyin.txt"));
            getUnicodeToHanyuPinyinTable().loadMultiPinyinExtend();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }

    /* access modifiers changed from: package-private */
    public Trie getHanyuPinyinTrie(char ch) {
        return getUnicodeToHanyuPinyinTable().get(Integer.toHexString(ch).toUpperCase());
    }

    /* access modifiers changed from: package-private */
    public String[] getHanyuPinyinStringArray(char ch) {
        return parsePinyinString(getHanyuPinyinRecordFromChar(ch));
    }

    /* access modifiers changed from: package-private */
    public String[] parsePinyinString(String pinyinRecord) {
        if (pinyinRecord == null) {
            return null;
        }
        int indexOfLeftBracket = pinyinRecord.indexOf("(");
        return pinyinRecord.substring("(".length() + indexOfLeftBracket, pinyinRecord.lastIndexOf(")")).split(",");
    }

    private boolean isValidRecord(String record) {
        return record != null && !record.equals("(none0)") && record.startsWith("(") && record.endsWith(")");
    }

    private String getHanyuPinyinRecordFromChar(char ch) {
        Trie trie = getUnicodeToHanyuPinyinTable().get(Integer.toHexString(ch).toUpperCase());
        String foundRecord = null;
        if (trie != null) {
            foundRecord = trie.getPinyin();
        }
        if (isValidRecord(foundRecord)) {
            return foundRecord;
        }
        return null;
    }

    static ChineseToPinyinResource getInstance() {
        return ChineseToPinyinResourceHolder.theInstance;
    }

    private static class ChineseToPinyinResourceHolder {
        static final ChineseToPinyinResource theInstance = new ChineseToPinyinResource();

        private ChineseToPinyinResourceHolder() {
        }
    }

    class Field {
        static final String COMMA = ",";
        static final String LEFT_BRACKET = "(";
        static final String RIGHT_BRACKET = ")";

        Field() {
        }
    }
}
