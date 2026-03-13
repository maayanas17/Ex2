package assignments.ex2;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SCellTest
{
    @Test
    void testConstructorAndGetters() {
        SCell cell = new SCell("5");
        assertEquals("5", cell.getData());
        assertEquals(2, cell.getType());
        cell = new SCell("=(1+2)*((3))-1");
        assertEquals("=(1+2)*((3))-1", cell.getData());
        assertEquals(3, cell.getType());
        cell = new SCell("=(2+A3)/A2");
        assertEquals("=(2+A3)/A2", cell.getData());
        assertEquals(3, cell.getType());
        cell = new SCell("hello");
        assertEquals("hello", cell.getData());
        assertEquals(1, cell.getType());
        cell = new SCell("=hello");
        assertEquals("=hello", cell.getData());
        assertEquals(-1, cell.getType());
        cell = new SCell("");
        assertEquals("", cell.getData());
        assertEquals(-1, cell.getType());
        cell = new SCell(null);
        assertEquals("", cell.getData());
        assertEquals(-1, cell.getType());
    }

    @Test
    void testSetDataAndGetType() {
        SCell cell = new SCell("5");
        assertEquals("5", cell.getData());
        assertEquals(2, cell.getType());
        cell.setData("=A1+5");
        assertEquals("=A1+5", cell.getData());
        assertEquals(3, cell.getType());
        cell.setData("hello ");
        assertEquals("hello ", cell.getData());
        assertEquals(1, cell.getType());
        cell.setData("=hello ");
        assertEquals("=hello ", cell.getData());
        assertEquals(-1, cell.getType());
        cell.setData("");
        assertEquals("", cell.getData());
        assertEquals(-1, cell.getType());
        cell.setData(null);
        assertEquals("", cell.getData());
        assertEquals(-1, cell.getType());
    }
    @Test
    void testSetTypeAndGetType() {
        SCell cell = new SCell("5");
        assertEquals("5", cell.getData());
        assertEquals(2, cell.getType());
        cell.setType(1);
        assertEquals(1, cell.getType());
        cell.setType(2);
        assertEquals(2, cell.getType());
        cell.setType(-1);
        assertEquals(-1, cell.getType());
        cell.setType(-2);
        assertEquals(-2, cell.getType());
    }

    @Test
    void testToString() {
        SCell cell = new SCell("Hello");
        assertEquals("Hello", cell.toString());
        cell.setData("=A1+5");
        assertEquals("=A1+5", cell.toString());
        cell.setData("5");
        assertEquals("5", cell.toString());
        cell.setData("=hello ");
        assertEquals("=hello ", cell.toString());
        cell.setData("");
        assertEquals("", cell.toString());
        cell.setData(null);
        assertEquals("", cell.toString());
    }

    @Test
    void testDeterminationType() {
        assertEquals(2, SCell.determinationType("42"));
        assertEquals(1, SCell.determinationType("Hello"));
        assertEquals(-1, SCell.determinationType("=5**2"));
        assertEquals(-1, SCell.determinationType("=a"));
        assertEquals(-1, SCell.determinationType("=AB"));
        assertEquals(-1, SCell.determinationType("=@2"));
        assertEquals(-1, SCell.determinationType("==2+)"));
        assertEquals(-1, SCell.determinationType("=5**"));
        assertEquals(-1, SCell.determinationType("=()"));
        assertEquals(3, SCell.determinationType("=A1+5"));
        assertEquals(3, SCell.determinationType("=1.2"));
        assertEquals(3, SCell.determinationType("=1+2*3"));
        assertEquals(3, SCell.determinationType("=(1+2)*((3))-1"));
        assertEquals(-1, SCell.determinationType(null));
        assertEquals(-1, SCell.determinationType(""));
    }

    @Test
    void testIsNumber() {
        assertTrue(SCell.isNumber("42"));
        assertTrue(SCell.isNumber("-3.14"));
        assertFalse(SCell.isNumber("Hello"));
        assertFalse(SCell.isNumber("()"));
        assertFalse(SCell.isNumber("=42"));
        assertFalse(SCell.isNumber(""));
        assertFalse(SCell.isNumber(null));
    }

    @Test
    void testIsText() {
        assertTrue(SCell.isText("Hello"));
        assertTrue(SCell.isText("HFG7"));
        assertTrue(SCell.isText("WORLD*A2"));
        assertFalse(SCell.isText("=Hello"));
        assertFalse(SCell.isText(null));
        assertFalse(SCell.isText(""));
    }

    @Test
    void testIsOperator() {
        assertTrue(SCell.isOperator('+'));
        assertTrue(SCell.isOperator('-'));
        assertTrue(SCell.isOperator('*'));
        assertTrue(SCell.isOperator('/'));
        assertFalse(SCell.isOperator('^'));
        assertFalse(SCell.isOperator(' '));
    }

    @Test
    void testIsCell() {
        assertTrue(SCell.isCell("A1"));
        assertTrue(SCell.isCell("B12"));
        assertFalse(SCell.isCell("12A"));
        assertFalse(SCell.isCell("Hello"));
        assertFalse(SCell.isCell(""));
        assertFalse(SCell.isCell(null));
    }

    @Test
    void testIsForm() {
        assertTrue(SCell.isForm("=1+2"));
        assertTrue(SCell.isForm("=(1+2)*((3))-1"));
        assertTrue(SCell.isForm("=((1+2)+((3)))"));
        assertTrue(SCell.isForm("=(A1+2)*(B3-1)"));
        assertTrue(SCell.isForm("=(2+(5*(5-1)))*2+(3*(1/2))*12"));
        assertTrue(SCell.isForm("=(2+(5*(5-1)))*2"));
        assertTrue(SCell.isForm("=8-(6-6)"));
        assertTrue(SCell.isForm("=a1+b0"));
        assertTrue(SCell.isForm("=A1 + B0"));
        assertTrue(SCell.isForm("=10/(5-5)"));
        assertTrue(SCell.isForm("=7/(-7)"));
        assertFalse(SCell.isForm("1+2"));
        assertFalse(SCell.isForm("=(1+2"));
        assertFalse(SCell.isForm("=maayan"));
        assertFalse(SCell.isForm("=)1+2("));
        assertFalse(SCell.isForm("=()"));
        assertFalse(SCell.isForm(""));
        assertFalse(SCell.isForm(null));
    }

    @Test
    void testGetWeight() {
        assertEquals(0.25, SCell.getWeight('+', 0));
        assertEquals(0.5, SCell.getWeight('*', 0));
        assertEquals(1.25, SCell.getWeight('-', 1));
        assertEquals(0, SCell.getWeight(' ', 25));
    }

    @Test
    void testIndexMainOperator() {
        assertEquals(5, SCell.IndexMainOperator("(1+2)*3"));
        assertEquals(5, SCell.IndexMainOperator("(1+2)+(3-4)"));
        assertEquals(14, SCell.IndexMainOperator("(((1+2)*(3-4))+9)"));
        assertEquals(1, SCell.IndexMainOperator("7-(-7)"));
        assertEquals(0, SCell.IndexMainOperator("-7"));
        assertEquals(-1, SCell.IndexMainOperator("42"));
        assertEquals(-1, SCell.IndexMainOperator("=42"));
        assertEquals(-1, SCell.IndexMainOperator(""));
        assertEquals(-1, SCell.IndexMainOperator(null));
    }
    @Test
    void testRemoveParenthses() {
        assertEquals("1+2", SCell.removeParenthses("((1+2))"));
        assertEquals("(1+2)*3", SCell.removeParenthses("((1+2)*3)"));
        assertEquals("1+2", SCell.removeParenthses("(1+2)"));
        assertEquals("((1+2)*(3-4))+9", SCell.removeParenthses("(((1+2)*(3-4))+9)"));
        assertEquals("5", SCell.removeParenthses("(5)"));
        assertEquals("(1+2)+(5)", SCell.removeParenthses("(1+2)+(5)"));
        assertEquals("", SCell.removeParenthses(""));
        assertEquals("", SCell.removeParenthses(null));
    }
    @Test
    void testComputeFormOnlyDigit(){
        assertEquals("9.0", SCell.computeFormOnlyDigit("=(1+2)*3"));
        assertEquals("3.0", SCell.computeFormOnlyDigit("=1+2"));
        assertEquals("6.0", SCell.computeFormOnlyDigit("=(((1+2)*(3-4))+9)"));
        assertEquals("2.0", SCell.computeFormOnlyDigit("=1*2"));
        assertEquals("Infinity", SCell.computeFormOnlyDigit("=10/(5-5)"));
        assertEquals("9.0", SCell.computeFormOnlyDigit("=((1+2)+((3*2)))"));
        assertEquals("8.0", SCell.computeFormOnlyDigit("=8-(6-6)"));
        assertEquals("10.0", SCell.computeFormOnlyDigit("=10"));
        assertEquals("14.0", SCell.computeFormOnlyDigit("=7-(-7)"));
        assertEquals("", SCell.computeFormOnlyDigit(""));
        assertEquals("", SCell.computeFormOnlyDigit(null));
    }
}
