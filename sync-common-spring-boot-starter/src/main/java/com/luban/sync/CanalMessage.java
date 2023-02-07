package com.hp.sync;


import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class CanalMessage implements Serializable {

    private static final long serialVersionUID = 4274196690583416581L;
    private String destination;                            // 对应canal的实例或者MQ的topic
    private String groupId;                                // 对应mq的group id
    private String database;                               // 数据库或schema
    private String table;                                  // 表名
    private List<String> pkNames;
    private Boolean isDdl;
    private String type;                                   // 类型: INSERT UPDATE DELETE
    // binlog executeTime
    private Long es;                                     // 执行时间
    // dml build timeStamp
    private Long ts;                                     // 同步时间
    private String sql;                                    // 执行的sql, dml sql为空
    private List<Map<String, Object>> data;                                   // 数据列表
    private List<Map<String, Object>> old;                                    // 旧数据列表, 用于update, size和data的size一一对应

    public static CanalMessage instance(byte[] payload) {
        final String string = new String(payload)
                .replace("\"0000-00-00\"", "null");
        return JSON.parseObject(string, CanalMessage.class);
    }
}
