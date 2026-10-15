package at.letto.tools;

import at.letto.globalinterfaces.IdEntity;

import java.util.List;

/**
 * Rückgabeobjekt der Methode {@link Listen#detectAddDel(List, List)}:
 * Liste der neu hinzuzufügenden Elemente (Typ der Speicher-Liste) und
 * Liste der zu löschenden Elemente (Typ der Original-Liste).
 *
 * @param add  Elemente aus der Speicher-Liste, die in der Original-Liste fehlen
 * @param del  Elemente aus der Original-Liste, die in der Speicher-Liste fehlen
 * @param <T>  Typ der Speicher-Liste (meist DTO)
 * @param <T1> Typ der Original-Liste (meist Entity)
 */
public record AddDelLists<T extends IdEntity, T1 extends IdEntity>(List<T> add, List<T1> del) {
}
