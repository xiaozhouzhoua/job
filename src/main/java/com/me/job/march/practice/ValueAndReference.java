package com.me.job.march.practice;

import com.me.job.march.pojo.Person;

public class ValueAndReference {
    public static void main(String[] args) {
        Person xiaoZhang = new Person("小张");
        Person xiaoLi = new Person("小李");
        swap(xiaoZhang, xiaoLi);
        System.out.println("xiaoZhang:" + xiaoZhang.getName());
        System.out.println("xiaoLi:" + xiaoLi.getName());
    }

    public static void destroy(Person person1, Person person2) {
        person1.setName("大张");
        person2.setName("大李");
    }

    public static void reference(Person person1, Person person2) {
        person1 = new Person();
        person2 = new Person();
        person1.setName("大张");
        person2.setName("大李");
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
}
