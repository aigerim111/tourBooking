package com.example.project1.user;

import com.example.project1.tours.Tour;
import com.example.project1.user.UserDetails;
import com.example.project1.user.Usr;
import com.lowagie.text.DocumentException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;

@PropertySource("")
public class PDF {
    public void generatePdfFromHtml(String html,Long id) throws IOException, DocumentException {
        OutputStream outputStream = new FileOutputStream("C:\\Users\\Aigerim\\IdeaProjects\\project1(1)\\src\\main\\resources\\user" + File.separator + id+".pdf");

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);


        outputStream.close();
    }

    public String parseThymeleafTemplate(UserDetails user, BookedInfo tour) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setOrder(0);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("usr", user);
        context.setVariable("tour",tour);

        return templateEngine.process("pdffile", context);
    }

}
