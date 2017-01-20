package open;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
@FunctionalInterface
public interface Singleton extends Type{
    Class<?> clazz();

    default boolean exists(Singleton singleton){
        return singleton.clazz().getName().equals(this.clazz().getName());
    }

    default boolean exists(Composition composition){
        return false;
    }

    default boolean exists(Class<?> clazz){
        return exists((Singleton) () -> clazz);
    }

    default List<Type> types(){
        return Arrays.asList((Singleton) () -> clazz());
    }
}
