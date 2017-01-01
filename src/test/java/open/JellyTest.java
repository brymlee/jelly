package open;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by ubuntulaptop on 12/28/16.
 */
public class JellyTest {
    private interface Example{
        Integer add(Integer i, Integer j);
        Integer getInteger();
        String getHello();
        String getHelloAndInteger(Integer integer);
        String reverse(String string);
    }

    @Test
    public void basicBuild(){
        assertEquals((Integer) 3, new Jelly()
            .add(nothing -> 3, Example.class, Void.class, Integer.class)
            .build(Example.class)
            .getInteger());
    }

    @Test
    public void multipleBuild(){
        assertEquals("Hello", new Jelly()
            .add(nothing -> 3, Example.class, Void.class, Integer.class)
            .add(nothing -> "Hello", Example.class, Void.class, String.class)
            .build(Example.class)
            .getHello());
    }

    @Test
    public void multipleBuild2(){
        assertEquals((Integer) 3, new Jelly()
            .add(nothing -> "Hello", Example.class, Void.class, String.class)
            .add(nothing -> 3, Example.class, Void.class, Integer.class)
            .build(Example.class)
            .getInteger());
    }

    @Test
    public void singleParameter(){
        assertEquals("Hello3", new Jelly()
            .add(integer -> "Hello" + integer, Example.class, Integer.class, String.class)
            .build(Example.class)
            .getHelloAndInteger(3));
    }

    @Test
    public void multipleParameters(){
        assertEquals((Integer) 6, new Jelly()
            .add(Integer.class, Integer.class, Integer.class, Example.class, (i, j) -> i + j)
            .build(Example.class)
            .add(3, 3));
    }

    @Test
    public void reverseString(){
        assertEquals("olleh", new Jelly()
            .add(String.class, String.class, Example.class, string -> IntStream.range(0, string.length())
                    .map(index -> (string.length() - 1) - index)
                    .mapToObj(index -> "" + string.charAt(index))
                    .reduce((i, j) -> i.concat(j))
                    .get())
                .build(Example.class)
                .reverse("hello"));
    }

}
