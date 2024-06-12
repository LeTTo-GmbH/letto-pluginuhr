package at.letto.tools;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDatum {

    @Test
    public void parseZielEinheitgW4() {
        assertEquals(4,4);
    }

    @Test
    public void parseTime() {
        assertEquals(10*3600+30*60, Datum.parseTime("10:30"));
        assertEquals(10*3600+30*60+22, Datum.parseTime("10:30:22"));
        assertEquals(10*3600+30*60+22.56, Datum.parseTime("10:30:22.56"));
    }

    @Test
    public void parseDate() {
        Date date = Datum.parseDate("13.5.1970");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(date.getDate(),Datum.day(date));
        assertEquals(date.getMonth()+1,Datum.month(date));
        assertEquals(date.getYear()+1900,Datum.year(date));

    }

    @Test
    public void testWeekday() {
        assertEquals("Sat",Datum.dayOfWeekString3(Datum.parseDate("24.9.2022")));
        assertEquals("Sun",Datum.dayOfWeekString3(Datum.parseDate("25.9.2022")));
        assertEquals("Mon",Datum.dayOfWeekString3(Datum.parseDate("26.9.2022")));
        assertEquals("Tue",Datum.dayOfWeekString3(Datum.parseDate("27.9.2022")));
        assertEquals("Wed",Datum.dayOfWeekString3(Datum.parseDate("28.9.2022")));
        assertEquals("Thu",Datum.dayOfWeekString3(Datum.parseDate("29.9.2022")));
        assertEquals("Fri",Datum.dayOfWeekString3(Datum.parseDate("30.9.2022")));
        assertEquals("Sat",Datum.dayOfWeekString3(Datum.parseDate("1.10.2022")));
        assertEquals("Wed",Datum.dayOfWeekString3(Datum.parseDate("13.5.1970")));
    }

    @Test
    public void testMonth() {
        assertEquals("Sep",Datum.monthStringGerman3(Datum.parseDate("24.9.2022")));
        assertEquals("Jän",Datum.monthStringGerman3(Datum.parseDate("24.1.2022")));
        assertEquals("Dec",Datum.monthString3(Datum.parseDate("24.12.1970")));
    }

    @Test
    public void testDateInteger() {
        String s = "13.5.1970 13:24";
        long i = Datum.toDateInteger(s);
        assertEquals(1970,Datum.year(i));
    }

    @Test
    public void weekday() {
        assertEquals(7, Datum.weekday(Datum.toDateInteger("25.6.2023")));
        assertEquals(7, LocalDate.of(2023,6,25).getDayOfWeek().getValue());
        assertEquals(1, LocalDate.of(2023,6,26).getDayOfWeek().getValue());
        assertEquals(2, LocalDate.of(2023,6,27).getDayOfWeek().getValue());
        assertEquals(3, LocalDate.of(2023,6,28).getDayOfWeek().getValue());
        assertEquals(4, LocalDate.of(2023,6,29).getDayOfWeek().getValue());
        assertEquals(5, LocalDate.of(2023,6,30).getDayOfWeek().getValue());
        assertEquals(6, LocalDate.of(2023,7,1).getDayOfWeek().getValue());
        assertEquals(7, LocalDate.of(2023,7,2).getDayOfWeek().getValue());
    }

    @Test
    public void week() {
        assertEquals(0, Datum.week(Datum.toDateInteger("1.1.2023")));
        assertEquals(1, Datum.week(Datum.toDateInteger("2.1.2023")));
        assertEquals(1, Datum.week(Datum.toDateInteger("3.1.2023")));
        assertEquals(1, Datum.week(Datum.toDateInteger("8.1.2023")));
        assertEquals(2, Datum.week(Datum.toDateInteger("9.1.2023")));
        assertEquals(1, Datum.week(Datum.toDateInteger("1.1.2024")));
        assertEquals(1, Datum.week(Datum.toDateInteger("7.1.2024")));
        assertEquals(2, Datum.week(Datum.toDateInteger("8.1.2024")));
        assertEquals(25, Datum.week(Datum.toDateInteger("23.6.2023")));
        assertEquals(25, Datum.week(Datum.toDateInteger("24.6.2023")));
        assertEquals(25, Datum.week(Datum.toDateInteger("25.6.2023")));
        assertEquals(26, Datum.week(Datum.toDateInteger("26.6.2023")));
    }



}
