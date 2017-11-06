package codesquad;

import org.junit.ClassRule;
import org.junit.Test;

import codesquad.test.SparkServerRule;

public class HomeControllerTest {
    @ClassRule
    public static final SparkServerRule SPARK_SERVER = new SparkServerRule(http -> {
        http.get("/ping", (request, response) -> "pong");
        http.get("/health", (request, response) -> "healthy");
    });
    
    @Test
    public void testSparkServerRule_PingRequest() {
        
    }
}
