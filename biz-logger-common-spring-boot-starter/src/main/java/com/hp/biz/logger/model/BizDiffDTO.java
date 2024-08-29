package com.hp.biz.logger.model;

import com.hp.common.base.annotation.FieldDesc;
import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author hp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BizDiffDTO {

    @FieldDesc("属性")
    private String attr;

    @FieldDesc("属性名")
    private String name;

    private List<DiffAction> actions;

    public BizDiffDTO(String attr, String name) {
        this.attr = attr;
        this.name = name;
    }

    @Data
    public static class DiffAction {

        @FieldDesc("操作状态: ADDED/CHANGED/REMOVED")
        private DiffState state;

        @FieldDesc("修改之前的值")
        private String before;

        @FieldDesc("如果存在转换, 这里是解析后的值, 如果没有转换, 则等于before")
        private String parsedBefore;

        @FieldDesc("修改之后的值")
        private String after;

        @FieldDesc("如果存在转换, 这里是解析后的值, 如果没有转换, 则等于after")
        private String parsedAfter;

        public void setState(String state) {
            this.state = DiffState.valueOf(state);
        }

        public void setState(DiffState state) {
            this.state = state;
        }

        public DiffAction(DiffState state, String before, String after) {
            this.state = state;
            this.before = before;
            this.after = after;
        }

        public DiffAction(String state, String before, String after) {
            this.state = DiffState.valueOf(state);
            this.before = before;
            this.after = after;
            this.parsedBefore = before;
            this.parsedAfter = after;
        }

        public boolean isAdded() {
            return Objects.equals(this.state, DiffState.ADDED);
        }

        public boolean isChanged() {
            return Objects.equals(this.state, DiffState.CHANGED);
        }

        public boolean isRemoved() {
            return Objects.equals(this.state, DiffState.REMOVED);
        }

        public String getAddedValue(boolean parsed) {
            if (isAdded()) {
                return parsed ? this.parsedAfter : this.after;
            }
            throw new IllegalStateException("The action is not a added action.");
        }

        public String getRemoveValue(boolean parsed) {
            if (isRemoved()) {
                return parsed ? this.parsedBefore : this.before;
            }
            throw new IllegalStateException("The action is not a removed action.");
        }
    }

    @AllArgsConstructor
    @Getter
    public enum DiffState implements BaseEnum<DiffState, Integer> {
        ADDED(1, "新增") {
            @Override
            public boolean meaningful() {
                return true;
            }
        },
        CHANGED(2, "修改") {
            @Override
            public boolean meaningful() {
                return true;
            }
        },
        REMOVED(3, "删除") {
            @Override
            public boolean meaningful() {
                return true;
            }
        },
        UNTOUCHED(4, "The value is identical in the working and base object"),
        CIRCULAR(5, "Special state to mark circular references"),
        IGNORED(6, "The value has not been looked at and has been ignored"),
        INACCESSIBLE(7, "When a comparison was not possible because the underlying value was not accessible"),
        ;
        private final Integer code;
        private final String name;

        public boolean meaningful() {
            return false;
        }
    }
}
