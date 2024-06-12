package at.letto.tools;

public class MainKeyTest {

    public static void main(String[] args) {
        for (int i=0;i<400;i++)
            System.out.println(ENCRYPT.generateKeyAz09(30));
    }
}
