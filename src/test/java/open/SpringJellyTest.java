package open;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.lang.reflect.Proxy;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

/**
 * Created by ubuntulaptop on 1/8/17.
 */
@ContextConfiguration("file:src/test/resources/exampleApplicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringJellyTest {
    @FunctionalInterface
    public interface GenericBeanFactory<T>{
        T getBeanType();

        default AbstractFactoryBean<T> getInstance(Class<T> clazz, JsonElement jsonElement){
            return new AbstractFactoryBean<T>(){
                @Override
                public Class<?> getObjectType() {
                   return clazz;
                }

                @Override
                protected T createInstance() throws Exception {
                    final JsonArray jsonArray = jsonElement.getAsJsonArray();
                    ImmutableMap<String, Object> values = new ImmutableMap.Builder<String, Object>()
                        .putAll(range(0, jsonArray.size())
                            .mapToObj(index -> jsonArray.get(index).getAsJsonObject())
                            .map(entry -> {
                                try{
                                    final String key = entry.get("key").getAsString();
                                    final String[] split = key.split(":");
                                    if(split.length != 2){
                                        throw new RuntimeException();
                                    }
                                    final String name = split[0];
                                    final String type = split[1];
                                    final byte[] valueBytes = Base64.decode(entry.get("value").getAsString().getBytes());
                                    switch(type.toLowerCase()){
                                        case "string" :
                                            return new ImmutableMap.Builder<String, Object>()
                                                .put(name, new String(valueBytes))
                                                .build()
                                                .entrySet()
                                                .stream()
                                                .reduce((i, j) -> i)
                                                .get();
                                        case "integer" :
                                            return new ImmutableMap.Builder<String, Object>()
                                                .put(name, Integer.parseInt(new String(valueBytes)))
                                                .build()
                                                .entrySet()
                                                .stream()
                                                .reduce((i, j) -> i)
                                                .get();
                                        default :
                                            throw new RuntimeException();
                                    }
                                }catch(Exception exception){
                                    throw new RuntimeException(exception);
                                }
                            }).collect(Collectors.toList()))
                        .build();
                    return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, (proxy, method, parameters) -> {
                        if("toString".equals(method.getName())
                        && "equals".equals(method.getName())
                        && "hashCode".equals(method.getName())
                        && "notify".equals(method.getName())
                        && "notifyAll".equals(method.getName())
                        && "wait".equals(method.getName())){
                            return method.invoke(proxy, parameters);
                        }
                        if(parameters == null || parameters.length == 0){
                            return values.get(method.getName());
                        }else{
                            throw new RuntimeException();
                        }
                    });
                }
            };
        }
    }

    public interface PersonManager{
        String getNames();
    }

    @Autowired
    private PersonManager personManager;

    @Test
    public void getBean(){
        System.out.println(personManager.getNames());
    }
}
