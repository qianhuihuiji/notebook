public class TestDate
{
    public static void main(String[] args)
    {
        int m1 = Integer.parseInt(args[0]);
        int d1 = Integer.parseInt(args[1]);
        int y1 = Integer.parseInt(args[2]);
        int m2 = Integer.parseInt(args[3]);
        int d2 = Integer.parseInt(args[4]);
        int y2 = Integer.parseInt(args[5]);

        BasicDate date1 = new BasicDate(m1, d1, y1);
        SmallDate date2 = new SmallDate(m2, d2, y2);

        StdOut.println(date1);
        StdOut.println(date2);
    }
}