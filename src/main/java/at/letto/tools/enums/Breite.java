package at.letto.tools.enums;

import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public enum Breite implements Selectable {
	Prozent, Pixel;

	@JsonIgnore
	@Override
	public String getText() {	
		return this.toString(); 	
	}
	@JsonIgnore
	@Override
	public int getId() { 
		return this.ordinal(); 
	}
}

