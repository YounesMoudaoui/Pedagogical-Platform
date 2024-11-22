package com.projetpedagogique.pegagogicalplatform.Util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfReader {

    // Méthode pour extraire le texte d'un PDF
    public static String extractTextFromPdf(String pdfFilePath) throws IOException {
        File file = new File(pdfFilePath);
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}
