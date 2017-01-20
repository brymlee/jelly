package open;

import open.SpringJellyTest.PersonManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ubuntulaptop on 1/8/17.
 */
@Configuration
public class SpringJellyTestConfiguration {
    @Bean
    public PersonManager personManagerBean(){
        return new Jelly()
            .add(Void.class, String.class, PersonManager.class, nothing -> "hello")
            .build(PersonManager.class);
    }
}
