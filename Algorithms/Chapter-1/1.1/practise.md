* 1.1.1
a:`7`
b:`200.0000002`
c:`true`

* 1.1.2
a:`float 1.618`
b:`float 10.0`
c:`bool true`
d:`string 33`

* 1.1.3
```
public static void main(String[] args) {
        // practice 3
        int[] params = StdIn.readAllInts();
        int one = params[0];
        int two = params[1];
        int three = params[2];

        if(one == two && two == three){
            StdOut.print("equal");
        }else {
            StdOut.print(" not equal"); 
        }
    }
```

* 1.1.4
a:`格式错误，then`
b:`格式错误，if`
c:`正确`
d:`格式错误，c=0;`

* 1.1.5

```
public static void main(String[] args) {
    double x = 0.5,y = 7.7887667;

    if(InZeroAndOne(x,y)) {
        StdOut.print(true);
    }else {
        StdOut.print(false);
    }
}

public static boolean InZeroAndOne(double x,double y) {
    if(x > 0 && x < 1 && y > 0 && y < 1) return true;

    return false;
}
```

* 1.1.6
会输出斐波那契数列，因为 `g`的值就是上一个`f`的值，实际就是 `f(n) = f(n-1) + f(n-2)`。
```
0
1
1
2
3
5
8
13
21
34
55
89
144
233
377
610
```

* 1.1.7

a:3.00009155413138
b:499500
c:1023

* 1.1.8

a:b，单纯输出字符
b:197，b 的 ASCII 值是 98，c 是 99
c:e,a 的 ASCII 值是 97，101 转换后对应 e

* 1.1.9
```
String s = "";

for (int n = N;n > 0; n /= 2)
    s = (n % 2) + s;
```

* 1.1.10

略

* 1.1.11