package open;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
public class TypeTest {
    @Test
    public void typeExistsSingleton(){
        assertEquals(true, ((Composition) () -> asList(String.class, Integer.class)).exists(String.class));
        assertEquals(false, ((Composition) () -> asList(String.class, Integer.class)).exists(Float.class));
    }

    @Test
    public void typeExistsSingletonComplextType(){
        assertEquals(true, ((Type) () -> asList((Singleton) () -> Integer.class)).exists(Integer.class));
        assertEquals(false, ((Type) () -> asList((Singleton) () -> Integer.class)).exists(Float.class));
    }

    @Test
    public void typeExistsCompositionComplexType(){
        assertEquals(true, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class))).exists((Composition) () -> asList(Integer.class, String.class))); assertEquals(false, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class))).exists((Composition) () -> asList(Integer.class, Float.class)));
        assertEquals(true, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class),
                                                (Singleton) () -> Float.class)).exists((Composition) () -> asList(Integer.class, String.class)));
        assertEquals(false, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class),
                                                 (Singleton) () -> Float.class)).exists((Composition) () -> asList(Integer.class, Float.class)));
    }

    @Test
    public void personType(){
        final Type personType = (Type) () -> asList((Singleton) () -> String.class, (Singleton) () -> Integer.class);
        assertEquals(true, personType.exists(String.class));
        assertEquals(true, personType.exists(Integer.class));
        assertEquals(false, personType.exists(Float.class));
        final Type person = personType
            .value(String.class, "John Smith")
            .value(Integer.class, 24);
        assertEquals("John Smith", person.value(String.class));
        assertEquals(Integer.valueOf(24), person.value(Integer.class));
    }

    @Test
    public void personComplexType(){
        final BoundSingleton<String> nameType = () -> entry(String.class, asList((Predicate<String>) (string) -> string.length() < 5));
        final BoundSingleton<Integer> ageType = () -> entry(Integer.class, asList((Predicate<Integer>) (integer) -> integer.intValue() < 30));
        final Type personType = () -> asList(ageType, nameType);
        final Type person = personType
            .value(String.class, "John")
            .value(Integer.class, 24);
        assertEquals("John", person.value(String.class));
        assertEquals(Integer.valueOf(24), person.value(Integer.class));
    }

    @Test(expected = RuntimeException.class)
    public void personComplexTypeFail1(){
        final BoundSingleton<String> nameType = () -> entry(String.class, asList((Predicate<String>) (string) -> string.length() < 5));
        final BoundSingleton<Integer> ageType = () -> entry(Integer.class, asList((Predicate<Integer>) (integer) -> integer.intValue() < 30));
        final Type personType = () -> asList(ageType, nameType);
        final Type person = personType
            .value(String.class, "John")
            .value(Integer.class, 30);
    }

    @Test(expected = RuntimeException.class)
    public void personComplexTypeFail2(){
        final BoundSingleton<String> nameType = () -> entry(String.class, asList((Predicate<String>) (string) -> string.length() < 5));
        final BoundSingleton<Integer> ageType = () -> entry(Integer.class, asList((Predicate<Integer>) (integer) -> integer.intValue() < 30));
        final Type personType = () -> asList(ageType, nameType);
        final Type person = personType
            .value(String.class, "John Madden")
            .value(Integer.class, 24);
    }

    public static <T, T1> Map.Entry<T, T1> entry(T t, T1 t1){
        return new ImmutableMap.Builder<T, T1>()
            .put(t, t1)
            .build()
            .entrySet()
            .stream()
            .reduce((i, j) -> i)
            .get();
    }

}
