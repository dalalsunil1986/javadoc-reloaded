package com.winterbe.javadoc;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Benjamin Winterberg
 */
public class FileWalker {

    public ExplorerResult walk(String basePath) throws Exception {
        Objects.nonNull(basePath);
        basePath = StringUtils.removeEnd(basePath, "/");

        List<String> paths = getPaths(basePath);

        FileParser parser = new FileParser();

        List<TypeInfo> typeInfos = new ArrayList<>();

        System.out.println("parsing type infos");

        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            String sourcePath = basePath + "/" + path;
            File sourceFile = new File(sourcePath);

            Optional<TypeInfo> optional = parser.parse(sourceFile, path);
            optional.ifPresent(typeInfos::add);

            if (i == 500) {
                break;
            }
        }

        ExplorerResult result = new ExplorerResult();
        result.setTypeInfos(typeInfos);
        return result;
    }

    private List<String> getPaths(String basePath) throws IOException {
        File file = new File(basePath + "/allclasses-frame.html");
        Document document = Jsoup.parse(file, "UTF-8", "");
        List<String> paths = new ArrayList<>();
        document
                .body()
                .select(".indexContainer li a")
                .forEach((link) -> paths.add(link.attr("href")));
        return paths;
    }

}