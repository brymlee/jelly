package open;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
public interface Type {
    List<Type> types();
    default boolean exists(Type targetType){
        return types()
            .stream()
            .filter(type -> {
                if(targetType instanceof Singleton){
                    if(type instanceof  Singleton){
                        return ((Singleton) type).exists((Singleton) targetType);
                    }else if(type instanceof Composition){
                        return ((Composition) type).exists((Singleton) targetType);
                    }
                    throw new RuntimeException();
                }else if(targetType instanceof Composition){
                    if(type instanceof Singleton){
                        return ((Singleton) type).exists((Composition) targetType);
                    }else if(type instanceof  Composition){
                        return ((Composition) type).exists((Composition) targetType);
                    }
                    throw new RuntimeException();
                }else{
                    throw new RuntimeException();
                }
            }).count() > 0;
    }

    default BoundSingleton boundSingleton(Class<?> clazz){
        Optional<BoundSingleton> boundSingleton = types()
            .stream()
            .filter(type -> {
                if (type instanceof BoundSingleton) {
                    return ((BoundSingleton) type).clazz().getName().equals(clazz.getName());
                }
                return false;
            }).map(type -> ((BoundSingleton) type))
            .reduce((i, j) -> i);
        if(boundSingleton.isPresent()){
            return boundSingleton.get();
        }
        return null;
    }

    default boolean exists(Class<?> clazz){
        return exists((Singleton) () -> clazz);
    }

    default <T> Type value(Class<T> clazz, T t){
        if(exists(clazz)){
            BoundSingleton<T> boundSingleton;
            if((boundSingleton = boundSingleton(clazz)) != null
            && boundSingleton
                .boundClazz()
                .getValue()
                .stream()
                .filter(predicate -> predicate.test(t))
                .count() != boundSingleton.boundClazz().getValue().size()){
                throw new RuntimeException();
            }
            return (Type) () -> new ImmutableList.Builder<Type>()
                .addAll(types()
                    .stream()
                    .filter(type -> !(type instanceof Singleton) || !((Singleton) type).clazz().getName().equals(clazz.getName()))
                    .collect(Collectors.toList()))
                .add((InstantiatedSingleton) () -> new ImmutableMap.Builder<Singleton, Object>()
                    .put((Singleton) () -> clazz, t)
                    .build()
                    .entrySet()
                    .stream()
                    .reduce((i, j) -> i)
                .get())
                .build();
        }
        return (Type) () -> types();
    }

    default <T> T value(Class<T> clazz){
        if(exists(clazz)){
            return (T) types()
                .stream()
                .filter(type -> type instanceof InstantiatedSingleton
                    && ((InstantiatedSingleton) type).clazz().getName().equals(clazz.getName()))
                .map(type -> ((InstantiatedSingleton) type))
                .reduce((i, j) -> i)
                .get()
                .value()
                .getValue();
        }
        return null;
    }
}
