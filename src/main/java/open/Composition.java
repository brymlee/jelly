package open;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
@FunctionalInterface
public interface Composition extends Type{
    List<Class<?>> classes();

    default boolean exists(Singleton singleton){
        return classes()
            .stream()
            .filter(clazz -> clazz.getName().equals(singleton.clazz().getName()))
            .count() == 1;
    }

    default boolean exists(Class<?> clazz){
        return exists((Singleton) () -> clazz);
    }

    default boolean exists(Composition composition){
        if(composition.classes().size() != classes().size()){
            return false;
        }
        return range(0, classes().size())
            .filter(index -> classes()
                .get(index)
                .getName()
                .equals(composition
                    .classes()
                    .get(index)
                    .getName()))
            .count() == composition.classes().size();
    }

    default List<Type> types(){
        return classes()
            .stream()
            .map(clazz -> (Singleton) () -> clazz)
            .collect(Collectors.toList());
    }
}
