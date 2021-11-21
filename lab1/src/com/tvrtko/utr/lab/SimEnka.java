package com.tvrtko.utr.lab;

import java.util.*;

public class SimEnka {

    private static class Prijelaz {
        String pocetno;
        String znak;
        ArrayList<String> krajnje;

        Prijelaz(String pocetno, String znak, ArrayList<String> izlazi) {
            this.pocetno = pocetno;
            this.znak = znak;
            this.krajnje = izlazi;
        }
    }

    public static void main(String[] args) {
        ArrayList<Prijelaz> prijelazi = new ArrayList<>();
        ArrayList<String> ulazniNizovi = new ArrayList<>();
        String pocetnoStanje;

        try (Scanner sc = new Scanner(System.in)) {
            String niz = sc.nextLine(); //ulazni niz
            String[] ulazi = niz.split("\\|");
            ulazniNizovi.addAll(Arrays.asList(ulazi));

            String stanja = sc.nextLine();  //stanja automata
            ArrayList<String> stanjaAutomata = new ArrayList<>(Arrays.asList(stanja.split(",")));

            String abeceda = sc.nextLine(); //abeceda ulaznih simbola
            String pStanja = sc.nextLine(); //prihvatljiva stanja automata

            pocetnoStanje = sc.nextLine(); //pocetno stanje

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isEmpty()) {
                    break;
                }
                String[] split = line.split("[->,]");
                ArrayList<String> izlazi = new ArrayList<>(Arrays.asList(split).subList(3, split.length));
                prijelazi.add(new Prijelaz(split[0], split[1], izlazi));
            }
        }


        for (String s : ulazniNizovi) {
            String[] ulaz = s.split(",");
            ArrayList<String> izlaz = automat(pocetnoStanje, prijelazi, ulaz);
            String output = outputAppend(izlaz);
            System.out.println(output);
        }
    }

    private static ArrayList<String> automat(String pocetno, ArrayList<Prijelaz> prijelazi, String... ulazi) {
        Set<String> pomocni = new TreeSet<>();
        Set<String> poc = new TreeSet<>();
        int pronden = 0;

        for (Prijelaz pr : prijelazi) {
            if (pr.pocetno.equals(pocetno) && pr.znak.equals("$")) {
                poc.addAll(pr.krajnje);
            }
        }
        Set<String> obiden = new TreeSet<>();
        poc.addAll(recEpsPoc(prijelazi, poc, obiden));
        obiden.clear();
        poc.add(pocetno);
        ArrayList<String> izlaz = new ArrayList<>(poc);

        for (String s : ulazi) {
            izlaz.add("|");
            for (String ps : poc) {
                for (Prijelaz p : prijelazi) {
                    if (p.pocetno.equals(ps) && p.znak.equals(s)) {
                        pomocni.addAll(p.krajnje);
                        pronden = 1;
                        Set<String> epsCheck = new TreeSet<>(p.krajnje);
                        epsCheck.remove("#");
                        pomocni.addAll(recEpsPoc(prijelazi, epsCheck, obiden));
                        if(pomocni.size() == 1 && pomocni.contains("#")){
                            pronden = 0;
                        }
                        pomocni.remove("#");
                    }
                }
            }
            if (pronden == 0) {
                izlaz.add("#");
            }
            pronden = 0;
            poc.clear();
            poc.addAll(pomocni);
            izlaz.addAll(pomocni);
            pomocni.clear();
            obiden.clear();
        }
        return izlaz;
    }

    private static Set<String> recEpsPoc(ArrayList<Prijelaz> prijelazi, Set<String> poc, Set<String> obiden) {
        Set<String> rez = new TreeSet<>();
        Set<String> nPoc = new TreeSet<>(poc);
        for (String s : poc) {
            if (!obiden.contains(s)) {
                for (Prijelaz p : prijelazi) {
                    if (p.znak.equals("$") && p.pocetno.equals(s)) {
                        rez.addAll(p.krajnje);
                        nPoc.addAll(p.krajnje);
                        nPoc.remove(s);
                        obiden.add(s);
                        rez.addAll(recEpsPoc(prijelazi, nPoc, obiden));
                    }
                }
                nPoc.remove(s);
                obiden.add(s);
            }
        }
        return rez;
    }


    private static String outputAppend(ArrayList<String> izlaz) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < izlaz.size(); j++) {
            sb.append(izlaz.get(j));
            if (j != izlaz.size() - 1) {
                if (!izlaz.get(j).equals("|") && !izlaz.get(j + 1).equals("|")) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }

}
