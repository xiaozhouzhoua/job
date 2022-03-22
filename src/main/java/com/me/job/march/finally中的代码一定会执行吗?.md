
### **finally 中的代码一定会执行吗？**

不一定的！在某些情况下，finally 中的代码不会被执行。

就比如说finally之前虚拟机被终止运行的话,finally中的代码就不会被执行。
```java
try {
        System.out.println("Try to do something");
        throw new RuntimeException("RuntimeException");
        } catch (Exception e) {
        System.out.println("Catch Exception -> " + e.getMessage());
        // 终止当前正在运行的Java虚拟机
        System.exit(1);
        } finally {
        System.out.println("Finally");
        }
```
输出：
```
Try to do something
Catch Exception -> RuntimeException
```
另外，在以下 2 种特殊情况下，finally 块的代码也不会被执行：
1. 程序所在的线程死亡
2. 关闭 CPU

> 注意，如果try语句里有return，返回的是try语句块中变量值。
详细执行过程如下：
a.如果有返回值，就把返回值保存到局部变量中；
b.执行jsr指令跳到finally语句里执行；
c.执行完finally语句后，返回之前保存在局部变量表里的值。
**如果try，finally语句里均有return，忽略try的return，而使用finally的return.**

相关 issue：
https://github.com/Snailclimb/JavaGuide/issues/190