package com.tvrtko.utr.lab3;

import java.util.*;

public class SimPa {
    public static String kraj;
    public static int i = 0;
    public static int failCheckGlobal = 0;

    private static class Prijelaz {
        String pocetno;
        String znak;
        String znakStog;
        String novoStanje;
        String noviStog;

        Prijelaz(String pocetno, String znak, String stog, String novoStanje, String noviStog) {
            this.pocetno = pocetno;
            this.znak = znak;
            this.znakStog = stog;
            this.novoStanje = novoStanje;
            this.noviStog = noviStog;
        }
    }

    private static class Ispis {
        String stanje;
        String stog;

        Ispis(String stanje, String stog){
            this.stanje = stanje;
            this.stog = stog;
        }
        private void stogCheck(){
            if(stog.equals("")){
                stog = "$";
            }
        }

        @Override
        public String toString(){
            stogCheck();
            return stanje + "#" + stog + "|";
        }
    }

    public static void main(String[] args) {
        ArrayList<Prijelaz> prijelazi = new ArrayList<>();
        ArrayList<String> ulazniNizovi = new ArrayList<>();
        String pocetnoStanje, pocetniStog;
        ArrayList<String> prihvatljivoStanje;

        try (Scanner sc = new Scanner(System.in)) {
            String niz = sc.nextLine(); //ulazni niz
            String[] ulazi = niz.split("\\|");
            ulazniNizovi.addAll(Arrays.asList(ulazi)); //#1 ulazni nizovi

            sc.nextLine();  //#2 stanja potisnog automata
            sc.nextLine(); //#3 ulazni znakovi
            sc.nextLine(); //#4 znakovi stoga

            prihvatljivoStanje = new ArrayList<>(Arrays.asList(sc.nextLine().split(","))); //#5 lista prihvatljivih

            pocetnoStanje = sc.nextLine(); //#6 pocetno stanje
            pocetniStog = sc.nextLine(); //#7 pocetni znak stoga

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isEmpty()) {
                    break;
                }
                String[] split = line.split("[->,]");
                //ArrayList<String> izlazi = new ArrayList<>(Arrays.asList(split).subList(3, split.length));
                prijelazi.add(new Prijelaz(split[0], split[1], split[2], split[4], split[5]));
            }
        }

        ArrayList<String> stog = new ArrayList<>();
        stog.add(pocetniStog);
        ArrayList<Ispis> ispis = new ArrayList<>();
        ispis.add(new Ispis(pocetnoStanje, pocetniStog));

        //izdvojeni epsilon prijelazi
        ArrayList<Prijelaz> epsilonPrijelazi = new ArrayList<>(prijelazi);
        epsilonPrijelazi.removeIf(prijelaz -> !prijelaz.znak.equals("$"));


        for (String s : ulazniNizovi) {
            String[] ulaz = s.split(",");
            ArrayList<String> ulazi = new ArrayList<>(Arrays.asList(ulaz));

            automat(pocetnoStanje, prijelazi, stog, ispis, ulazi);


            int prihvat = 0;
            if(failCheckGlobal == 1){
                String output = outputAppend(ispis, prihvat, 1);
                System.out.println(output);
            }
            else{
                if(prihvatljivoStanje.contains(kraj)){
                    prihvat = 1;
                }
                else {
                    String poc = kraj;
                    epsilon(epsilonPrijelazi, prihvatljivoStanje, poc, ispis, stog);
                    i = 0;
                    if(prihvatljivoStanje.contains(kraj))
                        prihvat = 1;
                }
                if(prihvatljivoStanje.contains(kraj)){
                    prihvat = 1;
                }

                String output = outputAppend(ispis, prihvat, 0);
                System.out.println(output);
            }

            //ocisti stog i ispise
            stog.clear();
            stog.add(pocetniStog);
            ispis.clear();
            ispis.add(new Ispis(pocetnoStanje, pocetniStog));
            failCheckGlobal = 0;
        }
    }

    private static String stogPrint(ArrayList<String> stog){
        StringBuilder sb = new StringBuilder();
        for(int i = stog.size() - 1; i >= 0; i--){
            sb.append(stog.get(i));
        }
        return sb.toString();
    }

    private static void automat(String pocetno, ArrayList<Prijelaz> prijelazi, ArrayList<String> stog,
                                             ArrayList<Ispis> ispis, ArrayList<String> ulazi) {
        int failCheck = 1;
        String top = "$";
        if(!stog.isEmpty()){
            top = stog.get(stog.size() - 1);
        }
        for (Prijelaz pr : prijelazi) {
            if(ulazi.size() == 0 || failCheckGlobal == 1){
                break;
            }
            else{
                if (pr.pocetno.equals(pocetno) && pr.znak.equals(ulazi.get(0)) && pr.znakStog.equals(top)) {
                    if(!stog.isEmpty()){
                        if(pr.noviStog.equals("$")){
                            stog.remove(stog.size() - 1);
                        }
                    }
                    if (pr.noviStog.length() >= 2) {
                        stog.remove(stog.size() - 1);
                        for(int i = pr.noviStog.length() - 1; i >= 0; i--){
                            stog.add(Character.toString(pr.noviStog.charAt(i)));
                        }
                    }
                    if (pr.noviStog.length() == 1 && !pr.noviStog.equals("$")) {
                        stog.remove(stog.size() - 1);
                        stog.add(pr.noviStog);
                    }
                    failCheck = 0;
                    ulazi.remove(0);
                    ispis.add(new Ispis(pr.novoStanje, stogPrint(stog)));
                    kraj = pr.novoStanje;
                    automat(pr.novoStanje, prijelazi, stog, ispis, ulazi);
                }
                if (pr.pocetno.equals(pocetno) && pr.znak.equals("$") && pr.znakStog.equals(top)) {
                    if(!stog.isEmpty()){
                        if(pr.noviStog.equals("$")){
                            stog.remove(stog.size() - 1);
                        }
                    }
                    if(pr.noviStog.length() == 2){
                        stog.add(Character.toString(pr.noviStog.charAt(0)));
                    }
                    failCheck = 0;
                    ispis.add(new Ispis(pr.novoStanje, stogPrint(stog)));
                    kraj = pr.novoStanje;
                    automat(pr.novoStanje, prijelazi, stog, ispis, ulazi);
                }
            }
        }
        if(failCheck == 1 && ulazi.size() != 0){
            failCheckGlobal = 1;
        }
    }

    private static void epsilon(ArrayList<Prijelaz> epsiloni, ArrayList<String> prihvatljiva, String pocetno, ArrayList<Ispis> ispis,
                                ArrayList<String> stog){
        String top = "$";
        if(!stog.isEmpty()){
            top = stog.get(stog.size() - 1);
        }
        for(Prijelaz eps: epsiloni){
            if(i == 0) {
                if (pocetno.equals(eps.pocetno) && eps.znakStog.equals(top)) {
                    if (eps.noviStog.equals("$")) {
                        stog.remove(stog.size() - 1);
                    }
                    if (eps.noviStog.length() >= 2) {
                        stog.remove(stog.size() - 1);
                        for(int i = eps.noviStog.length() - 1; i >= 0; i--){
                            stog.add(Character.toString(eps.noviStog.charAt(i)));
                        }
                    }
                    if (eps.noviStog.length() == 1 && !eps.noviStog.equals("$")) {
                        stog.remove(stog.size() - 1);
                        stog.add(eps.noviStog);
                    }
                    ispis.add(new Ispis(eps.novoStanje, stogPrint(stog)));
                    if (prihvatljiva.contains(eps.novoStanje)) {
                        i = 1;
                        kraj = eps.novoStanje;
                        break;
                    }
                    else{
                        ArrayList<Prijelaz> newEps = new ArrayList<>(epsiloni);
                        newEps.remove(eps);
                        epsilon(newEps, prihvatljiva, pocetno, ispis, stog);
                    }
                }
            }
        }
    }


    private static String outputAppend(ArrayList<Ispis> izlaz, int prihvat, int fail) {
        StringBuilder sb = new StringBuilder();
        for (Ispis is: izlaz) {
            sb.append(is.toString());
        }
        if(fail == 1){
            sb.append("fail|");
        }

        sb.append(prihvat);

        return sb.toString();
    }

}

