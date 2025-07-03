package com.company.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import java.util.UUID;

@Service
public class PdfService {

    private static final String PDF_DIR = "src/main/resources/generated-pdfs";

    public String generatePdfWithUserInfo(String userFullName, MultipartFile imageFile) throws IOException {
        Path pdfDirectory = ensurePdfDirectoryExists();
        String fileName = "pdf_" + UUID.randomUUID() + ".pdf";
        Path pdfPath = pdfDirectory.resolve(fileName);

        try (PdfWriter writer = new PdfWriter(pdfPath.toString());
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            document.add(new Paragraph("User Full Name: " + userFullName));

            if (imageFile != null && !imageFile.isEmpty()) {
                addImageToDocument(document, imageFile);
            } else {
                document.add(new Paragraph("No image provided"));
            }

        } catch (Exception e) {
            throw new IOException("Failed to generate PDF: " + e.getMessage(), e);
        }

        return pdfPath.toString();
    }

    public boolean isPdfFileExists(String pdfPath) {
        return Files.exists(Paths.get(pdfPath));
    }

    private Path ensurePdfDirectoryExists() throws IOException {
        Path path = Paths.get(PDF_DIR);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    private void addImageToDocument(Document document, MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            Image image = new Image(ImageDataFactory.create(imageBytes));
            image.setWidth(200);
            image.setHeight(150);
            document.add(image);
        } catch (IOException e) {
            document.add(new Paragraph("Image could not be embedded: " + e.getMessage()));
        }
    }
}
