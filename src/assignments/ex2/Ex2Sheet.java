
package assignments.ex2;
import com.sun.jdi.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static assignments.ex2.SCell.*;
import static assignments.ex2.SCell.computeFormOnlyDigit;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    // Constructor to initialize the sheet with specified dimensions.
    public Ex2Sheet(int x, int y) {
        if (x<=0||y<=0)
            table=null;
        else {
            table = new SCell[x][y];
            for (int i = 0; i < x; i = i + 1) {
                for (int j = 0; j < y; j = j + 1) {
                    table[i][j] = new SCell("");
                }
            }
            eval();// Evaluate all cells upon initialization
        }
    }
    // Default constructor for the sheet with predefined dimensions.
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        Cell c = get(x,y);
        if (isIn(x,y)) {
            if(isNumber(c.toString()))
            {
                return Double.toString(Double.parseDouble(c.toString())); // Convert number to String
            }
            if(c!=null) {ans = c.toString();}// Return the content of the cell
        }
        return ans;
    }
//Retrieve the cell at specified coordinates
    @Override
    public Cell get(int x, int y) {
        if(isIn(x, y))
         return table[x][y];
        return null;
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        CellEntry cordsCellEntry = new CellEntry(cords);
        if (!cordsCellEntry.isValid())
            return null; // Invalid cell reference
        int x=cordsCellEntry.getX();
        int y=cordsCellEntry.getY();
        if (isIn(x,y))
            return table[x][y];
        return ans;
    }
    // Width of the sheet
    @Override
    public int width() {
        if(table==null||table.length<=0||table[0].length<=0) //my assumption
            return 0;
        return table.length;
    }
    // Height of the sheet
    @Override
    public int height() {
        if(table==null||table.length<=0||table[0].length<=0) //my assumption
            return 0;
        return table[0].length;
    }
    @Override
    public void set(int x, int y, String s) {
        if (isIn(x,y)) {
            Cell c = new SCell(s);// Create a new cell with the given value
            table[x][y] = c;
        }
    }
    @Override
    public void eval() {
        int originalType;
        String value;
        Cell cell;
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                value = eval(x, y);// Evaluate the cell
                cell = get(x, y);
                originalType = cell.getType();// Keep the original type

                cell.setData(value); // Update the cell's data after evaluation
                if (value.equals(Ex2Utils.ERR_FORM))
                    cell.setType(-2); // Mark as invalid formula
                else if (value.equals(Ex2Utils.ERR_CYCLE))
                    cell.setType(-1); // Mark as a cyclic dependency
                else
                    cell.setType(originalType);// Restore the original type
            }
        }
    }

    // Check if coordinates are within bounds
    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;
        return ans&&xx<width()&&yy<height();
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = -1;// Initialize all cells with unexplored depth
            }
        }
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = computeDepth(i,j,ans,this);
            }
        }
        return ans;
    }
    //Compute the depth of dependencies for a given cell
    public int computeDepth(int x, int y, int[][] depths, Ex2Sheet sheet) {
        int maxDependencyDepth, depDepth;
        if (!sheet.isIn(x, y)) {// Cell is out of bounds
            return Ex2Utils.ERR_FORM_FORMAT;
        }
        if (depths[x][y] == -4) {// Cyclic dependency detected
            return Ex2Utils.ERR_CYCLE_FORM;
        }
        if (depths[x][y] != -1) {
            return depths[x][y];// Depth already computed
        }
        String value = sheet.value(x, y);
        if (isNumber(value) || isText(value)) {// Numbers or text have a depth of 0
            depths[x][y] = 0;
            return 0;
        }
        if (isForm(value)) {
            maxDependencyDepth = 0;
            depths[x][y] = -4;// Mark as visiting for cycle detection
            String[] dependArray = cellReferencesInParam(value);
            for (String depend : dependArray) {
                CellEntry depCell = new CellEntry(depend);
                int dependX = depCell.getX();
                int dependY = depCell.getY();
                if (!sheet.isIn(dependX, dependY)) {
                    depths[x][y] = -2; // Invalid dependency
                    return Ex2Utils.ERR_FORM_FORMAT;
                }
                if (sheet.value(dependX, dependY).equals(Ex2Utils.EMPTY_CELL)) {
                    depths[x][y] = -2; // Dependency refers to an empty cell
                    return Ex2Utils.ERR_FORM_FORMAT;
                }
                depDepth = computeDepth(dependX, dependY, depths, sheet);
                if (depDepth < 0) {
                    depths[x][y] = depDepth; //error
                    return depDepth;
                }
                maxDependencyDepth = Math.max(maxDependencyDepth, depDepth);
            }
            depths[x][y] = maxDependencyDepth + 1;
            return depths[x][y];
        }
        depths[x][y] = -2;// Invalid formula format
        return Ex2Utils.ERR_FORM_FORMAT;
    }
    /**
     * Extracts all cell references from a formula.
     */
    public static String[] cellReferencesInParam(String formula) {
        String regex = "[A-Za-z]+\\d+";// Regular expression to match cell references like A1, B12.
        Pattern pattern = Pattern.compile(regex);// Compile the regular expression into a pattern.
        Matcher matcher = pattern.matcher(formula);// Create a matcher to find matches in the formula.

        ArrayList<String> references = new ArrayList<>();// List to store found references.
        while (matcher.find()) {// Loop through all matches in the formula.
            references.add(matcher.group());// Loop through all matches in the formula.
        }
        return references.toArray(new String[0]);// Convert the list of references to an array and return it.
    }
    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine();// Skip the first line, which is a header line in the file.
            String line;
            // Loop through each line in the file after the header.
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);// Split the line into parts: x, y, and value.
                if (parts.length < 3) continue;// Split the line into parts: x, y, and value.

                int x = Integer.parseInt(parts[0].trim());// Split the line into parts: x, y, and value.
                int y = Integer.parseInt(parts[1].trim());// Split the line into parts: x, y, and value.
                String value = parts[2].trim();// Split the line into parts: x, y, and value.

                if (isIn(x, y)) {// Split the line into parts: x, y, and value.
                    set(x, y, value);// Split the line into parts: x, y, and value.
                }
            }
        }
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment");// Write the header line.
            writer.newLine();// Add a newline after the header.
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    String value = value(x, y);// Get the value of the current cell.
                    if (!value.equals(Ex2Utils.EMPTY_CELL)) {// Skip empty cells.
                        writer.write(x + "," + y + "," + value);// Write the cell's coordinates and value.
                        writer.newLine();
                    }// Write the cell's coordinates and value.
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        if (!this.isIn(x, y)) // Check if the cell is within bounds of the sheet.
            return Ex2Utils.ERR_FORM;
        String value = value(x, y);
        value=removeParenthses(value);
        boolean errCyc=false,errForm=false;
        if (value.equals(Ex2Utils.EMPTY_CELL))// Check if the cell is empty.
            return Ex2Utils.EMPTY_CELL;
        int[][] depths = depth();// Calculate the depth of dependencies for all cells.
        int dep = depths[x][y];
        if (dep == Ex2Utils.ERR_CYCLE_FORM) {// Check if there's a cyclic dependency.
            return Ex2Utils.ERR_CYCLE;
        }
        if (dep == Ex2Utils.ERR_FORM_FORMAT) {// Check if there's a format error in the formula.
            return Ex2Utils.ERR_FORM;
        }
        if (isNumber(value)) {
            return value(x, y);
        }
        if (isText(value)) {
            return value;
        }
        if (isForm(value)) {
            String[] dependArray = cellReferencesInParam(value);// Extract cells
            for (String depend : dependArray) {
                CellEntry depCell = new CellEntry(depend);
                int dependX = depCell.getX();
                int dependY = depCell.getY();
                if (!this.isIn(dependX, dependY)) {
                    return Ex2Utils.ERR_FORM;
                }
                String depEval = eval(dependX, dependY);
                if (depEval.equals(Ex2Utils.ERR_CYCLE)) {
                    errCyc=true;// Mark cyclic error flag as true.
                } else if (depEval.equals(Ex2Utils.ERR_FORM) || depEval.equals(Ex2Utils.EMPTY_CELL)||(!isNumber(depEval))) {
                    errForm=true;// Mark formula error flag as true.
                }
                value = value.replace(depend, depEval);
            }
            if(errCyc)
                return Ex2Utils.ERR_CYCLE;
            else if (errForm)
                return Ex2Utils.ERR_FORM;
            return computeFormOnlyDigit(value);
        }
        return Ex2Utils.ERR_FORM;
    }
}
