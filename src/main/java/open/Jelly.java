package open;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import open.CustomPredicate.PredicateInterface;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static open.CustomPredicate.entry;

/**
 * Created by ubuntulaptop on 12/28/16.
 */
public class Jelly {
    private ImmutableMap.Builder<String, Function<Object, Object>> functions = new ImmutableMap.Builder<>();
    private ImmutableMap.Builder<String, Object> bindables = new ImmutableMap.Builder<>();
    private ImmutableMap.Builder<String, CustomPredicate> customPredicatesBuilder = new ImmutableMap.Builder<>();

    @FunctionalInterface
    public interface Function2<T, T1, T2>{
        T2 function(T t, T1 t1);

        default Function<Object, Object> genericFunction(){
            return parameters -> {
                checkArgument(parameters instanceof List);
                final List<Object> parametersAsList = (List<Object>) parameters;
                checkArgument(parametersAsList.size() == 2);
                return (T2) function((T) parametersAsList.get(0), (T1) parametersAsList.get(1));
            };
        }
    }

    @FunctionalInterface
    public interface Function3<T, T1, T2, T3>{
        T3 function(T t, T1 t1, T2 t2);

        default Function<Object, Object> genericFunction(){
            return parameters -> {
                checkArgument(parameters instanceof List);
                final List<Object> parametersAsList = (List<Object>) parameters;
                checkArgument(parametersAsList.size() == 3);
                return (T3) function((T) parametersAsList.get(0), (T1) parametersAsList.get(1), (T2) parametersAsList.get(2));
            };
        }
    }

    @FunctionalInterface
    public interface Function4<T, T1, T2, T3, T4>{
        T4 function(T t, T1 t1, T2 t2, T3 t3);

        default Function<Object, Object> genericFunction(){
            return parameters -> {
                checkArgument(parameters instanceof List);
                final List<Object> parametersAsList = (List<Object>) parameters;
                checkArgument(parametersAsList.size() == 4);
                return (T4) function((T) parametersAsList.get(0),
                                     (T1) parametersAsList.get(1),
                                     (T2) parametersAsList.get(2),
                                     (T3) parametersAsList.get(3));
            };
        }
    }

    @FunctionalInterface
    public interface Function5<T, T1, T2, T3, T4, T5>{
        T5 function(T t, T1 t1, T2 t2, T3 t3, T4 t4);

        default Function<Object, Object> genericFunction(){
            return parameters -> {
                checkArgument(parameters instanceof List);
                final List<Object> parametersAsList = (List<Object>) parameters;
                checkArgument(parametersAsList.size() == 5);
                return (T5) function((T) parametersAsList.get(0),
                        (T1) parametersAsList.get(1),
                        (T2) parametersAsList.get(2),
                        (T3) parametersAsList.get(3),
                        (T4) parametersAsList.get(4));
            };
        }
    }

    @FunctionalInterface
    public interface Function6<T, T1, T2, T3, T4, T5, T6>{
        T6 function(T t, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

        default Function<Object, Object> genericFunction(){
            return parameters -> {
                checkArgument(parameters instanceof List);
                final List<Object> parametersAsList = (List<Object>) parameters;
                checkArgument(parametersAsList.size() == 6);
                return (T6) function((T) parametersAsList.get(0),
                        (T1) parametersAsList.get(1),
                        (T2) parametersAsList.get(2),
                        (T3) parametersAsList.get(3),
                        (T4) parametersAsList.get(4),
                        (T5) parametersAsList.get(5));
            };
        }
    }

    public <T, T1, T2> Jelly add(Class<T> firstType, Class<T1> secondType, Class<T2> bindableClass, Function<T, T1> function){
        add((Function<Object, Object>) function, bindableClass, firstType, secondType);
        return this;
    }

    public <T, T1, T2, T3> Jelly add(Class<T> firstType, Class<T1> secondType, Class<T2> thirdType, Class<T3> bindableClass, Function2<T, T1, T2> function){
        add(function.genericFunction(), bindableClass, firstType, secondType, thirdType);
        return this;
    }

    public <T, T1, T2, T3, T4> Jelly add(Class<T> firstType,
                                         Class<T1> secondType,
                                         Class<T2> thirdType,
                                         Class<T3> fourthType,
                                         Class<T4> bindableClass,
                                         Function3<T, T1, T2, T3> function){
        add(function.genericFunction(), bindableClass, firstType, secondType, thirdType, fourthType);
        return this;
    }

    public <T, T1, T2, T3, T4, T5> Jelly add(Class<T> firstType,
                                         Class<T1> secondType,
                                         Class<T2> thirdType,
                                         Class<T3> fourthType,
                                         Class<T4> fifthType,
                                         Class<T5> bindableClass,
                                         Function4<T, T1, T2, T3, T4> function){
        add(function.genericFunction(), bindableClass, firstType, secondType, thirdType, fourthType, fifthType);
        return this;
    }

    public <T, T1, T2, T3, T4, T5, T6> Jelly add(Class<T> firstType,
                                             Class<T1> secondType,
                                             Class<T2> thirdType,
                                             Class<T3> fourthType,
                                             Class<T4> fifthType,
                                             Class<T5> sixthType,
                                             Class<T6> bindableClass,
                                             Function5<T, T1, T2, T3, T4, T5> function){
        add(function.genericFunction(), bindableClass, firstType, secondType, thirdType, fourthType, fifthType, sixthType);
        return this;
    }

    public <T, T1, T2, T3, T4, T5, T6, T7> Jelly add(Class<T> firstType,
                                                 Class<T1> secondType,
                                                 Class<T2> thirdType,
                                                 Class<T3> fourthType,
                                                 Class<T4> fifthType,
                                                 Class<T5> sixthType,
                                                 Class<T6> seventhType,
                                                 Class<T7> bindableClass,
                                                 Function6<T, T1, T2, T3, T4, T5, T6> function){
        add(function.genericFunction(), bindableClass, firstType, secondType, thirdType, fourthType, fifthType, sixthType, seventhType);
        return this;
    }

    /*public <T, T1, T2> Jelly add(Class<T> firstType,
                                 BoundSingleton<T1> secondType,
                                 Class<T2> bindableType,
                                 Function<T, T1> function){
        //final List<Map.Entry<Class<? extends PredicateInterface>, T1>> entry = secondType.boundClazz().getValue().id();
        this.customPredicatesBuilder = this.customPredicatesBuilder
            .put(chainClassNames(secondType
                .boundClazz()
                .getValue()
                .id()
                .stream()
                .map(i -> i.getKey())
                .collect(Collectors.toList())), secondType.boundClazz().getValue());
        add((Function<Object, Object>) function, bindableType, firstType, secondType.clazz());
        return this;
    }*/

    public <T> Jelly add(Function<Object, Object> function, Class<T> bindableClass, Class<?> ... classes){
        this.functions = this.functions.put(chainClassNames(asList(classes)), function);
        if(!this.bindables.build().containsKey(bindableClass.getName())){
            this.bindables = this.bindables.put(bindableClass.getName(), Proxy.newProxyInstance(Jelly.class.getClassLoader(), new Class[]{bindableClass}, (proxy, method, args) -> {
                final String functionKey = args == null
                        ? chainClassNames(asList(Void.class, method.getReturnType()))
                        : chainClassNames(asList(args).stream().map(arg -> arg.getClass()).collect(toList()), method.getReturnType());

                if(isFunctionMatch(method, functionKey)){
                    final List<String> split = asList(functionKey.split(";"));
                    final List<String> functionParametersAsString = range(0, split.size() - 1)
                            .mapToObj(index -> "".equals(split.get(index).trim()) ? null : split.get(index))
                            .filter(arg -> arg == null ? false : true)
                            .collect(toList());
                    if(Void.class.getName().equals(functionParametersAsString.get(0)) && args == null){
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("parameters", new JsonArray());
                        return genericInvocation(jsonObject.toString(), new ArrayList<>(), this.functions.build().get(functionKey));
                    }
                    checkArgument(functionParametersAsString.size() == args.length);
                    JsonArray jsonArray = new JsonArray();
                    range(0, functionParametersAsString.size())
                            .mapToObj(index -> {
                                try{
                                    return args[index];
                                }catch(Exception exception){
                                    throw new RuntimeException(exception);
                                }
                            }).forEach(arg -> {
                        try{
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(arg);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(arg.getClass().getName(), Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
                            jsonArray.add(jsonObject);
                            objectOutputStream.close();
                            byteArrayOutputStream.close();
                        }catch (Exception exception){
                            throw new RuntimeException(exception);
                        }
                    });
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("parameters", jsonArray);
                    final Annotation[] annotations = method.getDeclaredAnnotations();
                    final List<PredicateInterfaces> matchedAnnotations = IntStream.range(0, annotations.length)
                        .mapToObj(i -> {
                            return annotations[i] instanceof PredicateInterfaces
                                ? (PredicateInterfaces) annotations[i]
                                : null;
                        })
                        .filter(i -> i != null)
                        .collect(Collectors.toList());
                    final PredicateInterfaces matchedAnnotation = matchedAnnotations.size() > 0 ? matchedAnnotations.get(0) : null;
                    if(matchedAnnotation != null){
                        final CustomPredicate customPredicate = CustomPredicate.getPredicate(matchedAnnotation);
                        if(customPredicate != null){
                            final Function<Object, Object> predicateWrappedFunction = object -> {
                                final Object result = this.functions.build().get(functionKey).apply(object);
                                if(customPredicate.apply(result)){
                                    return result;
                                }
                                throw new RuntimeException();
                            };
                            return genericInvocation(jsonObject.toString(), functionParametersAsString, predicateWrappedFunction);
                        }
                    }
                    return genericInvocation(jsonObject.toString(), functionParametersAsString, this.functions.build().get(functionKey));
                }
                try{
                    return method.invoke(proxy, args);
                }catch(InvocationTargetException invocationTargetException){
                    throw invocationTargetException.getCause();
                }
            }));

        }
        return this;
    }

    public static String chainClassNames(List<Class<?>> classes, Class<?> clazz){
        Class<?>[] array = new Class<?>[classes.size() + 1];
        range(0, array.length)
            .forEach(index -> {
                if(index == array.length - 1){
                    array[index] = clazz;
                }else{
                    array[index] = classes.get(index);
                }
            });
        return chainClassNames(asList(array));
    }

    public static String chainClassNames(List<Class<?>> classes) {
        return classes.stream()
            .map(clazz -> clazz.getName())
            .reduce((i, j) -> i.concat(";").concat(j))
            .get()
            .trim();
    }

    public static boolean isFunctionMatch(ImmutableMap.Builder<String, Function<Object, Object>> functions, Method method, String functionKey){
        return functions != null
            && functions.build().get(functionKey) != null
            && !"toString".equals(method.getName())
            && !"equals".equals(method.getName())
            && !"hashCode".equals(method.getName())
            && !"notify".equals(method.getName())
            && !"notifyAll".equals(method.getName())
            && !"wait".equals(method.getName());
    }

    private boolean isFunctionMatch(Method method, String functionKey){
        return isFunctionMatch(this.functions, method, functionKey);
    }

    private Object genericInvocation(String functionParametersAsJson, List<String> functionParametersAsString, Function<Object, Object> function){
        final JsonArray jsonArray = new JsonParser()
            .parse(functionParametersAsJson)
            .getAsJsonObject()
            .getAsJsonArray("parameters");
        if(functionParametersAsString.size() == 0){
            return function.apply(null);
        }else if(functionParametersAsString.size() == 1){
            final byte[] bytes = Base64.getDecoder().decode(jsonArray
                .get(0)
                .getAsJsonObject()
                .get(functionParametersAsString.get(0))
                .getAsString());
            try{
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Object object = objectInputStream.readObject();
                objectInputStream.close();
                return function.apply(object);
            }catch(Exception exception){
                throw new RuntimeException(exception);
            }
        }else{
            final List<Object> parameters = range(0, jsonArray.size())
                .mapToObj(index -> {
                    final byte[] bytes = Base64.getDecoder().decode(jsonArray
                        .get(index)
                        .getAsJsonObject()
                        .get(functionParametersAsString.get(index))
                        .getAsString()
                        .getBytes());
                    try{
                        return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
                    }catch(Exception exception){
                        throw new RuntimeException(exception);
                    }
                }).collect(Collectors.toList());
            return function.apply(parameters);
        }
    }

    public static Type jsonToType(InputStream inputStream){
        try{
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            final JsonObject typeJson = new JsonParser()
                    .parse(new String(bytes))
                    .getAsJsonObject();
            final JsonArray types = typeJson
                    .get("_types")
                    .getAsJsonArray();
            return (Type) () -> range(0, types.size())
                    .mapToObj(index -> {
                        final String key = types.get(index).getAsJsonObject().get(index + "").getAsString();
                        final JsonObject currentType = typeJson.get(key).getAsJsonObject();
                        final JsonArray cannotBe = currentType.get("cannotBe").getAsJsonArray();
                        switch(currentType.get("base").getAsString().toLowerCase()){
                            case "integer" :
                                if(cannotBe.size() > 0){
                                    final CustomPredicate<Integer> customPredicate = new CustomPredicate<>(Integer.class)
                                            .addAll(range(0, cannotBe.size())
                                                    .mapToObj(i -> cannotBe.get(i).getAsString())
                                                    .map(string -> (CustomPredicate.CannotBe<Integer>) () ->  Integer.valueOf(string))
                                                    .collect(toList()));
                                    return (BoundSingleton<Integer>) () -> entry(Integer.class, customPredicate);
                                }
                                throw new RuntimeException();
                            case "string" :
                                if(cannotBe.size() > 0){
                                    final CustomPredicate<String> customPredicate = new CustomPredicate<>(String.class)
                                            .addAll(range(0, cannotBe.size())
                                                    .mapToObj(i -> cannotBe.get(i).getAsString())
                                                    .map(string -> (CustomPredicate.CannotBe<String>) () ->  string)
                                                    .collect(toList()));
                                    return (BoundSingleton<String>) () -> entry(String.class, customPredicate);
                                }
                            default :
                                throw new RuntimeException();
                        }
                    }).collect(toList());
        }catch(Exception exception){
            throw new RuntimeException(exception);
        }
    }

    public <T> T build(Class<T> bindableClass){
        return (T) this.bindables.build().get(bindableClass.getName());
    }
}
