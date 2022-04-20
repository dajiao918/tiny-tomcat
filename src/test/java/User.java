import org.apache.catalina.coyote.InternalInputNioBuffer;
import org.apache.catalina.coyote.Request;
import org.apache.catalina.coyote.RequestFacade;
import org.apache.catalina.pageResovler.PageResolver;

/**
 * @author: Mr.Yu
 * @create: 2022-04-20 00:20
 **/
public class User {

    private String name = "user";

    private Girl girl;

    public String getName() {
        return name;
    }

    public Girl getGirl() {
        return girl;
    }

    public void setGirl(Girl girl) {
        this.girl = girl;
    }

    public static void main(String[] args) {
        // 需要注释CoyoteOutputStream和RequestFacade的一些代码
        Request request = new Request();
        InternalInputNioBuffer buffer = new InternalInputNioBuffer(request);
        request.setInputBuffer(buffer);
        RequestFacade facade = new RequestFacade(request);
        User user = new User();
        Girl girl = new Girl();
        user.setGirl(girl);
        facade.setAttribute("user", user);
        String page = "<div>${request.user.name}</div>\n"+"<div>${request.user.girl.name}</div>";
        byte[] resolve = PageResolver.resolve(page.getBytes(), facade);
        System.out.println(new String(resolve));
    }

}
