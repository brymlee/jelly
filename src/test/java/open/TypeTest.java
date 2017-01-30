package open;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import open.CustomPredicate.CannotBe;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static open.CustomPredicate.entry;
import static org.junit.Assert.*;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
public class TypeTest {
    @Test
    public void typeExistsSingleton(){
        assertEquals(true, ((Composition) () -> asList(String.class, Integer.class)).exists(String.class));
        assertEquals(false, ((Composition) () -> asList(String.class, Integer.class)).exists(Float.class));
    }

    @Test
    public void typeExistsSingletonComplextType(){
        assertEquals(true, ((Type) () -> asList((Singleton) () -> Integer.class)).exists((Singleton) () -> Integer.class));
        assertEquals(false, ((Type) () -> asList((Singleton) () -> Integer.class)).exists((Singleton) () -> Float.class));
    }

    @Test
    public void typeExistsCompositionComplexType(){
        assertEquals(true, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class))).exists((Composition) () -> asList(Integer.class, String.class))); assertEquals(false, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class))).exists((Composition) () -> asList(Integer.class, Float.class)));
        assertEquals(true, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class),
                                                (Singleton) () -> Float.class)).exists((Composition) () -> asList(Integer.class, String.class)));
        assertEquals(false, ((Type) () -> asList((Composition) () -> asList(Integer.class, String.class),
                                                 (Singleton) () -> Float.class)).exists((Composition) () -> asList(Integer.class, Float.class)));
    }

    @Test
    public void jsonToType(){
        try{
            InputStream inputStream = this.getClass().getResourceAsStream("personType.json");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            final JsonObject personTypeJson = new JsonParser()
                .parse(new String(bytes))
                .getAsJsonObject();
            final JsonArray personTypesJson = personTypeJson
                .get("_types")
                .getAsJsonArray();
            final Type personType = (Type) () -> range(0, personTypesJson.size())
                .mapToObj(index -> {
                    final String key = personTypesJson.get(index).getAsJsonObject().get(index + "").getAsString();
                    final JsonObject currentType = personTypeJson.get(key).getAsJsonObject();
                    final JsonArray cannotBe = currentType.get("cannotBe").getAsJsonArray();
                    switch(currentType.get("base").getAsString().toLowerCase()){
                        case "integer" :
                            if(cannotBe.size() > 0){
                                final CustomPredicate<Integer> customPredicate = new CustomPredicate<>(Integer.class)
                                    .addAll(range(0, cannotBe.size())
                                        .mapToObj(i -> cannotBe.get(i).getAsString())
                                        .map(string -> (CannotBe<Integer>) () ->  Integer.valueOf(string))
                                        .collect(toList()));
                                return (BoundSingleton<Integer>) () -> entry(Integer.class, customPredicate);
                            }
                            throw new RuntimeException();
                        case "string" :
                            if(cannotBe.size() > 0){
                                final CustomPredicate<String> customPredicate = new CustomPredicate<>(String.class)
                                    .addAll(range(0, cannotBe.size())
                                        .mapToObj(i -> cannotBe.get(i).getAsString())
                                        .map(string -> (CannotBe<String>) () ->  string)
                                        .collect(toList()));
                                return (BoundSingleton<String>) () -> entry(String.class, customPredicate);
                            }
                        default :
                            throw new RuntimeException();
                    }
                }).collect(toList());
            final BoundSingleton<Integer> ageType = (BoundSingleton<Integer>) () -> entry(Integer.class, new CustomPredicate<>(Integer.class)
                .cannotBe(Integer.class, 24)
                .cannotBe(Integer.class, 23));
            final BoundSingleton<String> nameType = (BoundSingleton<String>) () -> entry(String.class, new CustomPredicate<>(String.class)
                .cannotBe(String.class, "hello"));
            assertNotNull(personType.exists(ageType));
            assertNull(personType.exists((BoundSingleton<Integer>) () -> entry(Integer.class, new CustomPredicate<>(Integer.class)
                    .cannotBe(Integer.class, 23))));
            assertNotNull(personType.exists(nameType));
            assertNull(personType.exists((BoundSingleton<String>) () -> entry(String.class, new CustomPredicate<>(String.class)
                .cannotBe(String.class, "hell"))));
            assertNull(personType.value(ageType, 23).value(ageType));
            assertNull(personType.value(ageType, 24).value(ageType));
            assertEquals(Integer.valueOf(25), personType.value(ageType, 25).value(ageType));
        }catch(Exception exception){
            throw new RuntimeException(exception);
        }
    }

}
