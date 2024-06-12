package at.letto.tools.threads;

/**
 * aktueller Zustand eines Commando-Threads<br>
 * NEW      Neu, aber noch nicht gestartet <br>
 * RUNNING  läuft ohne Probleme<br>
 * STOPPED  Thread wurde von Benutzer gestoppt <br>
 * ERROR    wurde gestoppt durch einen Fehler<br>
 * FINISHED wurde ohne Fehler beendet<br>
 * ZOMBIE   Prozess konnte nicht korrekt gestoppt werden, sollte aber gestoppt werden<br>
 */
public enum ThreadStatus {
    NEW,
    RUNNING,
    ERROR,
    STOPPED,
    FINISHED,
    ZOMBIE;
}
