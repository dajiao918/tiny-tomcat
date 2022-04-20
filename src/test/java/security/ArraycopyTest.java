package security;

import org.apache.catalina.util.Mapper;

import java.util.Arrays;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 10:37
 **/
public class ArraycopyTest {

    public static void main(String[] args) {
        Integer[] old = new Integer[]{1,2,3,4,5};
        Integer[] newEle = new Integer[4];
        int pos = 1;
        removeElement(pos, old, newEle);
        System.out.println(Arrays.toString(newEle));
    }

    public static boolean removeElement(int pos, Object[] oldElements, Object[] newElements) {
        int len = oldElements.length;
        System.arraycopy(oldElements, 0, newElements, 0, pos+1);
        System.arraycopy(oldElements, pos+2, newElements, pos+1, len - pos - 2);
        return false;
    }

}
