package at.letto.tools;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestReadFile {

    private static final String filename = "out/testfile.txt";

    @Test
    public void testFileRead() {
        File file = new File(filename);
        Vector<String> data = new Vector<String>(Arrays.asList(new String[]{"<?abc>","x","<av>"}));
        Cmd.writelnfile(data, file);
        Vector<String> data1= Cmd.readfile(file);
        assertEquals(data.size(),data1.size());
        for (int i=0;i<data.size();i++) assertEquals(data.get(i), data1.get(i));
        file.delete();
    }

}
