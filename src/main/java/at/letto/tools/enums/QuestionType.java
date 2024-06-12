package at.letto.tools.enums;

import at.letto.ServerConfiguration;
import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;


public enum QuestionType implements Selectable {
	unused0,unused1,unused2,unused3,unused4,unused5,unused6,unused7,unused8,unused9,

	MoodleNumerical,
	MoodleCalculated,
	MoodleCalculated_Multi, 
	MoodleCalculated_Simple,
	MoodleMultichoice,
	MoodleShortAnswer,
	MoodleDescription,
	MoodleEssay,
	MoodleMatching,
	MoodleCloze,
	MoodleTrueFalse,
	MoodleRandomsMatch,
	MoodleClozeCalc,
	LinkedQuestion;
	
	public static QuestionType getType(int ordinal) {
		return QuestionType.values()[ordinal];
	}
	
	public SQMODE getSubQuestionType() {
		switch (this) {
		case MoodleCalculated:
		case MoodleCalculated_Multi:
		case MoodleCalculated_Simple:
			return SQMODE.CALCULATED;
		case MoodleCloze:
		case MoodleClozeCalc:
			return SQMODE.CALCULATED;
		case MoodleDescription:
		case MoodleEssay:
			return SQMODE.TEXT;
		case MoodleMatching:
			return SQMODE.ZUORDNUNG;
		case MoodleMultichoice:
			return SQMODE.MULTICHOICE;
		case MoodleNumerical:
			return SQMODE.CALCULATED;
		case MoodleRandomsMatch:
			return SQMODE.CALCULATED;
		case MoodleShortAnswer:
			return SQMODE.TEXT;
		case MoodleTrueFalse:
			return SQMODE.MULTICHOICE;
		default:
			break;
		}
		return SQMODE.CALCULATED;  
	}
	/**
	 * Bestimmt den Fragetyp eines Moodle-XML Fagetyp-Bezeichners 
	 * @param questionType Moodle-XML Fragetyp-Bezeichner
	 * @return QuestionTyp
	 */
	public static QuestionType getType(String questionType) {
		for (QuestionType qt : QuestionType.values()) {
			if (qt.toString().equalsIgnoreCase(questionType))
				return qt;
			if (qt.toString().equalsIgnoreCase("Moodle"+questionType))
				return qt;
		}
		return null;
	}
	/**
	 * Liefert die volle Bezeichnung für den Fragetyp
	 * @return	Bezeichnung des Fragetyps
	 */
	public String getName() {
		return ServerConfiguration.service.Res(this.toString());
	}

	@Override
	public String toString() {
		switch (this) {
		case MoodleCalculated:
		case MoodleCalculated_Multi:
		case MoodleCalculated_Simple:
			return MoodleCalculated.name()+"";
		case MoodleCloze:
		case MoodleClozeCalc:
		case MoodleDescription:
		case MoodleEssay:
		case MoodleMatching:
		case MoodleMultichoice:
		case MoodleNumerical:
		case MoodleRandomsMatch:
		case MoodleShortAnswer:
		case MoodleTrueFalse:
		case LinkedQuestion:
			return this.name()+"";
		case unused0:
		case unused1:
		case unused2:
		case unused3:
		case unused4:
		case unused5:
		case unused6:
		case unused7:
		case unused8:
		case unused9:
		default:
			return "";
		}
	}

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
