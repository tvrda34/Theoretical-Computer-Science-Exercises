package com.tvrtko.utr.lab4;

import java.util.Scanner;

public class Parser2 {
    public static int pozicija = 0;
    private static char[] ulaz;
    public static int length;
    public static String rez = "";
    public static int bsem = 1;

    public static void main(String[] args) {

        String input;
        try(Scanner sc = new Scanner(System.in)){
            input = sc.nextLine();
        }
        input = input.concat("\n");
        ulaz = input.toCharArray();
        length = ulaz.length - 1;

        prod_S();

        System.out.println(rez);
        if(ulaz[pozicija] == '\n' && bsem == 1){
            System.out.println("DA");
        }
        else{
            System.out.println("NE");
        }

    }

    private static void prod_S() {
        int in = 0;
        rez = rez.concat("S");
            if(ulaz[pozicija] == 'a'){
                ++pozicija;
                prod_A();
                prod_B();
                in = 1;
            }
            if(ulaz[pozicija] == 'b' && in == 0){
                ++pozicija;
                prod_B();
                prod_A();
            }
    }

    private static void prod_B() {
        if(bsem == 1) {
            rez = rez.concat("B");
            if (ulaz[pozicija] == 'c') {
                ++pozicija;
                if(ulaz[pozicija] == 'c'){
                    ++pozicija;
                    prod_S();
                }
                if (ulaz[pozicija] == 'b') {
                    ++pozicija;
                    if (ulaz[pozicija] == 'c')
                        ++pozicija;
                }
            }
        }
    }

    private static void prod_A() {
        if(ulaz[pozicija] != 'b' && ulaz[pozicija] != 'a' && bsem == 1){
            if(pozicija != length){
                rez = rez.concat("A");
                bsem = 0;
            }
        }
        if(ulaz[pozicija] == 'b'){
            rez = rez.concat("A");
            ++pozicija;
            prod_C();
        }

        if(ulaz[pozicija] == 'a'){
            rez = rez.concat("A");
            ++pozicija;
        }

    }

    private static void prod_C() {
        rez = rez.concat("C");
        prod_A();
        prod_A();
    }
}
