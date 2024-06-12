package at.letto.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LogService {
    private static  long time, time1;
    public static void logTimeDiff(String msg) {
        logTimeDiff(msg, true);
    }

    public static void logTimeDiff(String msg, boolean showTime) {
        if (time==0) time = System.currentTimeMillis();
        time1 = System.currentTimeMillis();
        if (showTime)
            System.out.println(msg + (time1 - time) + "ms");
        else
            System.out.println(msg);
        time = time1;
    }

    public static void logToTmpFile(String msg) {
        try {
            msg = msg+"\n";
            Files.write(Paths.get("/tmp/letto_server_logs.txt"), msg.getBytes(), StandardOpenOption.APPEND,StandardOpenOption.CREATE);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public static void clearTmpFile() {
        try {
            Files.write(Paths.get("/tmp/letto_server_logs.txt"), "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

}
