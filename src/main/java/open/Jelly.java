package open;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Created by ubuntulaptop on 12/28/16.
 */
public class Jelly {
    private ImmutableMap.Builder<String, Function<Object, Object>> functions = new ImmutableMap.Builder<>();
    private ImmutableMap.Builder<String, Object> bindables = new ImmutableMap.Builder<>();


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
                    return genericInvocation(jsonObject.toString(), functionParametersAsString, functions.build().get(functionKey));
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

    private boolean isFunctionMatch(Method method, String functionKey){
       return this.functions.build().get(functionKey) != null
           && !"toString".equals(method.getName())
           && !"equals".equals(method.getName())
           && !"hashCode".equals(method.getName())
           && !"notify".equals(method.getName())
           && !"notifyAll".equals(method.getName())
           && !"wait".equals(method.getName());
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

    public <T> T build(Class<T> bindableClass){
        return (T) this.bindables.build().get(bindableClass.getName());
    }
}
