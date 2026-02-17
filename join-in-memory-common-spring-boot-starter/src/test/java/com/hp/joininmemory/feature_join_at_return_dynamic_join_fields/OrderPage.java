package com.hp.joininmemory.feature_join_at_return_dynamic_join_fields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPage {

    private List<Order> rows;
}
