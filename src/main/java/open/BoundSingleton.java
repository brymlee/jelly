package open;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ubuntulaptop on 1/20/17.
 */
@FunctionalInterface
public interface BoundSingleton<T> extends Singleton{
    Map.Entry<Class<T>, List<Predicate<T>>> boundClazz();

    default Class<?> clazz(){
        return boundClazz().getKey();
    }
}
