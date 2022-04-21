import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author: Mr.Yu
 * @create: 2022-04-21 16:59
 **/
public class TestStop {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8085);
        OutputStream outputStream = socket.getOutputStream();
        String command = "shutdown";
        byte[] bytes = command.getBytes();
        outputStream.write(bytes);

    }

}
