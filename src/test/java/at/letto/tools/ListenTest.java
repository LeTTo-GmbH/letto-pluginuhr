package at.letto.tools;

import at.letto.globalinterfaces.IdEntity;
import at.letto.globalinterfaces.Unique;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class ListenTest {
    @Getter @AllArgsConstructor
    static
    class Dto implements IdEntity, Unique {
        private int id;
        private int id2;
        private int id3;
        private int val;

        public String key () {
            return id2 + "_" + id3;
        }

        public static String uniqueStatic (Dto x) {
            return x.id2 + "_" + x.id3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dto dto = (Dto) o;
            return id == dto.id && id2 == dto.id2 && id3 == dto.id3 && val == dto.val;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, id2, id3, val);
        }

        @Override
        public String toString() {
            return "Dto{" +
                    "id=" + id +
                    ", id2=" + id2 +
                    ", id3=" + id3 +
                    ", val=" + val +
                    '}';
        }

        public static boolean changed(Dto o1, Dto o2) {
            if (o1==null || o2==null) return false;
            return o1.val != o2.val;
        }
    }

    private List<Dto> init() {
        List<Dto> l = new Vector<>();
        l.add(new Dto(1,1,1,1));
        l.add(new Dto(2,1,2,2));
        l.add(new Dto(3,1,3,3));
        l.add(new Dto(4,2,1,4));
        l.add(new Dto(5,2,2,5));
        l.add(new Dto(6,2,3,6));
        l.add(new Dto(7,1,1,7));
        return l;
    }

    @Test
    public void test_detechtChanges() {
        List<Dto> orig = init();
        List<Dto> edit = init();
        edit.remove(6);
        edit.remove(5);

        edit.add(new Dto(0, 10,10,10));
        edit.add(new Dto(0, 11,11,11));
        edit.add(new Dto(0, 1,1,100));

        edit.get(1).id2 = 22;
        edit.get(2).id2 = 33;
        edit.get(3).val = 33;

        edit = Listen.removeElementsNonUnique(edit,  Dto::key, false);
        ChangeLists<Dto> ret = Listen.detectChanges(edit, orig, Dto::changed);

        Assertions.assertEquals(ret.add.size(), 3);
        Assertions.assertEquals(ret.del.size(), 3);   // ein Element ist mehr zu löschen wegen UNIQUE-Key Fehler
        Assertions.assertEquals(ret.change.get(0).getId(), 4);

        ret = Listen.detectChanges(edit, orig, null);
        Assertions.assertEquals(ret.change.size(), 3);
    }

    @Test
    public void test_change() {
        List<Dto> l = init();
        List<Dto> l2 = new Vector<>();
        l2.add(new Dto(1,4,4,4));
        l2.add(new Dto(2,5,5,5));

        Listen.change(l,l2);
        Assertions.assertEquals(l.get(0).getId2(), 4);
        Assertions.assertEquals(l.get(1).getId2(), 5);
    }


    /** Test der Remove-Funktionen + indexOf */
    @Test
    public void test_Remove() {
        List<Dto> l = init();
        List<Dto> del = init();
        Dto d1 = del.remove(1);
        Dto d2 = del.remove(3);
        Listen.remove(l, del);
        Assertions.assertEquals(l.size(),2);
        Assertions.assertTrue(Listen.indexOf(l, d1.getId())>=0);
        Assertions.assertTrue(Listen.indexOf(l, d2.getId())>=0);

        l = init();
        d1 = del.remove(2);
        Listen.remove(l, d1);
        Assertions.assertTrue(Listen.indexOf(l, d1.getId())==-1);

        // Test removeNotContaing
        l = init();
        Listen.removeNotContaing(l, 2, Dto::getId2);
        Assertions.assertEquals(l.size(),3);
        l.forEach(d->Assertions.assertEquals(d.getId2(),2));
    }



    /** Test der RemoveElements NON UNIQUE - Funktionen */
    @Test
    public void test_RemoveElementsNonUnique() {
        List<Dto> l = init();
        // Test mit IdEntity, unity-String-Definition wird als Instanz-Funktion übergeben
        // Erste Fundstelle wird behalten
        List<Dto> l1 = Listen.removeElementsNonUnique(l, Dto::key, true);
        Assertions.assertEquals(Listen.indexOf(l1, 7), -1);
        Assertions.assertEquals(Listen.indexOf(l1, 1), 0);
        // Letzte Fundstelle wird behalten
        l1 = Listen.removeElementsNonUnique(l, Dto::key, false);
        Assertions.assertEquals(Listen.indexOf(l1, 1), -1);
        Assertions.assertEquals(Listen.indexOf(l1, 7), 0);

        // Test mit IdEntity, unity-String-Definition wird als statische-Funktion übergeben
        // Erste Fundstelle wird behalten
        l1 = Listen.removeElementsNonUniqueStatic(l, Dto::uniqueStatic, true);
        Assertions.assertEquals(Listen.indexOf(l1, 7), -1);
        Assertions.assertEquals(Listen.indexOf(l1, 1), 0);
        // Letzte Fundstelle wird behalten
        l1 = Listen.removeElementsNonUniqueStatic(l, Dto::uniqueStatic, false);
        Assertions.assertEquals(Listen.indexOf(l1, 1), -1);
        Assertions.assertEquals(Listen.indexOf(l1, 7), 0);

        // Test mit Interface Unique, unity-String-Definition wird über Unique-Interface erzwungen
        // Erste Fundstelle wird behalten
        l1 = Listen.removeElementsNonUnique(l,true);
        Assertions.assertEquals(Listen.indexOf(l1, 7), -1);
        Assertions.assertEquals(Listen.indexOf(l1, 1), 0);
        // Letzte Fundstelle wird behalten
        l1 = Listen.removeElementsNonUnique(l, false);
        Assertions.assertEquals(Listen.indexOf(l1, 1), -1);
        Assertions.assertEquals(Listen.indexOf(l1, 7), 0);
    }
}

