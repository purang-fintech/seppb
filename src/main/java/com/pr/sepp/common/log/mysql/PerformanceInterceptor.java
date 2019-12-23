package com.pr.sepp.common.log.mysql;

import com.pr.sepp.common.config.PerformanceProperties;
import com.google.common.base.Stopwatch;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Intercepts(value = {
		@Signature(args = {Statement.class, ResultHandler.class}, method = "query", type = StatementHandler.class),
		@Signature(args = {Statement.class}, method = "update", type = StatementHandler.class),
		@Signature(args = {Statement.class}, method = "batch", type = StatementHandler.class)})
public class PerformanceInterceptor implements Interceptor {
	private static Logger mysqlSlowQuery = LoggerFactory.getLogger("sepp.sql.slow");

	private PerformanceProperties properties = new PerformanceProperties();

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		Stopwatch started = Stopwatch.createStarted();
		StatementHandler statementHandler = (StatementHandler) target;
		Object obj = invocation.proceed();
		long endTime = started.stop().elapsed(TimeUnit.MILLISECONDS);
		if (properties.getSlowTime() <= endTime) {
			BoundSql boundSql = statementHandler.getBoundSql();
			String sql = boundSql.getSql();
            mysqlSlowQuery.info("执行耗时：{} 毫秒[slowSql]{} ", endTime, sql);
		}
		return obj;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		Object slowTimeObject = properties.get("slowTime");
		Long slowTime = Long.valueOf(String.valueOf(slowTimeObject));
		this.properties.setSlowTime(slowTime);
	}
}
