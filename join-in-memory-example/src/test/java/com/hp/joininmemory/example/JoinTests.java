package com.hp.joininmemory.example;

import com.hp.joininmemory.JoinService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author hp
 */
@SpringBootTest(classes = BootstrapApplication.class)
public class JoinTests {

    @Autowired
    JoinRepository joinRepository;

    @Autowired
    JoinService joinService;

    /*
     * Grouped Join:
     * If multiple fields in a class are annotated with the same join-annotation,
     * those join processes will be grouped as one to be executed to reduce the
     * amount of times accessing the datasource.
     *
     * JoinInMemory.keyFromJoinData()
     * JoinInMemory.joinDataKeyConverter()
     * JoinInMemory.loader()
     * JoinInMemory.runLevel();
     *
     * The values above are used to create grouping keys.
     */


    /**
     * The field removedBy in the joinTester is annotated with
     * the same annotation as the field creator and the field updater
     * but with a custom property of sourceDataKeyConverter()
     * of which doesn't affect the grouping process.
     */
    @Test
    public void test_grouped_join_scene1() {
        final JoinTester joinTester = new JoinTester("1", "2", 3L);
        joinService.joinInMemory(joinTester);
        System.out.println(joinTester);
    }

    /**
     * The field removedBy in the JoinTester2 has the same annotation
     * as the field creator and the field updater, except it has one more
     * custom runLevel field whose value differs from the default (FIFTH).
     * The runLevel value will affect the grouping process.
     * <p>
     * 2024-01-09 11:36:38.541  INFO 32852 --- [Memory-Thread-2] c.h.joininmemory.example.JoinRepository  : Querying, ids=3
     * 2024-01-09 11:36:38.541  INFO 32852 --- [Memory-Thread-1] c.h.joininmemory.example.JoinRepository  : Querying, ids=1,2
     * JoinTester2(createdBy=1, creator=user1, updatedBy=2, updater=user1, removedBy=3, remover=user1)
     */
    @Test
    public void test_grouped_join_scene2() {
        final JoinTester2 joinTester = new JoinTester2("1", "2");
        joinTester.setRemovedBy(3L);
        joinService.joinInMemory(joinTester);
        System.out.println(joinTester);
    }

    /**
     * The JoinTester3 is annotated with JoinInMemoryConfig annotation,
     * but its processPolicy field is set to SEPARATED(default) without grouping.
     * <p>
     * 2024-01-09 11:42:09.445  INFO 32930 --- [Memory-Thread-1] c.h.joininmemory.example.JoinRepository  : Querying, ids=3
     * 2024-01-09 11:42:09.452  INFO 32930 --- [Memory-Thread-1] c.h.joininmemory.example.JoinRepository  : Querying, ids=1
     * 2024-01-09 11:42:09.453  INFO 32930 --- [Memory-Thread-2] c.h.joininmemory.example.JoinRepository  : Querying, ids=2
     * JoinTester3(createdBy=1, creator=user1, updatedBy=2, updater=user1, removedBy=3, remover=user1)
     */
    @Test
    public void test_grouped_join_scene3() {
        final JoinTester3 joinTester = new JoinTester3("1", "2");
        joinTester.setRemovedBy(3L);
        joinService.joinInMemory(joinTester);
        System.out.println(joinTester);
    }
}
