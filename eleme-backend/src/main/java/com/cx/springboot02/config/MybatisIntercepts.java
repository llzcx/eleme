package com.cx.springboot02.config;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
 
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
 
@Slf4j
public class MybatisIntercepts implements InnerInterceptor {
    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        log.info("#####willDoQuery");
        return false;
    }
 
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        log.info("#####beforeQuery");
    }
   
    @Override
    public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        log.info("#####willDoUpdate");
        // 一堆sql处理仅供参考
        BoundSql boundSql = ms.getBoundSql(parameter);
        ms.getSqlSource().getClass();
        String sql = boundSql.getSql();
        Statement statement = null;
        try {
            statement =  CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        List<String> tableList = tablesNamesFinder.getTableList(statement);
        log.info("sql:{}",sql);
        return false;
    }
 
    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {

    }
 
    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {

    }

 
    @Override
    public void setProperties(Properties properties) {

    }
}