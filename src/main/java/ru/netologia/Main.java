package ru.netologia;

import com.github.javafaker.Faker;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Faker faker = new Faker();
        System.out.println(faker.letterify("???") + faker.numerify("###"));
    }
}