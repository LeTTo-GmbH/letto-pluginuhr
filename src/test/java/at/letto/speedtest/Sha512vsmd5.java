package at.letto.speedtest;

import at.letto.tools.ENCRYPT;

public class Sha512vsmd5 {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<10000;i++) {
            sb.append(ENCRYPT.md5(""+System.currentTimeMillis()));
        }
        String s = sb.toString();
        long t1 = System.currentTimeMillis();
        String s1;
        for (int i=0;i<1000;i++)
            s1 = ENCRYPT.sha512(s);
        long t2 = System.currentTimeMillis();
        for (int i=0;i<1000;i++)
            s1 = ENCRYPT.md5(s);
        long t3 = System.currentTimeMillis();
        System.out.println("SHA: "+(t2-t1)+" MD5: "+(t3-t2));
        System.out.println("SHA: "+ENCRYPT.sha512(s));
        System.out.println("MD5: "+ENCRYPT.md5(s));
        System.out.println("length: "+s.length());


        s = "Das ist ein Satz";
        t1 = System.currentTimeMillis();
        for (int i=0;i<1000;i++)
            s1 = ENCRYPT.sha512(s);
        t2 = System.currentTimeMillis();
        for (int i=0;i<1000;i++)
            s1 = ENCRYPT.md5(s);
        t3 = System.currentTimeMillis();
        System.out.println("SHA: "+(t2-t1)+" MD5: "+(t3-t2));
        System.out.println("SHA: "+ENCRYPT.sha512(s));
        System.out.println("MD5: "+ENCRYPT.md5(s));
        System.out.println("length: "+s.length());

    }
}
