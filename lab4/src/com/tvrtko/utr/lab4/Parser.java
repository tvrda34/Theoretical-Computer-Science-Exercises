package com.tvrtko.utr.lab4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Parser {
    private static ArrayList<String> ulaz = new ArrayList<>();
    public static int length;
    public static String rez = "";
    public static int bsem = 1;

    public static void main(String[] args) {

        String input;
        try(Scanner sc = new Scanner(System.in)){
            input = sc.nextLine();
        }
        input = input.concat("\n");
        ulaz.addAll(Arrays.asList(input.split("")));
        length = ulaz.size() - 1;

        prod_S();
        //++pozicija;
        System.out.println(rez);
        if(ulaz.size() != 0){
            if(ulaz.get(0).equals("\n") && bsem == 1){
                System.out.println("DA");
            }
            else{
                System.out.println("NE");
            }
        }
        else{
            System.out.println("NE");
        }

    }

    private static void prod_S() {
        int in = 0;
        rez = rez.concat("S");
        String znak = ulaz.get(0);
        if(znak.equals("a")){
            ulaz.remove(0);
            prod_A();
            prod_B();
            in = 1;
        }
        if(znak.equals("b") && in == 0){
            ulaz.remove(0);
            prod_B();
            prod_A();
        }
    }

    private static void prod_B() {
        if(bsem == 1) {
            rez = rez.concat("B");
            if (ulaz.get(0).equals("c")) {
                ulaz.remove(0);
                if(ulaz.get(0).equals("c")){
                    ulaz.remove(0);
                    prod_S();
                }
                if (ulaz.get(0).equals("b")) {
                    ulaz.remove(0);
                    if (ulaz.get(0).equals("c"))
                        ulaz.remove(0);
                }
            }
        }
    }

    private static void prod_A() {
        if(bsem == 1) {
            String znak = ulaz.get(0);
            ulaz.remove(0);
            if (!znak.equals("b") && !znak.equals("a") && bsem == 1) {
                    rez = rez.concat("A");
                    bsem = 0;
            }
            if (znak.equals("b")) {
                rez = rez.concat("A");
                prod_C();
            }

            if (znak.equals("a")) {
                rez = rez.concat("A");
            }
        }

    }

    private static void prod_C() {
        rez = rez.concat("C");
        prod_A();
        prod_A();
    }
}
