package open;

import com.google.gson.JsonObject;

import java.io.InputStream;

/**
 * Created by ubuntulaptop on 1/15/17.
 */
@FunctionalInterface
interface MongoFindCommand {
    JsonObject query();

    default String runCommand() {
        try {
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
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
