package open;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
@FunctionalInterface
public interface Singleton extends Type{
    Class<?> clazz();

    default Type exists(BoundSingleton boundSingleton){
        return null;
    }

    default Type exists(Singleton singleton){
        return singleton.clazz().getName().equals(this.clazz().getName()) ? this : null;
    }

    default Type exists(Composition composition){
        return null;
    }

    default Type exists(Class<?> clazz){
        return this.clazz().getName().equals(clazz.getName()) ? this : null;
    }

    default List<Type> types(){
        return Arrays.asList((Singleton) () -> clazz());
    }
}
