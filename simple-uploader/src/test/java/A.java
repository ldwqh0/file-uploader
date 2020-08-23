
public class A {

    public static void main(String[] args) {
        String filename = "abc.jpge";
        String s = filename.substring(filename.lastIndexOf(".") + 1);
        System.out.println(s);
    }

}
