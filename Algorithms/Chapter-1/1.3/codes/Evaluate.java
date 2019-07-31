public class Evaluate
{
    public static void main(String[] args)
    {
        Stack<String> ops = new Stack<String>();
        Stack<Double> vals = new Stack<Double>();
        
        while(! StdIn.isEmpty())
        {
            // 读取字符，如果是操作符则压入操作符栈
            String s = StdIn.readString();
            if(s.equals("(")){

            }else if(s.equals("+")) {
                ops.push(s);
            }else if(s.equals("-")) {
                ops.push(s);
            }else if(s.equals("*")) {
                ops.push(s);
            }else if(s.equals("/")) {
                ops.push(s);
            }else if(s.equals("sqrt")) {
                ops.push(s);
            }else if(s.equals(")")) {
                // 如果是 ），弹出运算符和操作数，计算结果并压入栈
                String op = ops.pop();
                double v = vals.pop();

                if(op.equals("+"))          v = vals.pop() + v;
                else if(op.equals("-"))     v = vals.pop() - v;
                else if(op.equals("*"))     v = vals.pop() * v;
                else if(op.equals("/"))     v = vals.pop() / v;
                else if(op.equals("sqrt"))     v = Math.sqrt(v);

                vals.push(v);
            }else{ // 其他则视为数字压入操作符栈
                vals.push(Double.parseDouble(s));
            }
        }

        StdOut.println(vals.pop());
    }
}