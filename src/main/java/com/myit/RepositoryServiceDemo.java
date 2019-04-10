package com.myit;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RepositoryServiceDemo {
    //流程引擎对象
    private ProcessEngine engine;
    //流程存储服务组件
    private RepositoryService repositoryService;

    //1.创建ProcessEngine对象，其他Service对象
    @Before
    public void before() {
        //ProcessEngines.getDefaultProcessEngine()=>会自动加载resources目录下的名为activiti.cfg.xml的配置文件
        engine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = engine.getRepositoryService();
    }

    @After
    public void after() {
        engine.close();
    }

    @Test
    public void test1(){
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        String processDefinitionKey = null;
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionKey(processDefinitionKey).singleResult();
        //中止流程
        repositoryService.suspendProcessDefinitionByKey(processDefinition.getKey());
        //或
        //repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        //激活流程
        repositoryService.activateProcessDefinitionByKey(processDefinition.getKey());
        //或
        //repositoryService.activateProcessDefinitionById(processDefinition.getId());
    }
}
