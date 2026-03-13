package assignments.ex2;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CellEntryTest {
    @Test
    void testIsValid() {
        // Valid indices
        CellEntry validEntry1 = new CellEntry("A1");
        CellEntry validEntry2 = new CellEntry("B99");
        CellEntry validEntry3 = new CellEntry("c7");
        CellEntry validEntry4 = new CellEntry("b99");
        CellEntry validEntry5 = new CellEntry("D81");

        assertTrue(validEntry1.isValid());
        assertTrue(validEntry2.isValid());
        assertTrue(validEntry3.isValid());
        assertTrue(validEntry4.isValid());
        assertTrue(validEntry5.isValid());
        // Invalid indices
        CellEntry invalidEntry1 = new CellEntry("b");
        CellEntry invalidEntry2 = new CellEntry("A100");
        CellEntry invalidEntry3 = new CellEntry("a188");
        CellEntry invalidEntry4 = new CellEntry(null);
        CellEntry invalidEntry5 = new CellEntry(" ");
        CellEntry invalidEntry6 = new CellEntry("1B");
        CellEntry invalidEntry7 = new CellEntry("!1");

        assertFalse(invalidEntry1.isValid());
        assertFalse(invalidEntry2.isValid());
        assertFalse(invalidEntry3.isValid());
        assertFalse(invalidEntry4.isValid());
        assertFalse(invalidEntry5.isValid());
        assertFalse(invalidEntry6.isValid());
        assertFalse(invalidEntry7.isValid());
    }
    @Test
    void testConstructorAndToString() {
        // Valid entries - ensure they return the correct string representation
        CellEntry entry1 = new CellEntry("A1");
        assertEquals("A1", entry1.toString());
        CellEntry entry2 = new CellEntry("Z81");
        assertEquals("Z81", entry2.toString());
        CellEntry entry3 = new CellEntry("B2");
        assertEquals("B2", entry3.toString());
        CellEntry entry4 = new CellEntry("c99");
        assertEquals("c99", entry4.toString());
        // Invalid entries - should return an empty string
        CellEntry entry5 = new CellEntry("D100");
        assertEquals("", entry5.toString());
        CellEntry entry6 = new CellEntry("a");
        assertEquals("", entry6.toString());
        CellEntry entry7 = new CellEntry("B");
        assertEquals("", entry7.toString());
        CellEntry entry8 = new CellEntry("%2");
        assertEquals("", entry8.toString());
        // Valid entries - ensure they return the correct string representation
        CellEntry entry9=new CellEntry(0,1);
        assertEquals("A1", entry9.toString());
        CellEntry entry10=new CellEntry(1,99);
        assertEquals("B99", entry10.toString());
        CellEntry entry11=new CellEntry(6,13);
        assertEquals("G13", entry11.toString());
        // Invalid entries - should return an empty string
        CellEntry entry12 = new CellEntry(-1,3);
        assertEquals("", entry6.toString());
        CellEntry entry13 = new CellEntry(5,101);
        assertEquals("", entry13.toString());
        CellEntry entry14 = new CellEntry(-10,-5);
        assertEquals("", entry14.toString());
    }
    @Test
    void testGetX() {
        // Valid entries
        CellEntry entry1 = new CellEntry("A1");
        CellEntry entry2 = new CellEntry("B2");
        CellEntry entry3 = new CellEntry("c7");
        CellEntry entry4 = new CellEntry("b99");
        CellEntry entry5 = new CellEntry("D81");

        assertEquals(0, entry1.getX());
        assertEquals(1, entry2.getX());
        assertEquals(2, entry3.getX());
        assertEquals(1, entry4.getX());
        assertEquals(3, entry5.getX());

        // Invalid entries
        CellEntry entry6 = new CellEntry("b");
        CellEntry entry7 = new CellEntry("A100");
        CellEntry entry8 = new CellEntry("a188");
        CellEntry entry9 = new CellEntry(null);
        CellEntry entry10 = new CellEntry(" ");
        CellEntry entry11 = new CellEntry("1B");
        CellEntry entry12= new CellEntry("!1");

        assertEquals(Ex2Utils.ERR, entry6.getX());
        assertEquals(Ex2Utils.ERR, entry7.getX());
        assertEquals(Ex2Utils.ERR, entry8.getX());
        assertEquals(Ex2Utils.ERR, entry9.getX());
        assertEquals(Ex2Utils.ERR, entry10.getX());
        assertEquals(Ex2Utils.ERR, entry11.getX());
        assertEquals(Ex2Utils.ERR, entry12.getX());
    }
    @Test
    void testGetY() {
        // Valid entries
        CellEntry entry1 = new CellEntry("A1");
        CellEntry entry2 = new CellEntry("B99");
        CellEntry entry3 = new CellEntry("C91");
        CellEntry entry4 = new CellEntry("f0");
        assertEquals(1, entry1.getY());
        assertEquals(99, entry2.getY());
        assertEquals(91, entry3.getY());
        assertEquals(0, entry4.getY());
        // Invalid entries
        CellEntry entry5 = new CellEntry("0");
        CellEntry entry6 = new CellEntry("D100");
        CellEntry entry7 = new CellEntry("a");
        CellEntry entry8 = new CellEntry("B");
        CellEntry entry9 = new CellEntry(null);
        CellEntry entry10 = new CellEntry("");
        CellEntry entry11 = new CellEntry("!2");
        assertEquals(Ex2Utils.ERR, entry5.getY());
        assertEquals(Ex2Utils.ERR, entry6.getY());
        assertEquals(Ex2Utils.ERR, entry7.getY());
        assertEquals(Ex2Utils.ERR, entry8.getY());
        assertEquals(Ex2Utils.ERR, entry9.getY());
        assertEquals(Ex2Utils.ERR, entry10.getY());
        assertEquals(Ex2Utils.ERR, entry11.getY());
    }

}
