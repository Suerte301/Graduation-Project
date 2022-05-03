package com.suerte.lostandfound.config;

import cn.hutool.core.util.IdUtil;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/17
 * @Description:
 */
@Configuration
public class SQLLogConfig {

    @Bean
    public QueryExecutionListener queryExecutionListener() {
        return new QueryExecutionListener() {
            public final Logger log = LoggerFactory.getLogger(getClass());

            @Override
            public void beforeQuery(ExecutionInfo executionInfo, List<QueryInfo> list) {
                String id = "";
                try {
                    Connection connection = executionInfo.getStatement().getConnection();
                    id = IdUtil.simpleUUID();
                    connection.setClientInfo("current SQL ID", id);
                    log.debug("id为 {} 的sql语句track ==> 当前执行的 sql 的数据库名字 {}", id, connection.getCatalog());
                    DatabaseMetaData metaData = connection.getMetaData();
                    log.debug("id为 {} 的sql语句track ==> 当前执行的 sql 的数据库URL {}", id, metaData.getURL());
                    log.debug("id为 {} 的sql语句track ==> 当前执行的 sql 的数据库类型 {}", id, metaData.getDatabaseProductName());
                    log.debug("id为 {} 的sql语句track ==> 当前执行的 sql 的数据库版本 {}", id, metaData.getDatabaseProductVersion());
                } catch (Exception e) {
                    log.error("设置track的SQLID失败 报错信息{} 报错位置{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
                for (QueryInfo queryInfo : list) {
                    String query = queryInfo.getQuery();
                    for (List<ParameterSetOperation> parameterSetOperations : queryInfo.getParametersList()) {
                        for (ParameterSetOperation parameterSetOperation : parameterSetOperations) {
                            log.debug("id为 {} 的sql语句track ==> 当前执行的 sql {} 的对应参数 {}  ", id, query, Arrays.toString(parameterSetOperation.getArgs()));
                        }
                    }
                }
            }

            @Override
            public void afterQuery(ExecutionInfo executionInfo, List<QueryInfo> list) {
                String currentSqlId = "";
                try {
                    currentSqlId = executionInfo.getStatement().getConnection().getClientInfo("current SQL ID");
                } catch (Exception e) {
                    log.error("获取track的SQLID失败 报错信息{} 报错位置{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                }
                log.debug("id为 {} 的sql语句track ==> 当前执行的sql的connectionID为{}", currentSqlId, executionInfo.getConnectionId());
                log.debug("id为 {} 的sql语句track ==> 当前执行的sql的总执行时间为{}ms", currentSqlId, executionInfo.getElapsedTime());
                log.debug("id为 {} 的sql语句track ==> 当前执行的sql的最终返回结果为{}", currentSqlId, executionInfo.getResult());

            }
        };

    }
}
