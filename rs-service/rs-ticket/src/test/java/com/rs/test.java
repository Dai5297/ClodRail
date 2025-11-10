package com.rs;

import org.junit.jupiter.api.Test;

public class test {
    class Animal{
        void speak(){
            System.out.println("uuuuuuuu");
        }
    }

    @Test
    void  test() {
        Animal dog = new Animal() {
            @Override
            void speak() {
                System.out.println("woof woof");
            }
        };
        dog.speak();

    }

}
