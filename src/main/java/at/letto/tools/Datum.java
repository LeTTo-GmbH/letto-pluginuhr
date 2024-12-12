package at.letto.tools;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Datum {

    public static SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat simpleDateFormatMillis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");

    public static String[] WEEKDAYS3 = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    public static String[] WEEKDAYS2 = {"Su","Mo","Tu","We","Th","Fr","Sa"};
    public static String[] WEEKDAYS = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    public static String[] WEEKDAYSGERMAN = {"Sonntag","Montag","Dinestag","Mittwock","Donnerstag","Freitag","Samstag"};
    public static String[] WEEKDAYSGERMAN3 = {"Son","Mon","Die","Mit","Don","Fre","Sam"};
    public static String[] WEEKDAYSGERMAN2 = {"So","Mo","Di","Mi","Do","Fr","Sa"};

    public static String[] MONTH3 = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    public static String[] MONTHGERMAN3 = {"Jän","Feb","Mär","Apr","Mai","Jun","Jul","Aug","Sep","Okt","Nov","Dez"};
    public static String[] MONTH = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    public static String[] MONTHGERMAN = {"Jänner","Februar","März","April","Mai","Juni","Juli","August","September","Oktober","November","Dezember"};

    public static Pattern patternDateYYYYMMDD = Pattern.compile("^(?<y>\\d\\d\\d\\d)(?<m>\\d\\d)(?<d>\\d\\d)$");
    public static Pattern patternDateYYYYMD = Pattern.compile("^(?<y>\\d\\d\\d\\d)[\\.\\-\\/\\s]*(?<m>\\d+)[\\.\\-\\/\\s]*(?<d>\\d+)$");
    public static Pattern patternDateDMY = Pattern.compile("^(?<d>\\d+)[\\.\\-\\/\\s]*(?<m>\\d+)[\\.\\-\\/\\s]*(?<y>\\d+)$");

/*
    static String dateFormats[] = {"d.M.yyyy","d.MMM.yyyy","d/M/yyyy","d/MMM/yyyy","d-M-yyyy","d-MMM-yyyy","yyyy.M.d", "yyyy.MMM.d",
            "yyyy/M/d","yyyy/MMM/d","yyyy-M-d","yyyy-MMM-d","d.M.yy","d.MMM.yy","yy-M-d","yy-MMM-d",
            "d.MMM.y","y-M-d","y-MMM-d"};*/

    public static int second(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }

    public static int minute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    /** Stunde im 24-Stunden-Format */
    public static int hour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int day(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
        // return date.getDate();
    }

    public static int month(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH)+1;
        //return date.getMonth()+1;
    }

    public static int year(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
        //return date.getYear()+1900;
    }

    public static int dayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK)-1;
    }

    public static String dayOfWeekString3(Date date) { return WEEKDAYS3[dayOfWeek(date)]; }
    public static String dayOfWeekString2(Date date) { return WEEKDAYS2[dayOfWeek(date)]; }
    public static String dayOfWeekString(Date date) { return WEEKDAYS[dayOfWeek(date)]; }
    public static String dayOfWeekStringGerman3(Date date) { return WEEKDAYSGERMAN3[dayOfWeek(date)]; }
    public static String dayOfWeekStringGerman2(Date date) { return WEEKDAYSGERMAN2[dayOfWeek(date)]; }
    public static String dayOfWeekStringGerman(Date date) { return WEEKDAYSGERMAN[dayOfWeek(date)]; }

    public static String monthString3(Date date) { return MONTH3[month(date)-1];}
    public static String monthString(Date date) { return MONTH[month(date)-1];}
    public static String monthStringGerman3(Date date) { return MONTHGERMAN3[month(date)-1];}
    public static String monthStringGerman(Date date) { return MONTHGERMAN[month(date)-1];}


    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static String toString(Date date) {
        if (date==null) return "";
        return day(date)+"."+month(date)+"."+year(date);
    }

    public static String toHTMLDateString(Date date) {
        if (date==null) return "";
        String y=year(date)+"";
        while (y.length()<4) y="0"+y;
        String m=month(date)+"";
        while (m.length()<2) m="0"+m;
        String d=day(date)+"";
        while (d.length()<2) d="0"+d;
        return d+"/"+m+"/"+y;
    }

    public static String toSQLDateString(Date date) {
        if (date==null) return "";
        String y=year(date)+"";
        while (y.length()<4) y="0"+y;
        String m=month(date)+"";
        while (m.length()<2) m="0"+m;
        String d=day(date)+"";
        while (d.length()<2) d="0"+d;
        return y+"-"+m+"-"+d;
    }

    public static String toSQLDateString(int year, int month, int day) {
        String y=year+"";
        while (y.length()<4) y="0"+y;
        String m=month+"";
        while (m.length()<2) m="0"+m;
        String d=day+"";
        while (d.length()<2) d="0"+d;
        return y+"-"+m+"-"+d;
    }

    public static Date setDate(int y, int m, int d) {
        if (y<0 || m<1 || d<1) return null;
        if (y>9999) return null;
        if (m>12) return null;
        if (d>31) return null;
        try {
            Date date = java.sql.Date.valueOf(toSQLDateString(y, m, d));
            return date;
        } catch (Exception ex) { return null; }
    }

    public static Date parse(String s) {
        Matcher matcher;
        int y,m,d;
        s = s.trim();
        if ((matcher=patternDateYYYYMMDD.matcher(s)).find()) {
            y = Integer.parseInt(matcher.group("y"));
            m = Integer.parseInt(matcher.group("m"));
            d = Integer.parseInt(matcher.group("d"));
            return setDate(y,m,d);
        }
        if ((matcher=patternDateYYYYMD.matcher(s)).find()) {
            y = Integer.parseInt(matcher.group("y"));
            m = Integer.parseInt(matcher.group("m"));
            d = Integer.parseInt(matcher.group("d"));
            return setDate(y,m,d);
        }
        if ((matcher=patternDateDMY.matcher(s)).find()) {
            y = Integer.parseInt(matcher.group("y"));
            m = Integer.parseInt(matcher.group("m"));
            d = Integer.parseInt(matcher.group("d"));
            return setDate(y,m,d);
        }
        return parseDate(s);
    }

    static SimpleDateFormat formatter[] = {
            new SimpleDateFormat("dd/MM/yyyy"),
            new SimpleDateFormat("dd.MM.yyyy"),
            new SimpleDateFormat("dd-MM-yyyy"),
            new SimpleDateFormat("dd.MMM.yyyy"),
            new SimpleDateFormat("dd-MMM-yyyy"),
            new SimpleDateFormat("yyyy-MMM-dd"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy.MMM.dd")
    };

    private static Date parseDate(String dat) {
        Date date = null;
        for (SimpleDateFormat format: formatter) {
            try {
                date = format.parse(dat);
                return date;
            } catch (Exception e) {}
        }
        return null;
    }

    // ------------------------------------------------------- parse DATE -----------------------------
    private static final String  pTime = "\\s*(?<h>\\d\\d?):(?<m>\\d\\d?)(:(?<s>\\d\\d?)(\\.(?<nk>\\d+?)?)?)?\\s*";
    private static final Pattern patternTime=Pattern.compile("^"+pTime+"$");
    private static final Pattern patternTimeDate=Pattern.compile("^(?<time>"+pTime+")\\s(?<date>[a-zA-Z0-9]+.*)$");
    private static final Pattern patternDateTime=Pattern.compile("^(?<date>[a-zA-Z0-9]+.*)\\s(?<time>"+pTime+")$");

    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        date = c.getTime();
        return date;
    }

    public static LocalDateTime localDate(Date dateToConvert) {
        if (dateToConvert==null) return null;
        return LocalDateTime.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());
    }

    public static Date localDate(LocalDateTime dateToConvert) {
        if (dateToConvert==null) return null;
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Parst einen Zeitstring i eine Zeit in Sekunden
     * @param s Zeitstring 10:30 bzw. 10:30:22 bzw. 10:30:22.435423
     * @return Zeit in Sekunden
     */
    public static double parseTime(String s) {
        s=s.trim();
        Matcher m;
        double h=0, min=0, sek=0;
        if ((m=Pattern.compile("^(\\d+):(\\d+)$").matcher(s)).find()) {
            h = Double.parseDouble(m.group(1));
            min = Double.parseDouble(m.group(2));
        } else if ((m=Pattern.compile("^(\\d+):(\\d+):(\\d+\\.?\\d*)$").matcher(s)).find()) {
            h = Double.parseDouble(m.group(1));
            min = Double.parseDouble(m.group(2));
            sek = Double.parseDouble(m.group(3));
        }
        sek = h*3600d+min*60+sek;
        return sek;
    }

    /**
     * Parst einen Zeitstring i eine Zeit in Sekunden
     * @param s Zeitstring 10:30 bzw. 10:30:22 bzw. 10:30:22.435423
     * @return Zeit in Sekunden
     */
    public static int[] parseTimeArray(String s) {
        s=s.trim();
        Matcher m;
        int h=0, min=0, sek=0, millis=0;
        if ((m=Pattern.compile("^(\\d+):(\\d+)$").matcher(s)).find()) {
            h = Integer.parseInt(m.group(1));
            min = Integer.parseInt(m.group(2));
        } else if ((m=Pattern.compile("^(\\d+):(\\d+):(\\d+)$").matcher(s)).find()) {
            h = Integer.parseInt(m.group(1));
            min = Integer.parseInt(m.group(2));
            sek = Integer.parseInt(m.group(3));
        } else if ((m=Pattern.compile("^(\\d+):(\\d+):(\\d+)\\.(\\d+)$").matcher(s)).find()) {
            h = Integer.parseInt(m.group(1));
            min = Integer.parseInt(m.group(2));
            sek = Integer.parseInt(m.group(3));
            String ms = m.group(4);
            if (ms.length()>3) ms = ms.substring(0,3);
            while (ms.length()<3) ms = ms+"0";
            millis = Integer.parseInt(ms);
        } else return null;
        int[] result = new int[4];
        result[0] = h;
        result[1] = min;
        result[2] = sek;
        result[3] = millis;
        return result;
    }

    public static String formatSimple(Date date) {
        if (date==null) return "";
        return simpleDate.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date==null) return "";
        return simpleDateFormat.format(date);
    }

    public static LocalDate nowLocalDate() { return LocalDate.now(); }
    public static LocalDateTime nowLocalDateTime() { return LocalDateTime.now(); }
    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime==null) return "";
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public static String formatTime(LocalDateTime localDateTime) {
        if (localDateTime==null) return "";
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static String formatDateTime(LocalDate localDate) {
        if (localDate==null) return "";
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    public static String formatDateTime(long d) {
        LocalDateTime localDateTime = dateIntegerToLocalDateTime(d);
        return formatDateTime(localDateTime);
    }
    public static String formatDate(long d) {
        LocalDate localDate = dateIntegerToLocalDate(d);
        return formatDateTime(localDate);
    }
    public static String formatTime(long d) {
        LocalDateTime localDateTime = dateIntegerToLocalDateTime(d);
        return formatTime(localDateTime);
    }

    public static long nowDateInteger() { return toDateInteger(LocalDateTime.now()); }

    // -------------------------------------------------------------------------------------------------------------
    static String dateFormats[] = {"d.M.yyyy","d.MMM.yyyy","d/M/yyyy","d/MMM/yyyy","d-M-yyyy","d-MMM-yyyy","yyyy.M.d", "yyyy.MMM.d",
                                   "yyyy/M/d","yyyy/MMM/d","yyyy-M-d","yyyy-MMM-d","d.M.yy","d.MMM.yy","yy-M-d","yy-MMM-d",
                                   "d.MMM.y","y-M-d","y-MMM-d"};
    static String timeFormats[] = {"H:m:s","H:m","H:m:s.S","H:m:s.SS","H:m:s.n"};

    private static DateTimeFormatter[] formatterLDT=initFormatterLDT();

    public static DateTimeFormatter[] initFormatterLDT() {
        Vector<String> fo = new Vector<String>();
        for (int i=0;i<dateFormats.length;i++) {
            for (int j=0;j<timeFormats.length;j++) {
                fo.add(dateFormats[i]+" "+timeFormats[j]);
                fo.add(timeFormats[j]+" "+dateFormats[i]);
            }
        }
        DateTimeFormatter[] f=new DateTimeFormatter[fo.size()];
        for (int i=0;i<fo.size();i++)
            f[i] = DateTimeFormatter.ofPattern(fo.get(i));
        return f;
    }

    public static LocalDateTime parseLocalDateTime(String dat) {
        LocalDateTime date = null;
        String datumstring = dat.replaceAll("T"," ");
        for (DateTimeFormatter format: formatterLDT) try {
            TemporalAccessor dx = format.parse(datumstring);
            return LocalDateTime.parse(datumstring,format);
        } catch (Exception e) {}
        return null;
    }

    // Prüft ob es sich bei der Eingabe um ein Datum mit Zeit handelt
    public static boolean isDateTime(String dat) {
        Matcher m;
        try {
            if ((m = patternDateTime.matcher(dat)).matches()) {
                String d = m.group(1);
                String t = m.group(2);
                if (isDate(d) && isTime(t)) return true;
            }
            if ((m = patternTimeDate.matcher(dat)).matches()) {
                String t = m.group(1);
                String d = m.group(2);
                if (isDate(d) && isTime(t)) return true;
            }
        } catch (Exception e) {}
        return false;
    }
    //** Prüft ob es sich bei der Eingabe um ein Datum handelt
    public static boolean isDate(String dat) {
        try {
            Matcher m;
            if (isTime(dat)) return false;
            if (isDateTime(dat)) return false;
            if (parseDate(dat)!=null) return true;
        } catch (Exception e) {}
        return false;
    }
    /** Prüft ob es sich bei der Eingabe um eine Zeit handelt */
    public static boolean isTime(String dat) {
        try {
            Matcher m;
            if ((m=patternTime.matcher(dat)).matches()) {
                double t = parseTime(dat);
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    // -------------------------------------------------------
    /** Bestimmt die Tagesanzahl vom 1.1.0000 bis zum angegebenen Datum */
    public static long calcDays(int y, int m, int d) {
        LocalDate refdate = LocalDate.of(0, 1, 1);
        LocalDate date    = LocalDate.of(y,m,d);
        long days = ChronoUnit.DAYS.between(refdate,date);
        return days;
    }


    /** Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums */
    public static long toDateInteger(Date d) {
        return toDateInteger(year(d), month(d), day(d), hour(d), minute(d), second(d));
    }

    /** Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums */
    public static long toDateInteger(LocalDate d) {
        LocalDate refdate = LocalDate.of(0, 1, 1);
        long days = ChronoUnit.DAYS.between(refdate,d);
        long ret  = days*3600L*24L;
        return ret;
    }

    /** Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums */
    public static long toDateInteger(LocalDateTime d) {
        LocalDate refdate = LocalDate.of(0, 1, 1);
        long days = ChronoUnit.DAYS.between(refdate,d);
        long t    = d.getHour()*3600+d.getMinute()*60+d.getSecond();
        long ret = days*3600L*24L+t;
        return ret;
    }

    /** Bestimmt die Sekunden seit dem 1.1.0000 eines gegebenen Datums als String */
    public static long toDateInteger(String s) {
        if (isTime(s)) {
            int[] time = parseTimeArray(s);
            if (time!=null)
                return toDateInteger(0,0,0,time[0],time[1],time[2]);
        } else if (isDate(s)) {
            return toDateInteger(parse(s));
        } else if (isDateTime(s)) {
            Matcher m;
            String d,t;
            if ((m = patternDateTime.matcher(s)).matches()) {
                d = m.group(1);
                t = m.group(2);
            } else if ((m = patternTimeDate.matcher(s)).matches()) {
                t = m.group(1);
                d = m.group(2);
            } else throw new RuntimeException("cannot parse Date");
            int[] time = parseTimeArray(t);
            long rt = toDateInteger(0,1,1,time[0],time[1],time[2]);
            if (time[3]>499) rt++;
            long rd = toDateInteger(parse(d));
            return rt + rd;
        }
        throw new RuntimeException("cannot parse Date");
    }

    public static long toDateInteger(int y, int m, int d, int h, int min, int sec) {
        long days = calcDays(y,m,d);
        long t    = h*3600+min*60+sec;
        long ret  = days*3600L*24L+t;
        return ret;
    }

    public static LocalDate dateIntegerToLocalDate(long d){
        long days = d/3600/24;
        long time = d%(3600*24);
        LocalDate refdate = LocalDate.of(0, 1, 1);
        LocalDate date = refdate.plusDays(days);
        return date;
    }

    public static LocalDateTime dateIntegerToLocalDateTime(long d){
        long days = d/3600/24;
        long time = d%(3600*24);
        LocalDateTime refdate = LocalDateTime.of(0, 1, 1,0,0,0);
        LocalDateTime date = refdate.plusDays(days).plusSeconds(time);
        return date;
    }

    /** Bestimmt das Jahr eines Datum-Integers */
    public static int year(long d)  { return dateIntegerToLocalDate(d).getYear();}
    /** Bestimmt das Monat eines Datum-Integers */
    public static int month(long d)  { return dateIntegerToLocalDate(d).getMonthValue();}
    /** Bestimmt den Tag im Monat eines Datum-Integers */
    public static int day(long d)    { return dateIntegerToLocalDate(d).getDayOfMonth();}
    /** Bestimmt die Stunde eines Datum-Integers */
    public static int hour(long d)    { return dateIntegerToLocalDateTime(d).getHour();}
    /** Bestimmt die Minute eines Datum-Integers */
    public static int minute(long d)    { return dateIntegerToLocalDateTime(d).getMinute();}
    /** Bestimmt die Minute eines Datum-Integers */
    public static int second(long d)    { return dateIntegerToLocalDateTime(d).getSecond();}
    /** Bestimmt den Wochentag eines Datum-Integers als Mo=1 bis So=7 */
    public static int weekday(long d) { return dateIntegerToLocalDate(d).getDayOfWeek().getValue();}
    /** Bestimmt die Kalenderwoche eines Datum-Integers */
    public static int week(long d)    { return week(dateIntegerToLocalDate(d)); }
    /** Bestimmt die Kalenderwoche eines Datums */
    public static int week(LocalDate date) {
        // Wochentag des ersten Jänners im Jahr
        int weekdayErsterJan = LocalDate.of(date.getYear(),1,1).getDayOfWeek().getValue();
        int doy= date.getDayOfYear();
        int offset = (weekdayErsterJan+5)%7;
        int week = (doy+offset)/7;
        return week;
    }

    /**
     * parst eine Zeitdauer-Angabe in einen Sekunden Long-Wert
     * @param s Zeitdauer
     * @return  Zeitdauer in Sekunden
     */
    public static long parseDauer(String s) {
        Matcher m;
        s=s.trim();
        if (s.matches("^\\d+$")) {
            return Long.parseLong(s);
        }
        String ps1 = "^\\s*((?<y>\\d*)\\s*(y|years?|j|jahre?))?";
        ps1 +=       "\\s*,?\\s*((?<m>\\d*)\\s*(m|months?|monate?))?";
        ps1 +=       "\\s*,?\\s*((?<d>\\d*)\\s*(d|days?|t|tage?))?";
        ps1 +=       "\\s*,?\\s*((((?<h>\\d?\\d):)?((?<min>\\d?\\d):))?(?<s>\\d?\\d)(\\.(?<ts>\\d*))?)?\\s*$";
        long year=0,month=0,day=0,hour=0,min=0,sec=0,tsec=0;
        String ps2 = "^\\s*((?<y>\\d*)\\s*[\\s,])?";
        ps2 +=       "\\s*((?<m>\\d*)\\s*[\\s,])?";
        ps2 +=       "\\s*((?<d>\\d*)\\s*[\\s,])?";
        ps2 +=       "\\s*,?\\s*((((?<h>\\d?\\d):)?((?<min>\\d?\\d):))?(?<s>\\d?\\d)(\\.(?<ts>\\d*))?)?\\s*$";
        String[] psa = {ps1,ps2};
        for (String ps:psa) {
            if ((m = Pattern.compile(ps).matcher(s)).find()) {
                try {
                    year = Long.parseLong(m.group("y"));
                } catch (Exception ignore) {
                }
                try {
                    month = Long.parseLong(m.group("m"));
                } catch (Exception ignore) {
                }
                try {
                    day = Long.parseLong(m.group("d"));
                } catch (Exception ignore) {
                }
                try {
                    hour = Long.parseLong(m.group("h"));
                } catch (Exception ignore) {
                }
                try {
                    min = Long.parseLong(m.group("min"));
                } catch (Exception ignore) {
                }
                try {
                    sec = Long.parseLong(m.group("s"));
                } catch (Exception ignore) {
                }
                try {
                    String ts = m.group("ts").substring(0, 3);
                    while (ts.length() < 3) ts += "0";
                    tsec = Long.parseLong(ts);
                } catch (Exception ignore) {
                }
                long result = ((((year * 365L + month * 30L + day) * 24L + hour) * 60L) + min) * 60L + sec;//) * 1000L + tsec;
                return result;
            }
        }
        return 0;
    }

    public static String toDauerString(long dauer) {
        long sec = dauer%60; dauer/=60;
        long min = dauer%60; dauer/=60;
        long hour= dauer%24; dauer/=24;
        long year= dauer/365;dauer%=365;
        long mont= dauer/30; dauer%=30;
        long day = dauer;
        String result="";
        if (year>0) result += year+"y";
        if (mont>0) result += (result.length()>0?" ":"")+mont+"m";
        if (day>0)  result += (result.length()>0?" ":"")+day+"d";
        if (hour>0 || min>0 || sec>0) result += (result.length()>0?" ":"")+hour+":"+min+":"+sec;
        return result;
    }

    /**
     * Bestimmt die Dauer in Sekunden zwischen zwei Datumswerten
     * @param datum1  erstes Datum
     * @param datum2  zweites Datum
     * @return        Sekunden datum2-datum1
     */
    public static long secondsBetweenDate(Date datum1, Date datum2) {
        long s1 = toDateInteger(datum1);
        long s2 = toDateInteger(datum2);
        return s2-s1;
    }

    /**
     * Bestimmt die Dauer in Sekunden von dem Datmu date bis zur aktuellen Zeit
     * @param date  Datum
     * @return      Dauer in Sekunden von date bis jetzt
     */
    public static long secondsToNow(Date date) {
        long s1 = toDateInteger(date);
        long s2 = toDateInteger(LocalDateTime.now());
        return s2-s1;
    }

}
