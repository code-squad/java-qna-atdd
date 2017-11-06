package codesquad.test;

import spark.Service;

@FunctionalInterface
public interface ServiceInitializer {

    void init(Service service);

}