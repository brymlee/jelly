package open;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

/**
 * Created by ubuntulaptop on 1/6/17.
 */
//Integration Tests : These tests may require databases to be set up or the underlying system to be configured appropriately.
//DON'T EXPECT THESE TESTS TO PASS IF YOU DON'T HAVE EVERYTHING SET UP. THEY ARE NOT UNIT TESTS.
public class IOJellyTest {

    private interface PersonManager{
        List<Person> getAllPeople();
        Person getPerson(String name);
    }

    private interface Person{
        String name();

        default boolean equals(Person person){
            return new EqualsBuilder()
                .append(this.name(), person.name())
                .build();
        }
    }

    @FunctionalInterface
    private interface MongoFindCommand{
        JsonObject query();
        default String runCommand(){
            try{
                Process process = Runtime
                    .getRuntime()
                    .exec(new String[]{"mongo", "example", "--quiet", "--eval", "db.example.person.find({query})"
                        .replaceAll("\\{query}", query() == null ? "" : query().toString())});
                process.waitFor();
                InputStream inputStream = process.getInputStream();
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();
                return new String(bytes);
            }catch(Exception exception){
                throw new RuntimeException(exception);
            }
        }
    }

    @Test
    public void getPersonHello(){
        final Person expectedPerson = (Person) () -> "hello";
        final Person actualPerson = new Jelly()
            .add(String.class, Person.class, PersonManager.class, name -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", name);
                final String result = ((MongoFindCommand) () -> jsonObject).runCommand();
                List<Person> people = asList(result.split("\n"))
                    .stream()
                    .map(personString -> (Person) () -> new JsonParser()
                        .parse(personString)
                        .getAsJsonObject()
                        .get("name")
                        .getAsString()
                    ).collect(Collectors.toList());
                if(people.size() != 1){
                    throw new RuntimeException();
                }
                return people.get(0);
            }).build(PersonManager.class)
            .getPerson("hello");
        assertTrue(expectedPerson.equals(actualPerson));
    }

    @Test
    public void getAllPeople(){
        final List<Person> list = new Jelly()
            .add(Void.class, List.class, PersonManager.class, nothing -> {
                try{
                    return asList(((MongoFindCommand) () -> null).runCommand().split("\n"))
                        .stream()
                        .map(personString -> {
                            final JsonObject jsonObject = new JsonParser()
                                .parse(personString)
                                .getAsJsonObject();
                            return (Person) () -> jsonObject.get("name").getAsString();
                        }).collect(Collectors.toList());
                }catch(Exception exception){
                    throw new RuntimeException(exception);
                }
            }).build(PersonManager.class)
            .getAllPeople();
        checkNotNull(list);
        final Person helloPerson = () -> "hello";
        final Person bobPerson = () -> "bob";
        if(!list.get(0).equals(helloPerson) || !list.get(1).equals(bobPerson)){
            throw new RuntimeException();
        }
    }

}
