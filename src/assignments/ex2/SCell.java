package assignments.ex2;

public class SCell implements Cell {
    private String line;
    private int type;
    private int Order;
    public SCell(String s) {
        setData(s);
    }

    @Override
    public int getOrder() {
        return this.Order;
    }

    //@Override
    @Override
    public String toString() {
        if (line == null || line.isEmpty())
            return "";
        return getData();
    }

    @Override
     public void setData(String s) {
        if (s == null || s.isEmpty()) {
            this.type = Ex2Utils.ERR;
            this.line = "";
            return;
        }
        line = s;
        this.type=determinationType(s);
    }
    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        if (t == Ex2Utils.NUMBER ||
                t == Ex2Utils.FORM ||
                t == Ex2Utils.TEXT ||
                t == Ex2Utils.ERR_FORM_FORMAT ||
                t == Ex2Utils.ERR_CYCLE_FORM) {
            type = t;
        } else {
            throw new IllegalArgumentException("Invalid cell type: " + t);
        }
    }

    @Override
    public void setOrder(int t) {
        this.Order=t;
    }
    public static int determinationType(String a){//I decided that it would return a -1 error in case of any error for the sake of the overall project
        if (a == null || a.isEmpty()) {
            return -1;
        }
        if (isNumber(a))
            return 2;
        else if (a.charAt(0)=='=') {
            if (isForm(a))
                return 3;
            return -1;
        }
        return 1;
    }
    public static boolean isNumber(String a)
    {
        if(a==null)
            return false;
        a=a.replace(" ","");// Remove spaces
        try {
            Double.parseDouble(a);// Try parsing the string as a number
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isText(String a) {
        if (a == null || a.isEmpty()) {
            return false;
        }
        if (a.charAt(0) != '=')
            return true;
        return false;
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    public static boolean isCell (String s){
        if(s ==null||s.isEmpty())
            return false;
        return s.matches("^[A-Za-z][0-9]+$");
    }
    public static boolean isForm(String s){
        int countOpen = 0, countClose = 0;
        String inner;
        boolean balanced=true;
        if(s==null)
            return false;
        s=s.replace(" ","");
        if (s.isEmpty() || s.charAt(0) != '=')//Checking whether the string is empty or null or does not have '=' at the beginning
            return false;
        s = s.substring(1);//remove the '='
        if (s.isEmpty())//Checking whether the string is empty
            return false;
        while (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {//while the string has '(' at the begining and ')' at the end
            inner = s.substring(1, s.length() - 1);
            if (inner.isEmpty())
                return false;
            countOpen=0;
            countClose=0;
            for (int i = 0; i < inner.length(); i++) {
                if (inner.charAt(i) == '(') {
                    countOpen++;
                } else if (inner.charAt(i) == ')') {
                    countClose++;
                }
                if (countClose > countOpen) {// If during the count the parentheses are unbalanced, we will stop the loop
                    balanced = false;
                    break;
                }
            }
            if (countOpen == countClose && balanced) {
                s = s.substring(1, s.length() - 1);// Remove outer parentheses
            } else {
                break;
            }

        }
        if (IndexMainOperator(s) == -1||IndexMainOperator(s) == 0)
            return isNumber(s) || isCell(s);//number or cell
        String left = s.substring(0, IndexMainOperator(s));
        String right = s.substring(IndexMainOperator(s) + 1);
        return isForm("="+left) && isForm("="+right);
    }
    public static double getWeight (char op,int parenthesesWeight){
        if(!isOperator(op))
            return 0;
        double baseWeight=0;
        if (op=='+'||op=='-')
            baseWeight=0.25;
        else if (op=='*'||op=='/')
            baseWeight=0.5;
        return baseWeight+parenthesesWeight;
    }
    public static int IndexMainOperator(String expression) {
        int weightParentheses = 0, mainOperatorIndex = -1;
        char currChar;
        double weight, lowestWeight = Double.MAX_VALUE;
        if(expression==null)
            return -1;
        for (int i = 0; i < expression.length(); i++) {
            currChar = expression.charAt(i);
            if (currChar == '(') {
                weightParentheses++;
                continue;
            } else if (currChar == ')') {
                weightParentheses--;
                continue;
            }
            if (isOperator(currChar)) {
                weight = getWeight(currChar, weightParentheses);
                if (weight <= lowestWeight) {
                    lowestWeight = weight;
                    mainOperatorIndex = i;
                }
            }
        }
        return mainOperatorIndex;
    }
    public static String removeParenthses (String s)
    {
        String inner;
        int countOpen,countClose;
        boolean balanced=true;
        if(s==null||s.isEmpty())
            return "";
        while  (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
            inner = s.substring(1, s.length() - 1);
            if (inner.isEmpty())
                return inner;
            countOpen=0;
            countClose=0;
            for (int i = 0; i < inner.length(); i++) {
                if (inner.charAt(i) == '(')
                    countOpen++;
                else if (inner.charAt(i) == ')')
                    countClose++;
                if (countClose > countOpen)// If during the count the parentheses are unbalanced, we will stop the loop
                    return s;
            }
            s=inner;
        }
        return s;
    }
    public static String computeFormOnlyDigit(String form){//Calculating a formula that contains only numbers (starting from an assumption)
        int countOpen = 0, countClose = 0;
        String inner,copy,left,right;
        boolean flag=true,balanced=true;
        int mainOp;
        if (isForm(form)){
            form=form.substring(1);// Remove '='
            form=removeParenthses(form);
            mainOp = IndexMainOperator(form);//MAIN OP
            if (mainOp==-1)
                return String.valueOf(Double.parseDouble(form));
            if (mainOp==0)
                return String.valueOf(Double.parseDouble(form));
            left = form.substring(0, mainOp);//Recursion
            right = form.substring(mainOp + 1);//Recursion
            if (form.charAt(mainOp)=='+')
                return String.valueOf(Double.parseDouble(computeFormOnlyDigit("="+left)) + Double.parseDouble(computeFormOnlyDigit("="+right)));//sum
            else if (form.charAt(mainOp)=='-')
                return String.valueOf(Double.parseDouble(computeFormOnlyDigit("="+left)) - Double.parseDouble(computeFormOnlyDigit("="+right)));//subtraction
            else if (form.charAt(mainOp)=='*')
                return String.valueOf(Double.parseDouble(computeFormOnlyDigit("="+left)) * Double.parseDouble(computeFormOnlyDigit("="+right)));//multiplication
            else {//division
                if (Double.parseDouble(computeFormOnlyDigit("="+right))==0)
                    return "Infinity";
                else
                    return String.valueOf(Double.parseDouble(computeFormOnlyDigit("="+left)) / Double.parseDouble(computeFormOnlyDigit("="+right)));
            }
        }
        return "";
    }

}
