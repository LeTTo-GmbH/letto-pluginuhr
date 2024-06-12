package at.letto.tools.enums;

import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public enum Schulstufen implements Selectable {
    Jahrgang_1, Jahrgang_2, Jahrgang_3, Jahrgang_4, Jahrgang_5;

	@JsonIgnore
	@Override
	public String getText() {
		return this.name();
	}

	@JsonIgnore
	@Override
	public int getId() {
		return this.ordinal();
	}
}
