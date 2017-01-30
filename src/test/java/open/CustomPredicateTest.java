package open;

import open.CustomPredicate.CannotBe;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;
import static open.CustomPredicate.entry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ubuntulaptop on 1/23/17.
 */
public class CustomPredicateTest {
    @Test
    public void basicTruthCheck(){
        assertFalse(new CustomPredicate(String.class)
            .cannotBe(String.class, "hello")
            .apply("hello"));
    }

    @Test
    public void basicNegativeTruthCheck(){
        assertTrue(new CustomPredicate(String.class)
            .cannotBe(String.class, "hello")
            .apply("hell"));
    }

    @Test
    public void predicateIdCheck(){
        final List<Map.Entry<Class<?>, String>> expectedPredicateId = new ArrayList<>(asList(entry(CannotBe.class, "hello")));
        final List<Map.Entry<Class<?>, String>> actualPredicateId = new CustomPredicate(String.class)
            .cannotBe(String.class, "hello")
            .id();
        if(expectedPredicateId.size() != actualPredicateId.size()){
            throw new RuntimeException();
        }
        assertTrue(range(0, expectedPredicateId.size())
            .filter(index ->
                expectedPredicateId.get(index).getKey().getName().equals(actualPredicateId.get(index).getKey().getName())
             && expectedPredicateId.get(index).getValue().getClass().getName().equals(actualPredicateId.get(index).getValue().getClass().getName()))
            .count() == expectedPredicateId.size());
    }
}
