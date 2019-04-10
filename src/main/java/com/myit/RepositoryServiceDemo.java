package com.myit;

import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class RepositoryServiceDemo {
    //流程引擎对象
    private ProcessEngine engine;
    //流程存储服务组件
    private RepositoryService repositoryService;

    private IdentityService identityService;

    //1.创建ProcessEngine对象，其他Service对象
    @Before
    public void before() {
        //ProcessEngines.getDefaultProcessEngine()=>会自动加载resources目录下的名为activiti.cfg.xml的配置文件
        engine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = engine.getRepositoryService();
        identityService = engine.getIdentityService();
    }

    @After
    public void after() {
        engine.close();
    }

    @Test
    public void test1() {
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

    @Test
    public void test2() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        String processDefinitionKey = null;
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionKey(processDefinitionKey).singleResult();
        //设置流程有权限启动的用户
        repositoryService.addCandidateStarterUser(processDefinition.getId(), "userId");
        //设置流程有权限启动的用户组
        repositoryService.addCandidateStarterGroup(processDefinition.getId(), "groupId");
    }

    @Test
    public void test3() {
        ProcessDefinitionQuery processDefinitionQuery1 = repositoryService.createProcessDefinitionQuery();
        //根据用户查询有权限启动的流程
        List<ProcessDefinition> processDefinitions = processDefinitionQuery1.startableByUser("userId").list();

        //根据流程id查询有权限启动的用户组
        List<Group> groupList = identityService.createGroupQuery().potentialStarter("processDefinitionId").list();
        //根据流程id查询有权限启动的用户
        List<User> userList = identityService.createUserQuery().potentialStarter("processDefinitionId").list();
    }

    @Test
    public void test4() {
        //查询流程定义文件
        InputStream processDefinition = repositoryService.getProcessModel("processDefinitionId");
        //查询流程图
        InputStream processDiagram = repositoryService.getProcessDiagram("processDefinitionId");
    }
}
