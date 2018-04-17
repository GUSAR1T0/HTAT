package store.vxdesign.htat.tests.fixtures;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import store.vxdesign.htat.hosts.HostHolder;

interface Fixture {
    default void preparation(String step, Runnable runnable) {
        Logger logger = LogManager.getLogger();
        logger.info("Prepare to {} the testing", step);
        runnable.run();
        logger.info("Preparation to {} is over", step);
    }

    void setup(HostHolder holder);

    void teardown(HostHolder holder);
}
