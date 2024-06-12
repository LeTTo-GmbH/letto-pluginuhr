package at.letto.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLeTToProperties {

    private static final String apName = "application.prop";
    private static final String cfName = "config.json";
    private static final String cfxName= "configx.json";

    @BeforeAll
    public static void createApplicationProperties() throws IOException {
        // Application.properties
        File file = new File(apName);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("letto.xname=111\n");
        fileWriter.write("letto.configFile="+cfxName+"\n");
        fileWriter.close();
        // config.json
        file = new File(cfName);
        fileWriter = new FileWriter(file);
        fileWriter.write("{\n  \"b1\":\"222\",\n  \"b2\":\"333\"\n}\n");
        fileWriter.close();
        // config.json
        file = new File(cfxName);
        fileWriter = new FileWriter(file);
        fileWriter.write("{\"c1\":\"444\",\"c2\":\"555\"}\n");
        fileWriter.close();
    }

    @AfterAll
    public static void deleteApplicationProperties() {
        File file = new File(apName);
        if (file.exists())
            file.delete();
        file = new File(cfName);
        if (file.exists())
            file.delete();
        file = new File(cfxName);
        if (file.exists())
            file.delete();

    }

    @Test
    public void applicationPropertiesTest() {
        Map<String,String> p = new HashMap<>();
        p.put("properties",apName);
        LeTToProperties leTToProperties = new LeTToProperties(p);
        assertEquals("111", leTToProperties.propertyString("xname"));
        assertEquals(cfxName, leTToProperties.propertyString("configFile"));
        assertEquals("222", leTToProperties.propertyString("b1"));
        assertEquals("333", leTToProperties.propertyString("b2"));
        assertEquals("444", leTToProperties.propertyString("c1"));
        assertEquals("555", leTToProperties.propertyString("c2"));
    }

    @Test
    public void configTest() {
    }

}
