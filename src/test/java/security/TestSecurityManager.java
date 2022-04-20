package security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 09:22
 **/
public class TestSecurityManager {

    static class SecurityManagerChild extends SecurityManager {
        @Override
        public void checkRead(String file) {
            throw new RuntimeException("read not permit");
        }
    }

    public static void main(String[] args) {

        System.out.println(System.getSecurityManager());
//        System.setSecurityManager(new SecurityManagerChild());
        System.out.println(System.getSecurityManager());
        try(FileInputStream stream = new FileInputStream("F:\\dos\\calculate_code.ASM"))
        {
            int available = stream.available();
            byte[] buf = new byte[available];
            int len = 0;
            while ((len = stream.read(buf)) != -1) {
                System.out.println(new String(buf,0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
