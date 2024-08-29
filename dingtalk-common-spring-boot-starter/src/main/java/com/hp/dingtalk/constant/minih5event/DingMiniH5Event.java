package com.hp.dingtalk.constant.minih5event;


import com.hp.common.base.annotation.FieldDesc;
import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 事件订阅总览
 *
 * @author hp
 * @See https://open.dingtalk.com/document/orgapp/event-list
 */
public interface DingMiniH5Event {
    String getCode();

    String getName();

    static Optional<DingMiniH5Event> of(String code) {
        AtomicReference<DingMiniH5Event> holder = new AtomicReference<>(null);
        DingMiniH5EventHolder.holder()
                .forEach(
                        clz -> {
                            final DingMiniH5Event[] enumConstants = (DingMiniH5Event[]) clz.getEnumConstants();
                            final Optional<DingMiniH5Event> any = Arrays.stream(enumConstants).filter(e -> Objects.equals(e.getCode(), code)).findAny();
                            if (any.isEmpty() || Objects.nonNull(holder.get())) {
                                return;
                            }
                            holder.set(any.get());
                        }
                );
        return Optional.ofNullable(holder.get());
    }

    @Getter
    @AllArgsConstructor
    enum CheckEvent implements BaseEnum<CheckEvent, String>, DingMiniH5Event {
        /***/
        CHECK_URL("check_url", "检查配置URL事件"),
        ;
        private final String code;
        private final String name;

        public static Optional<CheckEvent> of(String code) {
            return Optional.ofNullable(BaseEnum.parseByCode(CheckEvent.class, code));
        }
    }

    @Getter
    @AllArgsConstructor
    enum OAEvent implements BaseEnum<OAEvent, String>, DingMiniH5Event {
        /***/
        TODO_CREATE("todo_task_create", "创建待办任务事件"),

        @FieldDesc("审批任务回调，是针对审批任务状态的推送; 有任务开始，任务结束和任务转交(转到下一审批人)共三个事件，可获取当前审批人的userId(针对审批流的各个审批人节点进行触发推送的场景)。")
        AUDIT_TASK_CHANGE("bpms_task_change", "审批任务回调"),

        @FieldDesc("针对审批实例状态的推送；有审批实例开始和审批实例结束两个事件，能得到发起审批人的userId(对整体审批流的触发推送的场景)")
        AUDIT_INSTANCE_CHANGE("bpms_instance_change", "审批实例回调"),
        ;
        private final String code;
        private final String name;

        public static Optional<OAEvent> of(String code) {
            return Optional.ofNullable(BaseEnum.parseByCode(OAEvent.class, code));
        }
    }

    @Getter
    @AllArgsConstructor
    enum ContactEvent implements BaseEnum<ContactEvent, String>, DingMiniH5Event {
        /***/
        USER_ADD_ORG("user_add_org","用户变更-通讯录用户增加。"),
        USER_ACTIVE_ORG("user_active_org","用户变更-加入企业后用户激活。"),
        USER_LEAVE_ORG("user_leave_org","用户变更-通讯录用户离职。"),
        ;
        private final String code;
        private final String name;

        public static Optional<ContactEvent> of(String code) {
            return Optional.ofNullable(BaseEnum.parseByCode(ContactEvent.class, code));
        }
    }
}
