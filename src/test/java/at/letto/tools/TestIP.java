package at.letto.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestIP {

    @Test
    public void parseIP() {
        assertEquals(IP.parseIP("192.168.0.101").toString(),"/192.168.0.101");
        assertEquals(IP.parseIP("10.0.0.10").toString(),"/10.0.0.10");
    }

    @Test
    public void isIP() {
        assertFalse(IP.isIP("0x13.13.20.10"));
        assertFalse(IP.isIP("256.13.20.10"));
        assertFalse(IP.isIP("13.20.10"));
        assertTrue(IP.isIP("193.168.0.101"));
        assertTrue(IP.isIP("0.0.0.0"));
    }

    @Test
    public void localIP() {
        assertFalse(IP.isOeffentlicheIP("192.168.0.101"));
        assertFalse(IP.isOeffentlicheIP("10.0.0.10"));
        assertFalse(IP.isOeffentlicheIP("127.0.0.1"));
        assertFalse(IP.isOeffentlicheIP("127.13.20.10"));
        assertTrue(IP.isOeffentlicheIP("193.168.0.101"));
        assertTrue(IP.isOeffentlicheIP("93.168.0.101"));
        assertTrue(IP.isOeffentlicheIP("8.8.8.8"));
    }

}
