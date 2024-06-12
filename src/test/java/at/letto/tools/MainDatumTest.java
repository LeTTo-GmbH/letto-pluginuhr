package at.letto.tools;

import java.time.LocalDateTime;

public class MainDatumTest {

    public static void main(String args[]) {
        System.out.println(System.currentTimeMillis());
        long d = Datum.nowDateInteger();
        LocalDateTime localDateTime = Datum.nowLocalDateTime();
        String ds  = localDateTime.toString();
        String ds1 = Datum.formatDateTime(localDateTime);
        String ds2 = Datum.formatDateTime(d);
        System.out.println(ds+", "+ds1+", "+ds2+", "+d);
        LocalDateTime localDateTime1 = Datum.parseLocalDateTime(ds);
        long d1 = Datum.toDateInteger(ds);
        System.out.println(d1-d);
        System.out.println(Datum.toDateInteger("13.5.1970 3:40:22.234"));
        System.out.println(Datum.toDateInteger("13.5.1970 3:40:22.23"));
        System.out.println(Datum.toDateInteger("13.5.1970 3:40:22.2"));
        System.out.println(System.currentTimeMillis());
    }

}
