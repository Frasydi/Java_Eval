package Sydn;
//Fungsi ini dibuat oleh Muhammad Fachri Rasyidi

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mathf {
    public static boolean modedebug = false;
    public static double eval(String str) {
        return eval(str, Precise.NORMAL);
    }
    public static double eval(String str, Precise es) {
        ArrayList<String> doub = new ArrayList<String>(Arrays.asList(splitAnStringtoArrayList(str)));
        if (modedebug) System.out.println(doub);
        int jumlahkurung = 0;
        int jumlahsimbol=0;
        for (String s : doub) {
            if (s.matches("[()]")) {
                jumlahkurung++;
            } else if (s.matches("[-+*/|^]"))jumlahsimbol++;
        }
        int[] index = new int[2];
        for (int z = 0; z < jumlahkurung/2; z++) {
            for (int i = 0; i < doub.size(); i++) {
                if (doub.get(i).equalsIgnoreCase("(")) {
                    index[0] = i;
                    for (int j = 0; j < doub.size(); j++) {
                        if (doub.get(j).equalsIgnoreCase("(")) {
                            index[0] = j;
                        } else if (doub.get(j).equalsIgnoreCase(")")) {
                            index[1] = j;
                            doub.remove(index[0]);
                            doub.remove(j-1);
                            break;
                        }
                    }
                    index[1] -=2;
                    if (modedebug) {
                        System.out.println("Test doub "+ doub);
                        System.out.println("Test "+index[0]+" "+index[1]);
                    }

                    penjumlahan(doub, index[0], index[1], true, es);
                }
            }
        }
        menghapuskurung(doub);
        penjumlahan(doub, 0, doub.size(), false, es);
        if (modedebug)System.out.println(doub);

        return Double.parseDouble(doub.get(0));
    }
    private static void menghapuskurung(ArrayList<String> as) {
        for (int i = 0; i < as.size(); i++) {
            if (as.get(i).equals("(")||as.get(i).equals(")")) {
                as.remove(i);
            }
        }
    }
    private static void penjumlahan(ArrayList<String> as, int awal, int akhir, boolean kurung, Precise es) {
        String[] sim = {"|","^","/","*","-","+"};
        //for (int z = 0; z < 2; z++) {;
        for (String ant : sim) {
            for (int i = awal, kz = 1; i < as.size(); i++) {
                if (i < akhir) {
                    try {
                        if (as.get(i).equals(ant)) {
                            if (modedebug) System.out.println(as);
                            if (modedebug) System.out.println(awal + " " + akhir);
                            if (es == Precise.NORMAL) {
                                double temp = hasil(Double.parseDouble(as.get(i - 1)), Double.parseDouble(as.get(i + 1)), ant);
                                penjum2(as, i, temp);
                            } else  {
                                BigDecimal temp = hasilBD(new BigDecimal(as.get(i-1)), new BigDecimal(as.get(i+1)), ant);
                                penjum2(as, i, temp);
                            }

                            if (kurung) {
                                akhir-= 2;
                                //(kz-(jumlahkurang-1))+1;
                                i = awal;
                            } else i = awal;
                        }
                    }catch (NumberFormatException e) {
                        System.out.println("error di index "+i);
                    }
                }
            }
        }
        //}
    }

    private static double hasil(double a1, double a2, String tanda) {
        switch (tanda) {
            case "+":
                return a1 + a2;
            case "-":
                return a1-a2;
            case "*":
                return a1*a2;
            case "/":
                return  a1/a2;
            case "^":
                return Math.pow(a1, a2);
            case "|" :
                return Math.round(Math.pow(a1, 1/a2)*1000.0)/1000.0;
            default:
                System.out.println("Error");
        }
        return 0.0;
    }
    private static BigDecimal hasilBD(BigDecimal a1, BigDecimal a2, String tanda) {
        switch (tanda) {
            case "+":
                return a1.add(a2);
            case "-":
                return a1.subtract(a2);
            case "*":
                return a1.multiply(a2);
            case "/":
                return  a1.divide(a2, 20, RoundingMode.HALF_UP);
            case "^":
                return a1.pow(a2.intValue());
            case "|" :
                return a1.pow(1/a2.intValue());
            default:
                System.out.println("Error");
        }
        return BigDecimal.valueOf(0);
    }
    private static <E> void penjum2(ArrayList<String> as,int index, E hasil) {
        for (int i = 0; i < 3; i++) {
            as.remove(index-1);
        }
        as.add(index-1,  String.valueOf(hasil));
    }
    public static String ConvertSymboltoDecimal(HashMap<String, Double> vbs, String sbn) {
        StringBuilder sb = new StringBuilder(sbn);
        for (int j = 0; j < sb.length(); j++) {
            if (vbs.containsKey(String.valueOf(sb.charAt(j)))) {
                sb.replace(j, j + 1, String.valueOf(vbs.get(String.valueOf(sb.charAt(j)))));
            } else if (String.valueOf(sb.charAt(j)).equalsIgnoreCase("p") ) {
                sb.replace(j, j + 1, String.valueOf(Math.PI));
            }
        }
        return sb.toString();
    }
    public static String ConvertSymboltoDecimal(HashMap<String, Double> vbs, ArrayList<String> sn) {
        List<String> temp = new ArrayList<>(vbs.keySet());
        if (modedebug)System.out.println(sn);
        for (String tns: temp) {
            for (int j = 0; j < sn.size(); j++) {
                if (tns.equalsIgnoreCase(sn.get(j))) {
                    sn.remove(j);
                    if (modedebug)System.out.println(true);
                    sn.add(j, String.valueOf(vbs.get(tns)));
                } else if (sn.get(j).equalsIgnoreCase("p")) {
                    sn.remove(j);
                    if (modedebug)System.out.println(true);
                    sn.add(j, String.valueOf(Math.PI));
                }
            }
        }
        StringBuilder bld = new StringBuilder();
        for (String dat:sn) {
            bld.append(dat);
        }
        return bld.toString();
    }
    //?!= \d\.\d\. ?!= \d\.\d\.
    public static String[] splitAnStringtoArrayList(String sbn) {
        Pattern ptrn = Pattern.compile(angka+"|([-|^(+\\/*)])|([a-z]{1,3})");
        Matcher match = ptrn.matcher(sbn);
        ArrayList<String> arrlist = new ArrayList<>();
        String temp2 = "";
        while (match.find()) {
            if (match.group() != null) {
                temp2 = match.group();
            }
            arrlist.add(temp2);
        }
        return arrlist.toArray(new String[0]);
    }
    public static String derivative(String persamaan) {
        HashMap<String, Double> stm = new HashMap<>();
        stm.put("x", 1.0);
        return derivative(stm ,persamaan);
    }
    private static final String angka = "((?:^\\-?\\d+([.][0-9]+)?)|(?:(?<=[-^|(+\\/*])(?:\\-?\\d+([.][0-9]+)?)))";
    public static String derivative(HashMap<String, Double> msn, String persamaan) {
        boolean sudah = false;
        String[] sns = splitAnStringtoArrayList(persamaan);
        ArrayList<String> db = new ArrayList<>(Arrays.asList(sns));
        ArrayList<String> keys = new ArrayList<>(msn.keySet());
        if (modedebug)System.out.println(db);
        double temp1 = 0;
        int jumlahkrug = 0;
        for (int i = 0; i < db.size(); i++) {
            try {
                if (db.get(i).matches(angka) && db.get(i + 1).equals("*") && !db.get(i + 2).equals("x")) {
                    for (int j = i; j < db.size(); j++) {
                        try {
                            if (modedebug) System.out.println(j);
                            if (db.get(j + 1).matches("[-+|^]") || db.get(j + 2).equals("x")) {
                                if (jumlahkrug % 2 == 1) {
                                    db.add(i, "(");
                                }
                                break;
                            }
                            if (db.get(j + 1).equals("*")) {
                                if (modedebug) System.out.println(db);
                                if (db.get(j + 2).matches(angka)) {
                                    double temp = Double.parseDouble(db.get(j)) * Double.parseDouble(db.get(j + 2));
                                    db.remove(j);
                                    db.remove(j);
                                    db.remove(j);
                                    db.add(j, String.valueOf(temp));
                                    j = i - 1;
                                } else if (db.get(j + 2).equals("(")) {
                                    if (db.get(j + 3).matches(angka)) {
                                        double temp = Double.parseDouble(db.get(j)) * Double.parseDouble(db.get(j + 3));
                                        db.remove(j);
                                        db.remove(j);
                                        db.remove(j);
                                        db.remove(j);
                                        db.add(j, String.valueOf(temp));
                                        j = i - 1;
                                        jumlahkrug++;
                                    }
                                }
                            } else if (db.get(j + 1).equals(")")) {
                                if (db.get(j + 2).equals("*") && db.get(j + 3).matches(angka)) {
                                    double temp = Double.parseDouble(db.get(j)) * Double.parseDouble(db.get(j + 3));
                                    db.remove(j);
                                    db.remove(j);
                                    db.remove(j);
                                    db.remove(j);
                                    db.add(j, String.valueOf(temp));
                                    jumlahkrug++;
                                    j = i - 1;
                                }
                            }
                            if (modedebug) System.out.println(db);
                        } catch (IndexOutOfBoundsException | NumberFormatException e) {
                            if (modedebug)e.printStackTrace();
                        } finally {

                        }
                    }
                }
            }catch (IndexOutOfBoundsException e) {
                if (modedebug)e.printStackTrace();
            }
        }
        String[] tambahdankurang = {"+"};
        for (int i = 0; i < db.size(); i++) {
            for (String simb: tambahdankurang) {
                if (db.get(i).equals(simb)) {
                    try {
                        if (db.get(i-1).matches("((?:^\\-?\\d+([.][0-9]+)?)|(?:(?<=[-^|(+\\/*])(?:\\-?\\d+([.][0-9]+)?)))") && db.get(i + 1).equals("x")) {
                            db.add(i + 2, db.get(i-1));
                            db.add(i + 2, db.get(i));
                            db.remove(i - 1);
                            db.remove(i - 1);
                        }
                    }catch (NumberFormatException |IndexOutOfBoundsException e) {
                        if (modedebug)System.out.println("error");
                    }
                }
            }
        }
        for (int i = 0; i < db.size(); i++) {
            sudah = false;
            if (modedebug) System.out.println(db);
            if (db.size() > 1) {
                if (db.get(i).equals("^")&&db.get(i-1).equals("x")) {
                    if (Double.parseDouble(db.get(i + 1)) > 1) {
                        temp1 = Double.parseDouble(db.get(i + 1));
                        db.remove(i + 1);
                        db.add(i + 1, String.valueOf(temp1 - 1));
                        try {
                            if (db.get(i - 2).equals("-")) {
                                try {
                                    if (db.get(i-3).equals("-")||db.get(i-3).matches("((?:^\\-?\\d+([.][0-9]+)?)|(?:(?<=[-^|(+\\/*])(?:\\-?\\d+([.][0-9]+)?)))")) {
                                        if (!db.get(i-3).matches("-")) {
                                            db.add(i - 1, "*");
                                            db.add(i - 1, String.valueOf(temp1));
                                        } else {
                                            db.add(i - 2, "*");
                                            db.add(i - 2, String.valueOf(temp1));
                                        }
                                    } else {
                                        db.add(i - 2, "*");
                                        db.add(i - 2, String.valueOf(temp1));
                                    }
                                }catch (ArrayIndexOutOfBoundsException e) {
                                    db.add(i - 1, "*");
                                    db.add(i - 1, String.valueOf(temp1));
                                }catch (NumberFormatException e) {
                                    if (modedebug) System.out.println("Error");;
                                }
                            } else {
                                db.add(i - 1, "*");
                                db.add(i - 1, String.valueOf(temp1));
                            }
                        }catch (ArrayIndexOutOfBoundsException e) {
                            db.add(i - 1, "*");
                            db.add(i - 1, String.valueOf(temp1));
                        }
                        i = i + 3;
                    } else if (Double.parseDouble(db.get(i + 1)) == 1) {
                        db.remove(i - 1);
                        db.remove(i - 1);
                        db.remove(i - 1);

                        if (i > 5) {
                            db.remove(i - 2);
                        }
                        i-=2;
                    }
                } else if (!(db.get(i).matches("[-+*^|/()]"))) {
                    try {
                        boolean bnr1 = false;
                        if (db.get(i+1).matches("[()]")) {
                            if (db.get(i+2).equals("*")) {
                                try {
                                    if (db.get(i + 3).matches("[-x]")) {
                                        if (db.get(i + 3).equals("-")) {
                                            if (!db.get(i + 4).equals("x")) {
                                                db.add(i + 1, "0");
                                                db.remove(i);
                                                bnr1 = true;
                                            } else {
                                                bnr1 = true;
                                            }
                                        } else {
                                            if (!db.get(i + 3).equals("x")) {
                                                db.add(i + 1, "0");
                                                db.remove(i);
                                                bnr1 = true;
                                            } else {
                                                bnr1 = true;
                                            }
                                        }
                                    }
                                }catch (IndexOutOfBoundsException e) {
                                    if (modedebug) e.printStackTrace();
                                }
                            }
                        }if (db.get(i).equals("x")&&!bnr1) {
                            if (!(db.get(i+1).equals("^"))) {
                                db.remove(i);
                                db.add(i, "1");
                            }
                        } else {


                            if (!(db.get(i+2).equals("x"))&&!bnr1) {
                                if (db.get(i+2).matches("[-]")&&!db.get(i+1).matches("[()]")) {
                                    if (!db.get(i+3).equals("x")) {
                                        db.add(i + 1, "0");
                                        db.remove(i);
                                    }
                                }else {
                                    db.add(i + 1, "0");
                                    db.remove(i);
                                }
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        if (modedebug) System.out.println("errorbound");
                        if (db.get(i).equals("x")) {
                            db.remove(i);
                            db.add(i, "1");
                        } else {
                            db.remove(i);
                            db.add(i,"0");
                        }
                    }
                }
            } else {
                if (db.get(i).equals("x")) {
                    db.remove(i);
                    db.add(i, "1");
                } else {
                    db.remove(i);
                    db.add(i, "0");
                }

            }
            if (modedebug) System.out.println(i);
        }
        StringBuilder bld = new StringBuilder();
        for (String dbz: db) {
            bld.append(dbz);
        }
        if (modedebug)System.out.println(bld);
        return bld.toString();
    }
    private static String[] split2(String sbn) {
        Matcher match = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)|([a-z])|(\\DD)|(\\D)").matcher(sbn);
        ArrayList<String> arrlist = new ArrayList<>();
        String temp2 = "";
        while (match.find()) {
            if (match.group() != null) {
                temp2 = match.group(1);
            }
            arrlist.add(temp2);
        }
        return arrlist.toArray(new String[0]);
    }
}



