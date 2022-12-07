package com.hp.sync;

import com.hp.sync.support.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncMessage implements Serializable {
    private static final long serialVersionUID = 3774135273878229520L;
    private String table;
    private Constants.DML type;
    private boolean isMain;
    private String pk;
    private List<Long> pkValues;

    public SyncMessage(String table, Constants.DML type) {
        this.table = table;
        this.type = type;
    }
}
