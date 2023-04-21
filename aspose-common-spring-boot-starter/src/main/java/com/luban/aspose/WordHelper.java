package com.luban.aspose;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.luban.aspose.exception.AsposeException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author hp 2023/4/20
 */
public final class WordHelper {
    private WordHelper() {
    }

    public static void wordToPdf(InputStream inputStream, OutputStream outputStream) {
        try {
            Document doc = new Document(inputStream);
            doc.save(outputStream, SaveFormat.PDF);
        } catch (Exception e) {
            throw new AsposeException("WordToPdf Exception", e);
        }
    }
}
