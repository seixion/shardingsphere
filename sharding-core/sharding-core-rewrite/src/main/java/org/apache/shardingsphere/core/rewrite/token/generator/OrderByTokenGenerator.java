/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.rewrite.token.generator;

import com.google.common.base.Optional;
import org.apache.shardingsphere.core.optimize.statement.OptimizedStatement;
import org.apache.shardingsphere.core.optimize.statement.sharding.dml.select.OrderByItem;
import org.apache.shardingsphere.core.optimize.statement.sharding.dml.select.ShardingSelectOptimizedStatement;
import org.apache.shardingsphere.core.parse.sql.segment.dml.order.item.TextOrderByItemSegment;
import org.apache.shardingsphere.core.parse.sql.statement.dml.SelectStatement;
import org.apache.shardingsphere.core.rewrite.builder.ParameterBuilder;
import org.apache.shardingsphere.core.rewrite.token.pojo.OrderByToken;
import org.apache.shardingsphere.core.rule.ShardingRule;

/**
 * Order by token generator.
 *
 * @author zhangliang
 */
public final class OrderByTokenGenerator implements OptionalSQLTokenGenerator<ShardingRule>, IgnoreForSingleRoute {
    
    @Override
    public Optional<OrderByToken> generateSQLToken(final OptimizedStatement optimizedStatement, final ParameterBuilder parameterBuilder, final ShardingRule shardingRule) {
        if (!(optimizedStatement instanceof ShardingSelectOptimizedStatement)) {
            return Optional.absent();
        }
        if (((ShardingSelectOptimizedStatement) optimizedStatement).isToAppendOrderByItems()) {
            return Optional.of(createOrderByToken((ShardingSelectOptimizedStatement) optimizedStatement));
        }
        return Optional.absent();
    }
    
    private OrderByToken createOrderByToken(final ShardingSelectOptimizedStatement optimizedStatement) {
        OrderByToken result = new OrderByToken(((SelectStatement) optimizedStatement.getSQLStatement()).getGroupByLastIndex() + 1);
        for (OrderByItem each : optimizedStatement.getOrderByItems()) {
            String columnLabel = each.getSegment() instanceof TextOrderByItemSegment ? ((TextOrderByItemSegment) each.getSegment()).getText() : String.valueOf(each.getIndex());
            result.getColumnLabels().add(columnLabel);
            result.getOrderDirections().add(each.getSegment().getOrderDirection()); 
        }
        return result;
    }
}
