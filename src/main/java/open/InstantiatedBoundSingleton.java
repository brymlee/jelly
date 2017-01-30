package open;

import java.util.Map;

/**
 * Created by ubuntulaptop on 1/29/17.
 */
@FunctionalInterface
public interface InstantiatedBoundSingleton<T> extends BoundSingleton<T>{
    Map.Entry<BoundSingleton<T>, T> instantiated();

    default Map.Entry<Class<T>, CustomPredicate<T>> boundClazz(){
        return instantiated().getKey().boundClazz();
    }
}
