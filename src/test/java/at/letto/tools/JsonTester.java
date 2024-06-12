package at.letto.tools;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;

public class JsonTester {

    public static void main(String[] args) {
        System.out.println("String Klasse:"+(String.class).toString());
        System.out.println(JSON.objToJson("ab\"c"));
    }

    @Test
    public void testString() {
        assertEquals("abc",JSON.jsonToObj(JSON.objToJson("abc"),String.class));
        assertEquals("ab\"c",JSON.jsonToObj(JSON.objToJson("ab\"c"),String.class));
        assertEquals("abc",JSON.jsonToObj(JSON.objToJson("abc"),"java.lang.String"));
        assertEquals("ab\"c",JSON.jsonToObj(JSON.objToJson("ab\"c"),"java.lang.String"));
    }
}
