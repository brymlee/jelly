package open;

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
                checkArgument(parameters instanceof Map);
                final Set<Map.Entry<String, Object>> parametersAsSet = ((Map<String, Object>) parameters).entrySet();
                checkArgument(parametersAsSet.size() == 2);
                Iterator<Map.Entry<String, Object>> iterator = parametersAsSet.iterator();
                final Object firstParameter = iterator.next().getValue();
                final Object secondParameter = iterator.next().getValue();
                return (T2) function((T) firstParameter, (T1) secondParameter);
            };
        }
    }

    public <T, T1, T2, T3> Jelly add(Class<T> firstType, Class<T1> secondType, Class<T2> thirdType, Class<T3> bindableClass, Function2<T, T1, T2> function){
        add(function.genericFunction(), bindableClass, firstType, secondType, thirdType);
        return this;
    }

    public <T, T1, T2> Jelly add(Class<T> firstType, Class<T1> secondType, Class<T2> bindableClass, Function<T, T1> function){
        add((Function<Object, Object>) function, bindableClass, firstType, secondType);
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
            final Set<Map.Entry<Object, Object>> parameters = range(0, jsonArray.size())
                .mapToObj(index -> {
                    final byte[] bytes = Base64.getDecoder().decode(jsonArray
                        .get(index)
                        .getAsJsonObject()
                        .get(functionParametersAsString.get(index))
                        .getAsString()
                        .getBytes());
                    try{
                        return new ImmutableMap.Builder<>()
                            .put(index + "", new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject())
                            .build()
                            .entrySet()
                            .stream()
                            .reduce((i, j) -> i)
                            .get();
                    }catch(Exception exception){
                        throw new RuntimeException(exception);
                    }
                }).collect(Collectors.toSet());
            final ImmutableMap<Object, Object> parametersAsImmutableMap = new ImmutableMap.Builder<>()
                    .putAll(parameters)
                    .build();
            return function.apply(parametersAsImmutableMap);
        }
    }

    public <T> T build(Class<T> bindableClass){
        return (T) this.bindables.build().get(bindableClass.getName());
    }
}
