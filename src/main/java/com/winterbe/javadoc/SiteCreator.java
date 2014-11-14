package com.winterbe.javadoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Benjamin Winterberg
 */
public class SiteCreator {

    public void createSite(ExplorerResult result, String basePath) throws IOException, URISyntaxException {
        createSiteDirectory(basePath);

        List<TypeInfo> typeInfos = result.getTypeInfos();

        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(typeInfos);

        URL url = getClass()
                .getClassLoader()
                .getResource("index.html");

        URI uri = url.toURI();
        Path path = Paths.get(uri);
        byte[] bytes = Files.readAllBytes(path);

        String template = new String(bytes, StandardCharsets.UTF_8);
        template = StringUtils.replaceOnce(template, "'{{DATA}}'", data);

        File file = new File("_site/index.html");
        BufferedWriter htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        htmlWriter.write(template);
        htmlWriter.flush();
        htmlWriter.close();
    }

    private void createSiteDirectory(String basePath) throws IOException {
        File site = new File("_site");
        if (!site.exists()) {
            Files.createDirectory(Paths.get("_site"));
        }

        System.out.println("clean site directory");
        FileUtils.cleanDirectory(site);

        System.out.println("copying javadoc files");
        FileUtils.copyDirectory(new File(basePath), site);

        InputStream cssStream = getClass().getClassLoader().getResourceAsStream("stylesheet.css");
        File cssFile = new File("_site/stylesheet.css");
        cssFile.delete();
        FileOutputStream fos = new FileOutputStream(cssFile);
        IOUtils.copy(cssStream, fos);
        IOUtils.closeQuietly(fos);
    }
}
