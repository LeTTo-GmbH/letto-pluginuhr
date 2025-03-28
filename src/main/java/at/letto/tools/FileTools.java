package at.letto.tools;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class FileTools {

    public static Vector<String> readFile(Path file) {
        List<TextFileAnalyse> files = new ArrayList<>();
        files.add(TextFileAnalyse.analyse(file, StandardCharsets.UTF_8));
        files.add(TextFileAnalyse.analyse(file, StandardCharsets.UTF_16));
        files.add(TextFileAnalyse.analyse(file, StandardCharsets.US_ASCII));
        files.add(TextFileAnalyse.analyse(file, StandardCharsets.ISO_8859_1));
        files.add(TextFileAnalyse.analyse(file, StandardCharsets.UTF_16BE));
        files.add(TextFileAnalyse.analyse(file, StandardCharsets.UTF_16LE));
        Collections.sort(files);
        if (files.size()==0) return null;
        return files.get(0).getData();
    }

    public static Vector<String> readFile(File file) {
        return readFile(file.toPath());
    }

    public static Vector<String> readFile(String filename) {
        return readFile(Paths.get(filename));
    }

}
