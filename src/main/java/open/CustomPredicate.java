package open;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Created by ubuntulaptop on 1/23/17.
 */
public class CustomPredicate<T>{
    private CustomPredicate(){

    }

    public CustomPredicate(Class<T> clazz){
        this.clazz = clazz;
    }

    private Class<T> clazz;
    private ImmutableList.Builder<PredicateInterface<T>> predicateInterfaces = new ImmutableList.Builder<>();

    public static <T, T1> Map.Entry<T, T1> entry(T t, T1 t1){
        return new ImmutableMap.Builder<T, T1>()
            .put(t, t1)
            .build()
            .entrySet()
            .stream()
            .reduce((i, j) -> i)
            .get();
    }

    public CustomPredicate<T> cannotBe(Class<T> clazz, T t){
        this.predicateInterfaces = this.predicateInterfaces
            .add((CannotBe<T>) () ->  t);
        return this;
    }

    public Boolean apply(T t){
        return this.predicateInterfaces.build()
            .stream()
            .allMatch(predicateInterface -> predicateInterface.predicateEntry().getKey().test(t));
    }

    public List<Map.Entry<Class<? extends PredicateInterface>, T>> id() {
        final List<PredicateInterface<T>> predicateInterfaces = this.predicateInterfaces.build();
        return range(0, predicateInterfaces.size())
            .mapToObj(index -> {
                final PredicateInterface<T> predicateInterface = predicateInterfaces.get(index);
                if(predicateInterface instanceof CannotBe){
                    Map.Entry<Class<? extends PredicateInterface>, T> entry = entry(CannotBe.class, predicateInterface.predicateEntry().getValue());
                    return entry;
                }else if(predicateInterface instanceof MustContain){
                    Map.Entry<Class<? extends PredicateInterface>, T> entry = entry(MustContain.class, predicateInterface.predicateEntry().getValue());
                    return entry;
                }else{
                    throw new RuntimeException();
                }
            }).collect(toList());
    }

    public CustomPredicate<T> addAll(List<PredicateInterface<T>> predicateInterfaces) {
        this.predicateInterfaces = this.predicateInterfaces.addAll(predicateInterfaces);
        return this;
    }

    public CustomPredicate<T> greaterThan(Class<T> clazz, T t){
        this.predicateInterfaces = this.predicateInterfaces
                .add((GreaterThan<T>) () ->  t);
        return this;
    }

    public CustomPredicate<T> mustContain(Class<T> clazz, T t){
        this.predicateInterfaces = this.predicateInterfaces
            .add((MustContain<T>) () -> t);
        return this;
    }

    public CustomPredicate<T> mustContain(Class<T> clazz, List<T> ts){
        ts.forEach(t -> mustContain(clazz, t));
        return this;
    }

    public static CustomPredicate getPredicate(PredicateInterfaces matchedAnnotation) {
        final Class<? extends PredicateInterface>[] predicateTypes = matchedAnnotation.predicateTypes();
        final String[] predicateValues = matchedAnnotation.predicateValues();
        if(predicateTypes.length != predicateValues.length){
            throw new RuntimeException();
        }
        return new CustomPredicate()
            .addAll(range(0, predicateTypes.length)
                .mapToObj(index -> {
                    if(CannotBe.class.getName().equals(predicateTypes[index].getName())){
                        return (CannotBe) () -> predicateValues[index];
                    }else if(MustContain.class.getName().equals(predicateTypes[index].getName())){
                        return (MustContain) () -> predicateValues[index];
                    }else if(GreaterThan.class.getName().equals(predicateTypes[index].getName())){
                        return (GreaterThan) () -> predicateValues[index];
                    }else{
                        throw new RuntimeException();
                    }
                }).collect(toList()));
    }

    @FunctionalInterface
    public interface PredicateInterface<T>{
        Map.Entry<Predicate<T>, T> predicateEntry();
    }

    @FunctionalInterface
    public interface CannotBe<T> extends PredicateInterface<T>{
        T t();
        default Map.Entry<Predicate<T>, T> predicateEntry(){
            return entry(t -> !t.equals(t()), t());
        }
    }

    @FunctionalInterface
    public interface MustContain<T> extends PredicateInterface<T>{
        T t();
        default Map.Entry<Predicate<T>, T> predicateEntry(){
            if(t() instanceof String){
                return entry((t) -> ((String) t).contains((String) t()), t());
            }else{
                throw new RuntimeException();
            }
        }
    }

    @FunctionalInterface
    public interface GreaterThan<T> extends PredicateInterface<T>{
        T t();
        default Map.Entry<Predicate<T>, T> predicateEntry(){
            if(t() instanceof Integer){
                return entry((t) -> ((Integer) t).compareTo((Integer) t()) > 0, t());
            }else if(t() instanceof Float){
                return entry((t) -> ((Float) t).compareTo((Float) t()) > 0, t());
            }else if(t() instanceof Double){
                return entry((t) -> ((Double) t).compareTo((Double) t()) > 0, t());
            }else if(t() instanceof String){
                return entry((t) -> ((Integer) t).compareTo(Integer.parseInt((String) t())) > 0, t());
            }else{
                throw new RuntimeException();
            }
        }
    }

}
