package at.letto.math.interfaces;

import at.letto.tools.enums.Score;

import java.util.List;

public interface ScoreTestAntwortSqInterface {

    String getInput();
    int    getScoreAnzahl();
    /** Soll-Punkte für die Subquestion */
    Double getSoll();
    void   setSoll(Double soll);
    /** Ist-Punkte für die Subquestion */
    Double getIst();
    void   setIst(Double ist);
    /** Ist-Punkte für die Subquestion ohne Penalty-Abzüge */
    Double getOrigIst();
    void   setOrigIst(Double origIst);
    /** Status der Beurteilung (Score.NotScored, Score.OK, ...) */
    void   setScored(Score scored);
    /** Information zur Beurteilung */
    void   setScoreText(String scoreText);
    /** Kurzinformationen wie Punkteabzüge bei Mehrfachantwort etc. zu dieser Beurteilung */
    String getScoreInfo();
    void   setScoreInfo(String scoreInfo);
    /** Gibt an, ob diese Subquestíon vom Lehrer beurteilt wurde */
    void   setLehrerScored(boolean lehrerScored);
    /** Feedback zur Beurteilung */
    void   setFeedback(String feedback);
    /**
     * Anzahl, wie oft diese Testantwort bereits beurteilt wurde: Nur wenn das Eingabefeld ausgefüllt wurde
     * (diese Teilfrage also beantwortet wurde), wird  diese Anzahl erhöht.
     * Verwendung: Penalty-Punkte bei mehrfach beantworteten Fragen kann ein Abzug pro Versuch
     * ermittelt werden
     */
    void   setScoreAnzahl(int scoreAnzahl);
    /** Schülerantwort auf eine Teilfrage */
    void   setInput(String input);
    List<ScoreTestAnswerDetailInterface> scoreAnswers();

}
