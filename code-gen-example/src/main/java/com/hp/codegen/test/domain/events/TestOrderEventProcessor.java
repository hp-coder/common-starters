// --- Auto Generated By CodeGen Module ---
package com.hp.codegen.test.domain.events;

import com.hp.codegen.test.domain.TestOrder;
import com.hp.codegen.test.domain.command.CreateTestOrderCommand;
import com.hp.codegen.test.domain.command.UpdateTestOrderCommand;
import com.hp.codegen.test.domain.context.CreateTestOrderContext;
import com.hp.codegen.test.domain.context.UpdateTestOrderContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hp
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(
        rollbackFor = Exception.class
)
public class TestOrderEventProcessor {
    @EventListener
    public void handleTestOrderCreated(TestOrderEvents.TestOrderCreatedEvent event) {
        final CreateTestOrderContext context = event.getContext();
        final TestOrder entity = context.getEntity();
        final CreateTestOrderCommand command = context.getCommand();
        // handle event
    }

    @EventListener
    public void handleTestOrderUpdated(TestOrderEvents.TestOrderUpdatedEvent event) {
        final UpdateTestOrderContext context = event.getContext();
        final TestOrder entity = context.getEntity();
        final UpdateTestOrderCommand command = context.getCommand();
        // handle event
    }
}