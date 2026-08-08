insert into sys_config (k, v)
values ('text.to.sql.eval.metadata', '{
  "evalTargets": [
    {"label": "端到端效果", "value": "END_TO_END", "color": "blue"},
    {"label": "问题路由", "value": "ROUTING", "color": "purple"},
    {"label": "业务规则/Skill", "value": "BUSINESS_SKILL", "color": "cyan"},
    {"label": "指标检索", "value": "METRIC_RETRIEVAL", "color": "geekblue"},
    {"label": "表结构检索", "value": "TABLE_SCHEMA_RETRIEVAL", "color": "gold"},
    {"label": "SQL生成", "value": "SQL_GENERATION", "color": "green"},
    {"label": "澄清判断", "value": "CLARIFICATION", "color": "orange"},
    {"label": "结果分析", "value": "RESULT_ANALYSIS", "color": "magenta"}
  ],
  "sampleCategories": [
    {"label": "正常样本", "value": "NORMAL", "color": "green"},
    {"label": "边界样本", "value": "BOUNDARY", "color": "orange"},
    {"label": "回归问题", "value": "REGRESSION", "color": "red"},
    {"label": "歧义澄清", "value": "AMBIGUOUS", "color": "gold"},
    {"label": "负向样本", "value": "NEGATIVE", "color": "default"}
  ],
  "assertionOperators": [
    {"label": "等于", "value": "EQ"},
    {"label": "包含", "value": "CONTAINS"},
    {"label": "不包含", "value": "NOT_CONTAINS"},
    {"label": "存在", "value": "EXISTS"},
    {"label": "不为空", "value": "NOT_EMPTY"},
    {"label": "正则匹配", "value": "REGEX"},
    {"label": "语义判断", "value": "SEMANTIC"}
  ],
  "failureTypes": [
    {"label": "路由错误", "value": "ROUTING_ERROR"},
    {"label": "表选择错误", "value": "TABLE_SELECTION_ERROR"},
    {"label": "字段选择错误", "value": "FIELD_SELECTION_ERROR"},
    {"label": "SQL动作错误", "value": "SQL_ACTION_ERROR"},
    {"label": "SQL生成错误", "value": "SQL_GENERATION_ERROR"},
    {"label": "澄清判断错误", "value": "CLARIFICATION_ERROR"},
    {"label": "回答质量错误", "value": "ANSWER_QUALITY_ERROR"},
    {"label": "State断言错误", "value": "STATE_ASSERT_ERROR"}
  ]
}')
on duplicate key update
    v = values(v);

