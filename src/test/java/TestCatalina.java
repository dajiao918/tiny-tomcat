import org.apache.catalina.core.Catalina;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 20:12
 **/
public class TestCatalina {

    public static void main(String[] args) {
        Catalina catalina = new Catalina();
        catalina.load();
        catalina.start();
    }

}
