package com.m1yellow.mypages;

public class MyTest {
    public static void main(String[] args) {
        System.out.println(MyTest.class.getResource(""));
        System.out.println(MyTest.class.getResource("/"));

        System.out.println(MyTest.class.getClassLoader().getResource(""));
        System.out.println(MyTest.class.getClassLoader().getResource("/"));
    }
}
