package open;

import open.CustomPredicate.PredicateInterface;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;

/**
 * Created by ubuntulaptop on 1/20/17.
 */
@FunctionalInterface
public interface BoundSingleton<T> extends Type{
    Map.Entry<Class<T>, CustomPredicate<T>> boundClazz();

    default List<Type> types(){
        return Arrays.asList(null);
    }
    default Class<T> clazz(){
        return boundClazz().getKey();
    }

    default Type exists(BoundSingleton boundSingleton){
        return is((CustomPredicate) boundSingleton.boundClazz().getValue());
    }

    default Type exists(Singleton singleton) {
        return null;
    }

    default Type is(CustomPredicate expectedCustomPredicate){
        final List<Map.Entry<Class<? extends PredicateInterface>, T>> expectedEntries = expectedCustomPredicate.id();
        final List<Map.Entry<Class<? extends PredicateInterface>, T>> actualEntries = boundClazz().getValue().id();
        if(expectedEntries.size() != actualEntries.size()){
            return null;
        }
        return range(0, expectedEntries.size())
            .allMatch(index -> {
                return expectedEntries.get(index).getKey().getName().equals(actualEntries.get(index).getKey().getName())
                    && expectedEntries.get(index).getValue().getClass().getName().equals(actualEntries.get(index).getValue().getClass().getName())
                    && expectedEntries.get(index).getValue().equals(actualEntries.get(index).getValue());
            }) ? this : null;
    }
}
