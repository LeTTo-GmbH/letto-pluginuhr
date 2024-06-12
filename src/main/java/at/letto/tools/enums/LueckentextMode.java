package at.letto.tools.enums;

import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Enumerations-Feld zur Definition des 
 * @author Thomas Mayer
 *
 */
public enum LueckentextMode implements Selectable {
	MultipleChoice,
	SingleChoice,
	Texteingabe,
	DragAndDrop,
	RegularExpression,
	CaseSensitive;

	public static LueckentextMode getType(int ordinal) {
		return LueckentextMode.values()[ordinal];
	}

	@JsonIgnore
	@Override
	public String getText() {
		return this.toString().replaceAll("_", " ");
	}

	@JsonIgnore
	@Override
	public int getId() {
		return ordinal();
	}
}
