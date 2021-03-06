package open;

import open.CustomPredicate.CannotBe;
import open.CustomPredicate.GreaterThan;
import open.CustomPredicate.MustContain;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;
import static open.CustomPredicate.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ubuntulaptop on 12/28/16.
 */
public class BasicJellyTest {
    private interface Example{
        String multiplyString(String string, Integer integer);
        Integer add(Integer i, Integer j);
        Integer getInteger();
        String getHello();
        String getHelloAndInteger(Integer integer);
        String reverse(String string);
        String threeParametersConcat(String s, String s2, String s3);
        String fourParametersConcat(String s, String s2, String s3, String s4);
        String fiveParametersConcat(String s, String s2, String s3, String s4, String s5);
        String sixParametersConcat(String s, String s2, String s3, String s4, String s5, String s6);
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
    public void twoParameters(){
        assertEquals((Integer) 6, new Jelly()
            .add(Integer.class, Integer.class, Integer.class, Example.class, (i, j) -> i + j)
            .build(Example.class)
            .add(3, 3));
    }

    @Test
    public void twoParametersMultiplyConcat(){
        assertEquals("HelloHello", new Jelly()
            .add(String.class, Integer.class, String.class, Example.class, (string, integer) -> range(0, string.length() * integer)
                .mapToObj(i -> String.valueOf(string.charAt(i % string.length())))
                .reduce((i, j) -> i.concat(j))
                .get())
            .build(Example.class)
            .multiplyString("Hello", 2));
    }

    @Test
    public void threeParametersConcat(){
        assertEquals("HelloByeGoodbye", new Jelly()
            .add(String.class, String.class, String.class, String.class, Example.class, (s, s1, s2) -> s.concat(s1).concat(s2))
            .build(Example.class)
            .threeParametersConcat("Hello", "Bye", "Goodbye"));
    }

    @Test
    public void fourParametersConcat(){
        assertEquals("HelloByeGoodbyeHello", new Jelly()
            .add(String.class, String.class, String.class, String.class, String.class, Example.class, (s, s1, s2, s3) -> s.concat(s1)
                .concat(s2)
                .concat(s3))
            .build(Example.class)
            .fourParametersConcat("Hello", "Bye", "Goodbye", "Hello"));
    }

    @Test
    public void fiveParametersConcat(){
        assertEquals("HelloByeGoodbyeHelloBye", new Jelly()
            .add(String.class, String.class, String.class, String.class, String.class, String.class, Example.class, (s, s1, s2, s3, s4) -> s.concat(s1)
                .concat(s2)
                .concat(s3)
                .concat(s4))
            .build(Example.class)
            .fiveParametersConcat("Hello", "Bye", "Goodbye", "Hello", "Bye"));
    }

    @Test
    public void sixParametersConcat(){
        assertEquals("HelloByeGoodbyeHelloByeGoodbye", new Jelly()
            .add(String.class, String.class, String.class, String.class, String.class, String.class, String.class, Example.class, (s, s1, s2, s3, s4, s5) -> s.concat(s1)
                .concat(s2)
                .concat(s3)
                .concat(s4)
                .concat(s5))
            .build(Example.class)
            .sixParametersConcat("Hello", "Bye", "Goodbye", "Hello", "Bye", "Goodbye"));
    }

    @Test
    public void reverseString(){
        assertEquals("olleh", new Jelly()
            .add(String.class, String.class, Example.class, string -> range(0, string.length())
                .map(index -> (string.length() - 1) - index)
                .mapToObj(index -> "" + string.charAt(index))
                .reduce((i, j) -> i.concat(j))
                .get())
            .build(Example.class)
            .reverse("hello"));
    }

    public interface PersonTypeExampleInterface{
        @PredicateInterfaces( predicateTypes = {MustContain.class, MustContain.class, MustContain.class, MustContain.class, MustContain.class}
                             ,predicateValues ={"h"              , "e"              , "l"              , "l"              , "o"})
        String reverseHello(String string);

        @PredicateInterfaces( predicateTypes = {GreaterThan.class}
                             ,predicateValues ={"2"})
        Integer add(Integer x, Integer y);

        @PredicateInterfaces( predicateTypes = {MustContain.class, CannotBe.class}
                             ,predicateValues ={"hello", "goodbye"})
        String getString(String string);

    }

    @Test
    public void predicateJellyReverseString(){
        //final BoundSingleton<String> nameType = () -> entry(String.class, new CustomPredicate<>(String.class)
        //        .mustContain(String.class, asList("h", "e", "l", "l", "o")));
        final PersonTypeExampleInterface personTypeExampleInterface = new Jelly()
            .add(String.class, String.class, PersonTypeExampleInterface.class, string -> range(0, string.length())
                .map(index -> (string.length() - 1) - index)
                .mapToObj(index -> "" + string.charAt(index))
                .reduce((i, j) -> i.concat(j))
                .get())
            .build(PersonTypeExampleInterface.class);
        assertEquals("olleh", personTypeExampleInterface.reverseHello("hello"));
        try{
            assertEquals("olleh", personTypeExampleInterface.reverseHello("bob"));
            assertFalse(true);
        }catch(RuntimeException runtimeException){
            assertTrue(true);
        }
    }

    @Test
    public void predicateJellyAddIntegers(){
        final PersonTypeExampleInterface personTypeExampleInterface  = new Jelly()
            .add(Integer.class, Integer.class, Integer.class, PersonTypeExampleInterface.class, (x, y) -> x + y)
            .build(PersonTypeExampleInterface.class);
        assertEquals(Integer.valueOf(3), personTypeExampleInterface.add(1, 2));
        try{
            assertEquals(Integer.valueOf(1), personTypeExampleInterface.add(0, 1));
            assertFalse(true);
        }catch(RuntimeException runtimeException){
            assertTrue(true);
        }
    }

    @Test
    public void predicateJellyGetString(){
        final PersonTypeExampleInterface personTypeExampleInterface = new Jelly()
            .add(String.class, String.class, PersonTypeExampleInterface.class, (string) -> range(0, 3)
                .mapToObj(index -> string)
                .reduce((i, j) -> i.concat(j))
                .get())
            .build(PersonTypeExampleInterface.class);
        assertEquals("hellohellohello", personTypeExampleInterface.getString("hello"));
        try{
            assertEquals("goodbyegoodbyegoodbye", personTypeExampleInterface.getString("goodbye"));
            assertFalse(true);
        }catch(RuntimeException runtimeException){
            assertTrue(true);
        }
    }

}
