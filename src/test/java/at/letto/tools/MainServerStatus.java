package at.letto.tools;

public class MainServerStatus {

    public static void main(String[] args) {
        System.out.println("Rev:"+ServerStatus.getRevision());
        System.out.println("IP:"+ServerStatus.getIP());
    }
}
