package com.tvrtko.utr.lab2;

import java.util.*;

public class MinDka {

    private static class Prijelaz {
        String pocetno;
        String znak;
        String krajnje;

        Prijelaz(String pocetno, String znak, String kraj) {
            this.pocetno = pocetno;
            this.znak = znak;
            this.krajnje = kraj;
        }

        @Override
        public String toString() {
            return pocetno + "," + znak + "->" + krajnje;
        }
    }

    private static class Table {
        String s1;
        String s2;
        String ista;

        Table(String s1, String s2, String ista) {
            this.s1 = s1;
            this.s2 = s2;
            this.ista = ista;
        }
    }

    public static void main(String[] args) {
        ArrayList<Prijelaz> prijelazi = new ArrayList<>();
        ArrayList<String> stanja = new ArrayList<>();
        ArrayList<String> abeceda = new ArrayList<>();
        ArrayList<String> prihvatljivaStanja = new ArrayList<>(); //MOZDA SET TREE POREDAK!
        String pocetnoStanje;

        try (Scanner sc = new Scanner(System.in)) {
            String niz = sc.nextLine(); //ulazni niz
            String[] ulazi = niz.split(",");
            stanja.addAll(Arrays.asList(ulazi));

            niz = sc.nextLine();
            ulazi = niz.split(",");
            abeceda.addAll(Arrays.asList(ulazi)); //abeceda

            niz = sc.nextLine();
            ulazi = niz.split(",");
            prihvatljivaStanja.addAll(Arrays.asList(ulazi));//prihvatljiva stanja automata

            pocetnoStanje = sc.nextLine(); //pocetno stanje

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isEmpty()) {
                    break;
                }
                String[] split = line.split("[->,]");
                prijelazi.add(new Prijelaz(split[0], split[1], split[3]));
            }
        }
        Set<String> stanjaDohvatljiva = new TreeSet<>();
        stanjaDohvatljiva.add(pocetnoStanje);
        Set<String> obiden = new HashSet<>();
        stanjaDohvatljiva.addAll(nedohvatljiva(prijelazi, pocetnoStanje, obiden, abeceda));
        Set<String> nedohvatStanja = new HashSet<>(stanja);
        nedohvatStanja.removeAll(stanjaDohvatljiva);

        //uklanjanje prijelaza koji ukljucuju nedohvaljiva stanja
        nedohvaljivaRedukcija(prijelazi, nedohvatStanja);

        //uklanjanje prihatljivih stanja ako su nedohvatljiva
        Set<String> prihvatStanjaMicanje = new HashSet<>();
        if (nedohvatStanja.size() != 0) {
            for (String p : prihvatljivaStanja) {
                if (nedohvatStanja.contains(p)) {
                    prihvatStanjaMicanje.add(p);
                }
            }
        }
        prihvatljivaStanja.removeAll(prihvatStanjaMicanje);

        //punjenje tablice
        ArrayList<Table> tablica = new ArrayList<>();
        for (String s1 : stanjaDohvatljiva) {
            for (String s2 : stanjaDohvatljiva) {
                if (s1.compareTo(s2) < 0) {
                    tablica.add(new Table(s1, s2, "i"));
                }
            }
        }
        for (Table t : tablica) {
            if ((prihvatljivaStanja.contains(t.s1) && !prihvatljivaStanja.contains(t.s2))
                    || (!prihvatljivaStanja.contains(t.s1) && prihvatljivaStanja.contains(t.s2))) {
                t.ista = "x";
            }
        }

        pocetnoStanje = minimizacija(prijelazi, abeceda, tablica, stanjaDohvatljiva, prihvatljivaStanja, pocetnoStanje);

        //izlaz print
        System.out.println(stanjaDohvatljiva.toString().replace("[", "").replace("]", "").replace(" ", ""));
        System.out.println(abeceda.toString().replace("[", "").replace("]", "").replace(" ", ""));
        System.out.println(prihvatljivaStanja.toString().replace("[", "").replace("]", "").replace(" ", ""));
        System.out.println(pocetnoStanje);
        for (Prijelaz p : prijelazi) {
            System.out.println(p.toString());
        }

    }

    //otkriva stanja koja su dohavtljiva
    private static Set<String> nedohvatljiva(ArrayList<Prijelaz> prijelazi, String poc, Set<String> obiden, ArrayList<String> abeceda) {
        Set<String> rez = new TreeSet<>();
        if (!obiden.contains(poc)) {
            for (String ulaz : abeceda) {
                for (Prijelaz p : prijelazi) {
                    if (p.znak.equals(ulaz) && p.pocetno.equals(poc) && !p.krajnje.equals(poc)) {
                        rez.add(p.krajnje);
                        obiden.add(poc);
                        rez.addAll(nedohvatljiva(prijelazi, p.krajnje, obiden, abeceda));
                    }
                }
            }
        }
        return rez;
    }

    private static void nedohvaljivaRedukcija(ArrayList<Prijelaz> prijelazi, Set<String> nedohvaljiva) {
        Set<Prijelaz> reduced = new HashSet<>();
        for (String s : nedohvaljiva) {
            for (Prijelaz p : prijelazi) {
                if (p.pocetno.equals(s)) {
                    reduced.add(p);
                }
                if (p.krajnje.equals(s)) {
                    reduced.add(p);
                }
            }
        }

        prijelazi.removeAll(reduced);
    }

    //MINIMIZACIJA PREOSTALIH PRIJELAZA
    private static String minimizacija(ArrayList<Prijelaz> prijelazi, ArrayList<String> abeceda, ArrayList<Table> tablica, Set<String> stanja,
                                     ArrayList<String> prihvatStanja, String pocetnoStanje) {
        int added;
        do {
            added = 0;
            for (String s1 : stanja) {
                for (String s2 : stanja) {
                    if (s1.compareTo(s2) < 0) {
                        for (String ulaz : abeceda) {
                            for (Prijelaz p1 : prijelazi) {
                                if (p1.pocetno.equals(s1) && p1.znak.equals(ulaz)) {
                                    for (Prijelaz p2 : prijelazi) {
                                        if (p2.pocetno.equals(s2) && p2.znak.equals(ulaz)) {
                                            for (Table t : tablica) {
                                                if ((t.s1.equals(p1.krajnje) && t.s2.equals(p2.krajnje))
                                                        || (t.s1.equals(p2.krajnje) && t.s2.equals(p1.krajnje))) {
                                                    if (t.ista.equals("x")) {
                                                        for (Table t2 : tablica) {
                                                            if (t2.s1.equals(p1.pocetno) && t2.s2.equals(p2.pocetno) && !t2.ista.equals("x")) {
                                                                t2.ista = "x";
                                                                added = 1;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (added == 1);

        ArrayList<Prijelaz> removeP = new ArrayList<>();

        for (Table t : tablica) {
            if (!t.ista.equals("x")) {
                for (Prijelaz p : prijelazi) {
                    if (p.krajnje.equals(t.s2)) {
                        p.krajnje = t.s1;
                    }
                    if (p.pocetno.equals(t.s2)) {
                        removeP.add(p);
                    }
                }
                stanja.removeIf(s -> s.equals(t.s2));
                prihvatStanja.removeIf(s -> s.equals(t.s2));
                if(pocetnoStanje.equals(t.s2))
                    pocetnoStanje = t.s1;
            }
        }
        prijelazi.removeAll(removeP);
        return pocetnoStanje;
    }

}
