package at.letto.tools;

import at.letto.ServerConfiguration;
import org.junit.Test;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class MainCmdTest {

    public static String recode(String s, String fromCharset, String toCharset) {
        try {
            Charset cs = Charset.forName(fromCharset);
            CharsetEncoder encoder = cs.newEncoder();
            ByteBuffer bf = encoder.encode(CharBuffer.wrap(s));
            String ret = Charset.forName(toCharset).newDecoder().decode(bf).toString();
            return ret;
        } catch (Exception ex) {}
        return s;
    }

    public static void main(String[] args) {
        String ret = Cmd.systemcallbatch("dir","c:/opt/letto");
        System.out.println(ret);
        String s  = recode(ret,"windows-1252","UTF-8");
        String s1 = recode(ret,"UTF-8","windows-1252");
        String s2 = recode(ret,"UTF-8","UTF-8");
        System.out.println(s);
        System.out.println(s1);
        System.out.println(s2);
        //ret = ec;
    }
}
