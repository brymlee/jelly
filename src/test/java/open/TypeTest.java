package open;

import org.junit.Test;

import static open.CustomPredicate.entry;
import static org.junit.Assert.*;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
public class TypeTest {
    @Test
    public void jsonToPersonType(){
        try{
            final BoundSingleton<Integer> ageType = () -> entry(Integer.class, new CustomPredicate<>(Integer.class)
                .cannotBe(Integer.class, 24)
                .cannotBe(Integer.class, 23));
            final BoundSingleton<String> nameType = () -> entry(String.class, new CustomPredicate<>(String.class)
                .cannotBe(String.class, "hello"));
            //final Type personType = () -> asList(nameType, ageType);
            final Type personType = Jelly
                    .jsonToType(this.getClass()
                    .getResourceAsStream("personType.json"));
            assertNotNull(personType.exists(ageType));
            assertNull(personType.exists((BoundSingleton<Integer>) () -> entry(Integer.class, new CustomPredicate<>(Integer.class)
                    .cannotBe(Integer.class, 23))));
            assertNotNull(personType.exists(nameType));
            assertNull(personType.exists((BoundSingleton<String>) () -> entry(String.class, new CustomPredicate<>(String.class)
                .cannotBe(String.class, "hell"))));
            assertNull(personType.value(ageType, 23).value(ageType));
            assertNull(personType.value(ageType, 24).value(ageType));
            assertEquals(Integer.valueOf(25), personType.value(ageType, 25).value(ageType));
        }catch(Exception exception){
            throw new RuntimeException(exception);
        }
    }

}
