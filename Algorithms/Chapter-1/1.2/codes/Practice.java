// import java.util.Arrays;

public class Practice
{
    public static void main(String[] args) {
        StdOut.print("xxx");
    }

    /**
     * 判断字符串是否是回文字符
     */
    public static boolean isPalindrome(String s)
    {
        int N = s.length();

        for (int i = 0;i < N/2; i++)
        {
            if(s.charAt(i) != s.charAt(N-1-i))
                return false;
        }

        return true;
    }
}