package at.letto.tools.enums;

public enum Level {
    Grundlagen, Erweiterungswissen; 

    public static Level getVal(String val) {
    	try {
    		return Level.valueOf(val);
    	} catch (Exception e) {
    		return Level.Grundlagen;
    	}
    }
}

