package assignments.ex2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Ex2SheetTest {

    @Test
    public void testSetAndGet() {//tests for set,get and value
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(1, 1, "5");
        assertEquals("5.0", sheet.value(1, 1));
        sheet.set(55, 1, "5");
        assertEquals("", sheet.value(55, 1));
        sheet.set(2, 2, "A1 + 2");
        assertEquals("A1 + 2", sheet.value(2, 2));
        sheet.set(0, 0, "=a0+5");
        assertEquals("=a0+5", sheet.value(0, 0));
    }
    @Test
    public void testWidthAndHeight() {//tests for consructor ,default constructor and height and width
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals(5,sheet.height());
        assertEquals(5,sheet.width());
        Ex2Sheet sheet1 = new Ex2Sheet();
        assertEquals(Ex2Utils.HEIGHT,sheet1.height());
        assertEquals(Ex2Utils.WIDTH,sheet1.width());
        Ex2Sheet sheet2 = new Ex2Sheet(0,0);
        assertEquals(0,sheet2.height());
        assertEquals(0,sheet2.width());
        Ex2Sheet sheet3 = new Ex2Sheet(5,0);
        assertEquals(0,sheet3.height());
        assertEquals(0,sheet3.width());
        Ex2Sheet sheet4 = new Ex2Sheet(-2,2);
        assertEquals(0,sheet4.height());
        assertEquals(0,sheet4.width());
    }


    @Test
    public void testIsIn() {//tests fo isIn(x,y)
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertTrue(sheet.isIn(0, 0));
        assertTrue(sheet.isIn(4, 4));
        assertFalse(sheet.isIn(-1, 0));
        assertFalse(sheet.isIn(5, 5));
        assertFalse(sheet.isIn(-5, -5));
    }

    @Test
    public void testEval() {//tests for eval() and therefore eval(int x, int y)
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");//a0
        sheet.set(2, 2, "maayan");//c2
        sheet.set(3, 3, "ERR_FORM!");
        sheet.set(4, 4, "ERR_CYCLE!");
        sheet.set(1, 1, "=A0 + 5");
        sheet.set(1, 3, "=A0 + B3");//b3 err cyc
        sheet.set(0, 1, "=A2 + B1");//err from
        sheet.set(1, 4, "=(2+(5*(5-1)))*2+(3*(1/2))*12");
        sheet.set(2, 4, "=C2+A0");
        sheet.set(2, 3, "=a1");//c3
        sheet.set(0, 3, "=c3+b3");
        sheet.set(4, 1, null);
        sheet.set(4, 2, "");
        sheet.set(4, 3, "=7-(-7)");
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.eval(4, 0));
        assertEquals("15.0", sheet.eval(1, 1));
        assertEquals("ERR_FORM!", sheet.eval(0, 1));//a1
        assertEquals("10.0", sheet.eval(0, 0));
        assertEquals("maayan", sheet.eval(2, 2));
        assertEquals("ERR_FORM!", sheet.eval(3, 3));
        assertEquals("ERR_CYCLE!", sheet.eval(4, 4));
        assertEquals("ERR_CYCLE!", sheet.eval(1, 3));//b3
        assertEquals("62.0", sheet.eval(1, 4));
        assertEquals("ERR_FORM!", sheet.eval(2, 4));
        assertEquals("ERR_FORM!", sheet.eval(2, 3));
        assertEquals("", sheet.eval(4, 2));
        assertEquals("14.0", sheet.eval(4, 3));
    }


    @Test
    public void testLoadAndSave() throws IOException, IOException {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "A1 + 5");
        sheet.save("test.csv");

        Ex2Sheet newSheet = new Ex2Sheet(5, 5);
        newSheet.load("test.csv");

        assertEquals("10.0", newSheet.value(0, 0));
        assertEquals("A1 + 5", newSheet.value(1, 1));
    }

    @Test
    public void testDepth() {//test for depth() and therefore also for computeDepth(int x, int y, int[][] depths, Ex2Sheet sheet)
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A0 + 5");
        sheet.set(0, 1, "=A1 + 5");//a1
        sheet.set(1, 2, "=A0 + c5");//b2
        sheet.set(4, 4, "=8-(6-6)");
        sheet.set(2, 3, "=b2");
        sheet.set(3, 3, "=a1+6");
        sheet.set(0, 3, "=a1+b2");
        int[][] depths = sheet.depth();
        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[1][1]);
        assertEquals(-1, depths[0][1]);//a1
        assertEquals(-2, depths[1][2]);//b2
        assertEquals(1, depths[4][4]);
        assertEquals(-2, depths[2][3]);
        assertEquals(-1, depths[3][3]);
        assertEquals(-1, depths[0][3]);
        sheet.set(0, 0, "10");//a0
        sheet.set(2, 2, "maayan");//c2
        sheet.set(3, 3, "ERR_FORM!");//
        sheet.set(4, 4, "ERR_CYCLE!");
        sheet.set(1, 1, "=A0 + 5");
        sheet.set(1, 3, "=A0 + B3");//b3
        sheet.set(0, 1, "=A2 + B5");//a1
        sheet.set(1, 4, "=8-(6-6)");
        sheet.set(2, 4, "=C2+A0");
        sheet.set(2, 3, "=a1");
        sheet.set(0, 3, "=E4+D3");
        assertEquals(-1, depths[0][ 3]);

    }

    @Test
    public void testDependencies() {
        String[] deps = Ex2Sheet.cellReferencesInParam("=A1 + B1");
        assertArrayEquals(new String[]{"A1", "B1"}, deps);
        String[] deps1 = Ex2Sheet.cellReferencesInParam("=8 -(6-6)");
        assertArrayEquals(new String[]{}, deps1);
        String formula1 = "=a1+b2";
        String formula2 = "=(A3*C4)/D5";
        String formula3 = "((E10+F20)-G30)";
        String formula4 = "=H1+I2+J3";
        String formula5 = "=(I9+10)-(11-J10)";
        assertArrayEquals(new String[]{"a1", "b2"},Ex2Sheet.cellReferencesInParam(formula1));
        assertArrayEquals(new String[]{"A3", "C4", "D5"},Ex2Sheet.cellReferencesInParam(formula2));
        assertArrayEquals(new String[]{"E10", "F20", "G30"}, Ex2Sheet.cellReferencesInParam(formula3));
        assertArrayEquals(new String[]{"H1", "I2", "J3"}, Ex2Sheet.cellReferencesInParam(formula4));
        assertArrayEquals(new String[]{"I9", "J10"},  Ex2Sheet.cellReferencesInParam(formula5));


    }
    @Test
    void testGetByCoordinates() {
        Ex2Sheet sheet = new Ex2Sheet(7, 7);

        // Set up some cells
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A0+5");
        sheet.set(4, 4, "hello");

        // Valid indices
        assertNotNull(sheet.get(0, 0)); // Check that cell exists
        assertEquals("10", sheet.get(0, 0).getData()); // Check data
        assertNotNull(sheet.get(1, 1)); // Check that cell exists
        assertEquals("=A0+5", sheet.get(1, 1).getData()); // Check data
        assertNotNull(sheet.get(4, 4)); // Check that cell exists
        assertEquals("hello", sheet.get(4, 4).getData()); // Check data

        // Invalid indices
        assertNull(sheet.get(-1, 0));
        assertNull(sheet.get(0, -1));
        assertNull(sheet.get(8, 0));
        assertNull(sheet.get(0, 8));
    }

    @Test
    void testGetByStringReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);

        // Set up some cells
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=A0+5");
        sheet.set(4, 4, "hi");

        // Valid string references
        assertNotNull(sheet.get("A0")); // Check that cell exists
        assertEquals("10", sheet.get("A0").getData()); // Check data
        assertNotNull(sheet.get("B1")); // Check that cell exists
        assertEquals("=A0+5", sheet.get("B1").getData()); // Check data
        assertNotNull(sheet.get("E4")); // Check that cell exists
        assertEquals("hi", sheet.get("E4").getData()); // Check data

        // Invalid string references
        assertNull(sheet.get("Z10"));
        assertNull(sheet.get("A-1"));
        assertNull(sheet.get("10A"));
        assertNull(sheet.get(""));
        assertNull(sheet.get(null));
        assertNull(sheet.get("A100"));
    }

}
