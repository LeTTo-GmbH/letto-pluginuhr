package at.letto.tools;

import at.letto.globalinterfaces.IdEntity;
import at.letto.globalinterfaces.Unique;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Listen {

    /**
     * Ersetzt in der ersten Liste alle Objekte mit Elementen aus der zweiten Liste,
     * wo die IDs übereinstimmen (Wenn IDs passt, dann wird in der Liste change
     * das Objekt mit der entsprechenden ID aus der Liste l2 ersetzt
     *
     * @param change  zu ändernde Liste
     * @param newVals Liste mit geänderten DTOs, die und andere Liste gespeichert werden sollen
     * @param <T>     Objekte der Listen müssen Interface IdEntity implementiert haben
     */
    public static <T extends IdEntity> void change(List<T> change, List<T> newVals) {
        Map<Integer, T> ids = newVals.stream().collect(Collectors.toMap(IdEntity::getId, x -> x, (x1, x2) -> x1));
        for (int i = 0; i < change.size(); i++) {
            IdEntity el = change.get(i);
            if (ids.containsKey(el.getId()))
                change.set(i, ids.get(el.getId()));
        }
    }

    /**
     * Löschen von Objekten aus Liste
     *
     * @param change Liste, aus der gelöscht werden soll
     * @param del    zu löschende Elemente
     * @param <T>    Interface IdEntity: Klasse muss Getter mit getId() haben
     */
    public static <T extends IdEntity> void remove(List<T> change, List<T> del) {
        Map<Integer, T> ids = del.stream().collect(Collectors.toMap(IdEntity::getId, x -> x, (x1, x2) -> x1));
        Iterator<T> i = change.iterator();
        while (i.hasNext()) {
            IdEntity el = i.next();
            if (ids.containsKey(el.getId()))
                i.remove();
        }
    }

    /**
     * Entfernen van allen Listen-Elementen, wo eine ID nicht passend ist
     *
     * @param change List, die geändert/kontrolliert werden soll
     * @param id     ID, die erfüllt sein muss
     * @param selId  Getter-Methode des Objekts, die die zu kontrollierende ID liefert
     * @param <T>    Elemented der Liste müssen Interface IdEntity implementiert haben
     */
    public static <T extends IdEntity> void removeNotContaing(List<T> change, int id, Function<T, Integer> selId) {
        Iterator<T> i = change.iterator();
        while (i.hasNext()) {
            T el = i.next();
            if (selId.apply(el) != id)
                i.remove();
        }
    }

    /**
     * Löschen eines Objects mit ID
     *
     * @param change Liste, aus der gelöscht werden soll
     * @param l2     zu löschendes Element
     * @param <T>    Interface IdEntity: Klasse muss Getter mit getId() haben
     */
    public static <T extends IdEntity> void remove(List<T> change, IdEntity l2) {
        Iterator<T> i = change.iterator();
        while (i.hasNext()) {
            IdEntity el = i.next();
            if (l2.getId() == el.getId())
                i.remove();
        }
    }

    /**
     * Bestimmung des Indexes eines Objekts (IdEntity) in einer Liste
     *
     * @param list Zu durchsuchende Liste von DTOs
     * @param id   ID, die gesucht wird
     * @param <T>  Typ generisch
     * @return Index oder -1, wenn die ID nicht gefunden wurde
     */
    public static <T extends IdEntity> int indexOf(List<T> list, int id) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getId() == id) return i;

        return -1;
    }

    /**
     * Hinzufügen eines Elements mit einer ID.
     * Die ID in der Liste soll eindeutig sein.
     * Deswegen wird bei gleicher ID das Element ersetzt, sonst hibzugefügt.
     * @param list  List, zu der Element hinzugefügt wird
     * @param item  Neuer Eintrag (Änderung oder neu)
     * @param <T>   Typ generisch
     */
    public static <T extends IdEntity> void add(List<T> list, T item) {
        int i = indexOf(list, item.getId());
        if (i>=0)
            list.set(i, item);
        else
            list.add(item);
    }


        /**
         * Scuhen eines Elements in der Liste nach dessen ID
         *
         * @param list Zu suchende Liste
         * @param id   ID des DTO-Objekts
         * @param <T>  Typ generisch
         * @return gefundenes Listenelement oder null
         */
    public static <T extends IdEntity> T get(List<T> list, int id) {
        for (T el : list) {
            if (el.getId() == id) return el;
        }
        return null;
    }

    public static <T extends IdEntity> T get(List<T> list, int id, Function<T, Integer> searchCol) {
        for (T el : list) {
            if (searchCol.apply(el) == id) return el;
        }
        return null;
    }


    /**
     * Ermittlung der Unterschiede von zwei Listen von Objekten
     *
     * @param save Liste mit Objekten, die gespeichert gehören
     * @param orig Original-Liste, meistens Liste mit DTOs aus Datebnbank geladen
     * @param eq   Optional: BiPredicate-Funktion zum Vergleich von Objekten, ob im DTO eine Änderung erfolgte
     *             ist eq null, dann wird equals zurm Vergleich herangezogen
     * @param <T>  Klasse mit implementiertem Interface IdEntity (getId)
     * @return 3 Listen (add, del und change), geben an welche DTOs in DB
     * hinzugefügt, gelöscht oder geändert werden sollen
     */
    public static <T extends IdEntity> ChangeLists<T> detectChanges(List<T> save, List<T> orig, BiPredicate<T, T> eq) {
        // Suche nach Rechten, die noch nicht gespeichert sind
        List<T> add = save.stream()
                .filter(r1 -> orig.stream().noneMatch(rx -> Objects.equals(rx.getId(), r1.getId())))
                .collect(Collectors.toList());

        // Suche nach Rechten, die gelöscht gehören
        List<T> del = orig.stream()
                .filter(r1 -> save.stream().noneMatch(rx -> Objects.equals(rx.getId(), r1.getId())))
                .collect(Collectors.toList());

        // Suche nach Rechten, die geändert gehören
        List<T> change = save.stream()
                .filter(r1 -> orig.stream().anyMatch(rx ->
                        eq != null ?
                                Objects.equals(rx.getId(), r1.getId()) && eq.test(rx, r1) :
                                Objects.equals(rx.getId(), r1.getId()) && !Objects.equals(rx, r1)))
                .collect(Collectors.toList());
        return new ChangeLists(add, del, change);
    }

    /**
     * Ermittlung von einzufügenden Objekten und zu Objekten, die zu löschen sind.
     * Es erfolgt keine Gleichheitsprüfung.
     * Die Listen können unterschiedliche Datentypen enthalten,
     * verglichen werden die IDs der Objekte. (Alle Objekte müssen IdEntity implementiert haben!)
     *
     * @param save Liste mit Objekten, die gespeichert gehören
     * @param orig Original-Liste, die an save angepasst werden soll
     * @param <T>  Klasse mit implementiertem Interface IdEntity (getId)
     * @param <T1> Klasse mit implementiertem Interface IdEntity (getId)
     * @return 3 Listen (add, del und change), geben an welche DTOs in DB
     * hinzugefügt, gelöscht oder geändert werden sollen
     */
    public static <T extends IdEntity, T1 extends IdEntity> ChangeLists<T> detectChanges(List<T> save, List<T1> orig) {
        List<T> add = (List)save.stream().filter((r1) -> {
            return orig.stream().noneMatch((rx) -> {
                return Objects.equals(rx.getId(), r1.getId());
            });
        }).collect(Collectors.toList());
        List<T> del = (List)orig.stream().filter((r1) -> {
            return save.stream().noneMatch((rx) -> {
                return Objects.equals(rx.getId(), r1.getId());
            });
        }).collect(Collectors.toList());
        return new ChangeLists(add, del, null);
    }

    /**
     * Entfernen von Elementen, die den selben Unique-String haben.
     * ACHTUNG: Die URSPRUNGSLISTE wird NICHT verändert!
     *
     * @param list  Zu bearbeitende Liste
     * @param first true: das erste gefundene Element bleibt erhalten, sonst das letzte
     * @param <T>   Elemnte der Liste müssen das Interface Unique implementiert haben:
     *              Liefert einen String mit dem Unique-Beuzeichner, der eindeutig sein soll
     * @return Liste mit Objekten, wobei alle mehrdeutigkeiten entfernt wurden
     */
    public static <T extends Unique> List<T> removeElementsNonUnique(List<T> list, boolean first) {
        return first ?
                list.stream()
                        .collect(Collectors.toMap((x) -> x.key(), x -> x, (x1, x2) -> x1))
                        .values().stream().collect(Collectors.toList()) :

                list.stream()
                        .collect(Collectors.toMap((x) -> x.key(), x -> x, (x1, x2) -> x2))
                        .values().stream().collect(Collectors.toList());

    }

    /**
     * Entfernen von Elementen, die den selben Unique-String haben
     * ACHTUNG: Die URSPRUNGSLISTE wird NICHT verändert!
     *
     * @param list   Zu bearbeitende Liste
     * @param unique Statische Funktion mit Parameter IdEntity:
     *               Function-Interface, das aus dem Objekt einen UNIQUE-String zurückgibt,
     *               der eindeutig sein muss.
     * @param first  true: das erste gefundene Element bleibt erhalten, sonst das letzte
     * @param <T>    Elemnte der Liste müssen das Interface Unique implementiert haben:
     *               Liefert einen String mit dem Unique-Beuzeichner, der eindeutig sein soll
     * @return Liste mit Objekten, wobei alle mehrdeutigkeiten entfernt wurden
     */
    public static <T extends IdEntity> List<T> removeElementsNonUniqueStatic(
            List<T> list,
            Function<T, String> unique,
            boolean first) {
        return first ?
                list.stream().collect(Collectors.toMap((x) -> unique.apply(x), x -> x, (x1, x2) -> x1))
                        .values().stream().collect(Collectors.toList()) :
                list.stream().collect(Collectors.toMap((x) -> unique.apply(x), x -> x, (x1, x2) -> x2))
                        .values().stream().collect(Collectors.toList());
    }

    /**
     * Entfernen von Elementen, die den selben Unique-String haben
     * ACHTUNG: Die URSPRUNGSLISTE wird NICHT verändert!
     *
     * @param list   Zu bearbeitende Liste
     * @param unique Function-Interface (Instanzmethode), das aus dem Objekt
     *               einen UNIQUE-String zurückgibt, der eindeutig sein muss
     * @param first  true: das erste gefundene Element bleibt erhalten, sonst das letzte
     * @param <T>    Elemnte der Liste müssen das Interface Unique implementiert haben:
     *               Liefert einen String mit dem Unique-Beuzeichner, der eindeutig sein soll
     * @return Liste mit Objekten, wobei alle mehrdeutigkeiten entfernt wurden
     */
    public static <T extends IdEntity> List<T> removeElementsNonUnique(
            List<T> list,
            Function<T, String> unique,
            boolean first) {
        return first ?
                list.stream().collect(Collectors.toMap((x) -> unique.apply(x), x -> x, (x1, x2) -> x1))
                        .values().stream().collect(Collectors.toList()) :
                list.stream().collect(Collectors.toMap((x) -> unique.apply(x), x -> x, (x1, x2) -> x2))
                        .values().stream().collect(Collectors.toList());
    }


    /**
     * Sortierung einer Liste in der Datenbank:
     * Methode liefert ids und sort-Order für spätere Update-Query
     *
     * @param list   sortierte Liste
     * @param getter Getter auf Sortierungsspalte
     * @param <T> Typ generisch
     * @return String mit ids und sortOrter in der Form (id1, sort1),(id2,sort2),....
     */
    public static <T extends IdEntity> String sqlUpdateSort(List<T> list, Function<T, Integer> getter) {
        List<String> changes = new Vector<>();
        for (T t : list)
            changes.add("(" + t.getId() + "," + getter.apply(t) + ")");

        return changes.stream().collect(Collectors.joining(","));
    }

    /**
     * Nachtragen einer SortColumn im Objekt aufgrund der aktuellen Reihenfolge
     *
     * @param list   sortierte Liste
     * @param setter Methodenreferenz auf Setter der sortColumn
     * @param <T>    Typ generisch
     */
    public static <T extends IdEntity> void sortColumn(List<T> list, BiConsumer<T, Integer> setter) {
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            setter.accept(t, i);
        }
    }

    public static <T> List<T> vereinigungsmenge(List<T> list1, List<T> list2) {
        List<T> res = new Vector<>();
        for (T e : list1) {
            if (list2.contains(e))
                res.add(e);
        }
        return res;
    }

    public static <T extends IdEntity> void sortColumn(List<T> list) {

    }
}