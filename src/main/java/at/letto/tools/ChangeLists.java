package at.letto.tools;

import at.letto.globalinterfaces.IdEntity;
import lombok.Getter;

import java.util.List;

/**
 * Rückgabeobjekt der Methode detectChanges aus Listen:
 * Gibt die gefundenen Änderungen inf Form von 3 Listen zurück:
 * Neue Elemente, zu löschende Elemente und Änderungen
 */
@Getter
public class ChangeLists <T extends IdEntity> {
    public ChangeLists(List<T> add, List<T> del, List<T> change) {
        this.add =    add;
        this.del =    del;
        this.change = change;
    }
    /** Liste it Elementen, die hinzugefügt werden sollen */
    List<T> add;
    /** Liste it Elementen, die gelöscht werden sollen */
    List<T> del;
    /** Liste it Elementen, die geändert werden sollen */
    List<T> change;
}
