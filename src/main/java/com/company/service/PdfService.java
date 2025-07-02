package com.company.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PdfService {

    public String generatePdfWithUserInfo(String userFullName, MultipartFile imageFile) throws IOException {
        Path pdfPath = createTempPdfPath();

        try (PdfWriter writer = new PdfWriter(pdfPath.toString());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            addUserFullName(document, userFullName);
            addImage(document, imageFile);
        }

        return pdfPath.toString();
    }

    private Path createTempPdfPath() {
        String fileName = "pdf_" + UUID.randomUUID() + ".pdf";
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        return tempDir.resolve(fileName);
    }

    private void addUserFullName(Document document, String userFullName) {
        Paragraph nameParagraph = new Paragraph("User Full Name: " + userFullName);
        document.add(nameParagraph);
    }

    private void addImage(Document document, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            document.add(new Paragraph("No image provided"));
            return;
        }

        try {
            byte[] imageBytes = imageFile.getBytes();
            Image image = new Image(ImageDataFactory.create(imageBytes));
            image.setWidth(200);
            image.setHeight(150);
            document.add(image);
        } catch (Exception e) {
            document.add(new Paragraph("Image could not be embedded: " + e.getMessage()));
        }
    }


    public boolean isPdfFileExists(String pdfPath) {
        return Files.exists(Paths.get(pdfPath));
    }
}
