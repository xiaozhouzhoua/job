### **为什么 Java 只有值传递？**

* 值传递（pass by value）是指在调用函数时将实际参数复制一份传递到函数中，这样在函数中如果对参数进行修改，将不会影响到实际参数。
* 引用传递（pass by reference）是指在调用函数时将实际参数的地址直接传递到函数中，那么在函数中对参数所进行的修改，将影响到实际参数。

这里我们来举一个形象的例子。再来深入理解一下值传递和引用传递，然后你就知道两者的区别了。

* 你有一把钥匙，当你的朋友想要去你家的时候，如果你直接把你的钥匙给他了，这就是引用传递。这种情况下，如果他对这把钥匙做了什么事情，比如他在钥匙上刻下了自己名字，那么这把钥匙还给你的时候，你自己的钥匙上也会多出他刻的名字。

* 你有一把钥匙，当你的朋友想要去你家的时候，你复刻了一把新钥匙给他，自己的还在自己手里，这就是值传递。这种情况下，他对这把钥匙做什么都不会影响你手里的这把钥匙。

但是，不管上面那种情况，你的朋友拿着你给他的钥匙，进到你的家里，把你家的电视砸了。那你说你会不会受到影响？而我们在传递的方法中，改变对象的相关属性值的时候，不就是在“砸电视”么。

Java 中将实参传递给方法（或函数）的方式是 值传递 ：
* 如果参数是基本类型的话，很简单，传递的就是基本类型的字面量值的拷贝，会创建副本。
* **如果参数是引用类型，传递的就是实参所引用的对象在堆中地址值的拷贝，同样也会创建副本。**

```java
public static void main(String[] args) {
    int num1 = 10;
    int num2 = 20;
    swap(num1, num2);
    System.out.println("num1 = " + num1);
    System.out.println("num2 = " + num2);
}

public static void swap(int a, int b) {
    int temp = a;
    a = b;
    b = temp;
    System.out.println("a = " + a);
    System.out.println("b = " + b);
}
```
输出：
```
a = 20
b = 10
num1 = 10
num2 = 20
```
解析：

在 swap() 方法中，a、b 的值进行交换，并不会影响到 num1、num2。因为，a、b 的值，只是从 num1、num2 的复制过来的。也就是说，a、b 相当于 num1、num2 的副本，副本的内容无论怎么修改，都不会影响到原件本身。

通过上面例子，我们已经知道了一个方法不能修改一个基本数据类型的参数，而对象引用作为参数就不一样了：
```java
public static void main(String[] args) {
      int[] arr = { 1, 2, 3, 4, 5 };
      System.out.println(arr[0]);
      change(arr);
      System.out.println(arr[0]);
	}

	public static void change(int[] array) {
      // 将数组的第一个元素变为0
      array[0] = 0;
	}
```
输出：
```
1
0
```
change方法的参数拷贝的是arr（实参）的地址，因此，它和arr指向的是同一个数组对象。这也就说明了为什么方法内部对形参的修改会影响到实参。

```java
public class Person {
    private String name;
   // 省略构造函数、Getter&Setter方法
}

public static void main(String[] args) {
    Person xiaoZhang = new Person("小张");
    Person xiaoLi = new Person("小李");
    swap(xiaoZhang, xiaoLi);
    System.out.println("xiaoZhang:" + xiaoZhang.getName());
    System.out.println("xiaoLi:" + xiaoLi.getName());
}

public static void swap(Person person1, Person person2) {
    person1.setName("周杰伦");
    person2.setName("刘德华");
    Person temp = person1;
    person1 = person2;
    person2 = temp;

    System.out.println("person1:" + person1.getName());
    System.out.println("person2:" + person2.getName());
}
```
输出：
```
person1:刘德华
person2:周杰伦
xiaoZhang:周杰伦
xiaoLi:刘德华
```
swap 方法的参数 person1 和 person2 只是拷贝的实参 xiaoZhang 和 xiaoLi 的地址。因此， person1 和 person2 的互换只是拷贝的两个地址的互换罢了，并不会影响到实参xiaoZhang和xiaoLi，但是对其属性进行修改会影响指向同一地址堆的属性，这就是拿到钥匙进屋进行了破坏。

![value](https://gitee.com/zhouguangping/image/raw/master/markdown/java-value-passing.png)

所以，值传递和引用传递的区别并不是传递的内容。而是实参到底有没有被复制一份给形参。在判断实参内容有没有受影响的时候，要看传的的是什么，如果你传递的是个地址，那么就看这个地址的变化会不会有影响，而不是看地址指向的对象的变化。