package open;

import java.util.List;
import java.util.stream.Collectors;

import static open.CustomPredicate.entry;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
public interface Type {
    List<Type> types();
    default Type exists(Type targetType){
        final List<Type> types = types()
            .stream()
            .map(type -> {
                if(targetType instanceof Singleton){
                    if(type instanceof Singleton){
                        return ((Singleton) type).exists((Singleton) targetType);
                    }else if(type instanceof Composition){
                        return ((Composition) type).exists((Singleton) targetType);
                    }else if(type instanceof BoundSingleton){
                        return ((BoundSingleton) type).exists((Singleton) targetType);
                    }
                    throw new RuntimeException();
                }else if(targetType instanceof Composition){
                    if(type instanceof Singleton){
                        return ((Singleton) type).exists((Composition) targetType);
                    }else if(type instanceof  Composition){
                        return ((Composition) type).exists((Composition) targetType);
                    }else if(type instanceof BoundSingleton){
                        return ((BoundSingleton) type).exists((Composition) targetType);
                    }
                    throw new RuntimeException();
                }else if(targetType instanceof BoundSingleton){
                    if(type instanceof Singleton){
                        return ((Singleton) type).exists((BoundSingleton) targetType);
                    }else if(type instanceof  Composition){
                        return ((Composition) type).exists((BoundSingleton) targetType);
                    }else if(type instanceof BoundSingleton){
                        return ((BoundSingleton) type).exists((BoundSingleton) targetType);
                    }
                    throw new RuntimeException();
                }else{
                    throw new RuntimeException();
                }
            }).filter(type -> type != null)
            .collect(Collectors.toList());
        if(types.size() == 1){
            return types.get(0);
        }else{
            return null;
        }

    }

    default <T> Type value(BoundSingleton<T> boundSingleton, T t){
        final BoundSingleton<T> boundSingletonToChange = (BoundSingleton<T>) exists(boundSingleton);
        if(boundSingletonToChange != null){
            return () -> types()
                .stream()
                .map(type -> {
                    if(type instanceof BoundSingleton
                    && ((BoundSingleton) type).is(boundSingletonToChange.boundClazz().getValue()) != null
                    && boundSingletonToChange.boundClazz().getValue().apply(t)){
                       return (InstantiatedBoundSingleton<T>) () -> entry(boundSingletonToChange, t);
                    }
                    return type;
                }).collect(Collectors.toList());
        }
        return null;
    }

    default <T> T value(BoundSingleton<T> expectedBoundSingleton){
        final BoundSingleton<T> actualBoundSingleton = (BoundSingleton<T>) exists(expectedBoundSingleton);
        if(actualBoundSingleton instanceof InstantiatedBoundSingleton){
            return ((InstantiatedBoundSingleton<T>) actualBoundSingleton).instantiated().getValue();
        }
        return null;
    }
}
