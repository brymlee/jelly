package open;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
@FunctionalInterface
public interface Composition extends Type{
    List<Class<?>> classes();

    default Type exists(BoundSingleton boundSingleton){
        return null;
    }

    default Type exists(Singleton singleton){
        return classes()
            .stream()
            .filter(clazz -> clazz.getName().equals(singleton.clazz().getName()))
            .map(clazz -> (Singleton) () -> clazz)
            .reduce((i, j) -> i)
            .get();
    }

    default Type exists(Class<?> clazz){
        return exists((Singleton) () -> clazz);
    }

    default Type exists(Composition composition){
        if(composition.classes().size() != classes().size()){
            return null;
        }
        return (Composition) () -> range(0, classes().size())
            .filter(index -> classes()
                .get(index)
                .getName()
                .equals(composition
                    .classes()
                    .get(index)
                    .getName()))
            .mapToObj(index -> classes().get(index))
            .collect(toList());
    }

    default List<Type> types(){
        return classes()
            .stream()
            .map(clazz -> (Singleton) () -> clazz)
            .collect(toList());
    }
}
