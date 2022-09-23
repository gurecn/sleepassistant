package com.p000hp.hpl.sparta;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/* renamed from: com.hp.hpl.sparta.ParseByteStream */
class ParseByteStream implements ParseSource {
    private ParseCharStream parseSource_;

    public ParseByteStream(String systemId, InputStream istream, ParseLog log, String guessedEncoding, ParseHandler handler) throws ParseException, IOException {
        log = log == null ? DEFAULT_LOG : log;
        if (!istream.markSupported()) {
            throw new Error("Precondition violation: the InputStream passed to ParseByteStream must support mark");
        }
        istream.mark(MAXLOOKAHEAD);
        byte[] start = new byte[4];
        guessedEncoding = guessedEncoding == null ? guessEncoding(systemId, start, istream.read(start), log) : guessedEncoding;
        try {
            istream.reset();
            try {
                this.parseSource_ = new ParseCharStream(systemId, new InputStreamReader(istream, fixEncoding(guessedEncoding)), log, guessedEncoding, handler);
            } catch (IOException e) {
                log.note("Problem reading with assumed encoding of " + guessedEncoding + " so restarting with " + "euc-jp", systemId, 1);
                istream.reset();
                try {
                    this.parseSource_ = new ParseCharStream(systemId, new InputStreamReader(istream, fixEncoding("euc-jp")), log, (String) null, handler);
                } catch (UnsupportedEncodingException e2) {
                    throw new ParseException(log, systemId, 1, 0, "euc-jp", "\"" + "euc-jp" + "\" is not a supported encoding");
                }
            }
        } catch (EncodingMismatchException e3) {
            String declaredEncoding = e3.getDeclaredEncoding();
            log.note("Encoding declaration of " + declaredEncoding + " is different that assumed " + guessedEncoding + " so restarting the parsing with the new encoding", systemId, 1);
            istream.reset();
            try {
                this.parseSource_ = new ParseCharStream(systemId, new InputStreamReader(istream, fixEncoding(declaredEncoding)), log, (String) null, handler);
            } catch (UnsupportedEncodingException e4) {
                throw new ParseException(log, systemId, 1, 0, declaredEncoding, "\"" + declaredEncoding + "\" is not a supported encoding");
            }
        }
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public String toString() {
        return this.parseSource_.toString();
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public String getSystemId() {
        return this.parseSource_.getSystemId();
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public int getLineNumber() {
        return this.parseSource_.getLineNumber();
    }

    private static String guessEncoding(String systemId, byte[] start, int n, ParseLog log) throws IOException {
        String encoding;
        if (n != 4) {
            log.error(n <= 0 ? "no characters in input" : "less than 4 characters in input: \"" + new String(start, 0, n) + "\"", systemId, 1);
            encoding = "UTF-8";
        } else if (equals(start, 65279) || equals(start, -131072) || equals(start, 65534) || equals(start, -16842752) || equals(start, 60) || equals(start, 1006632960) || equals(start, 15360) || equals(start, 3932160)) {
            encoding = "UCS-4";
        } else if (equals(start, 3932223)) {
            encoding = "UTF-16BE";
        } else if (equals(start, 1006649088)) {
            encoding = "UTF-16LE";
        } else if (equals(start, 1010792557)) {
            encoding = "UTF-8";
        } else if (equals(start, 1282385812)) {
            encoding = "EBCDIC";
        } else if (equals(start, (short) -2) || equals(start, (short) -257)) {
            encoding = "UTF-16";
        } else {
            encoding = "UTF-8";
        }
        if (!encoding.equals("UTF-8")) {
            log.note("From start " + hex(start[0]) + " " + hex(start[1]) + " " + hex(start[2]) + " " + hex(start[3]) + " deduced encoding = " + encoding, systemId, 1);
        }
        return encoding;
    }

    private static String hex(byte b) {
        String s = Integer.toHexString(b);
        switch (s.length()) {
            case 1:
                return "0" + s;
            case 2:
                return s;
            default:
                return s.substring(s.length() - 2);
        }
    }

    private static boolean equals(byte[] bytes, int integer) {
        return bytes[0] == ((byte) (integer >>> 24)) && bytes[1] == ((byte) ((integer >>> 16) & 255)) && bytes[2] == ((byte) ((integer >>> 8) & 255)) && bytes[3] == ((byte) (integer & 255));
    }

    private static boolean equals(byte[] bytes, short integer) {
        return bytes[0] == ((byte) (integer >>> 8)) && bytes[1] == ((byte) (integer & 255));
    }

    private static String fixEncoding(String encoding) {
        return encoding.toLowerCase().equals("utf8") ? "UTF-8" : encoding;
    }
}
