package com.hp.joininmemory.feature_nested_join;

import com.hp.joininmemory.annotation.AfterJoin;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.annotation.NestedJoin;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JoinInMemoryConfig(
        executorType = JoinInMemoryExecutorType.SERIAL,
        fieldProcessPolicy = JoinFieldProcessPolicy.SEPARATED
)
public class User {

    private Long id;

    private String username;

    @NestedJoin
    @JoinOrderOnUserId("id")
//    private List<Order> orders;
    private Order order;

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    @AfterJoin
    public void userAfterJoin() {
        System.out.println("User After Join!!!");
        System.out.println("this.order = " + this.order);
    }
}
