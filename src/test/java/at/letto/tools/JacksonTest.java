package at.letto.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class JacksonTest {

    @Getter @Setter @AllArgsConstructor
    public class TestObj {
        private String einheit;
        private String wert;
    }

    @Test
    public void TestSingleQuote() {
        TestObj o = new TestObj("'V/A'","abc'xyz");
        System.out.println(JSON.objToJson(o));
        assertEquals("{\"einheit\":\"\\u0027V/A\\u0027\",\"wert\":\"abc\\u0027xyz\"}",JSON.objToJson(o));
    }

}
