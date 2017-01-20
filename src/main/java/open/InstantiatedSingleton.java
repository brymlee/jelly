package open;

import java.util.Map;

/**
 * Created by ubuntulaptop on 1/20/17.
 */
@FunctionalInterface
public interface InstantiatedSingleton extends Singleton{
    Map.Entry<Singleton, Object> value();

    default Class<?> clazz(){
        return value().getKey().clazz();
    }
}
