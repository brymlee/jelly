package open;

import open.CustomPredicate.PredicateInterface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ubuntulaptop on 2/10/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@interface PredicateInterfaces {
    Class<? extends PredicateInterface>[] predicateTypes();
    String[] predicateValues();
}
