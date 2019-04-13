package com.myit;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;


//1.任务有候选人、持有人、代理人、候选组
//2.持有人、代理人都只能各有一个；而候选人、候选组可以有多个
public class TaskServiceDemo {
    //流程引擎对象
    private ProcessEngine engine;
    //流程存储服务组件
    private RepositoryService repositoryService;
    //流程运行时服务组件
    private RuntimeService runtimeService;
    //流程任务组件
    private TaskService taskService;

    //1.创建ProcessEngine对象，其他Service对象
    @Before
    public void before() {
        //ProcessEngines.getDefaultProcessEngine()=>会自动加载resources目录下的名为activiti.cfg.xml的配置文件
        engine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = engine.getRepositoryService();
        runtimeService = engine.getRuntimeService();
        taskService = engine.getTaskService();
    }


    @After
    public void after() {
        engine.close();
    }

    //1.删除任务
    @Test
    public void test1() {
        String taskId = null;
        Task task = taskService.newTask(taskId);
        //删除task，不包括历史数据（ACT_HI_TASKINST）和子任务
        taskService.deleteTask(taskId);
        //删除task，不包括历史数据（ACT_HI_TASKINST）和子任务
        taskService.deleteTask(taskId, false);
        //删除task，包括历史数据（ACT_HI_TASKINST）和子任务
        taskService.deleteTask(taskId, true);

        Collection<String> taskIds = null;
        taskService.deleteTasks(taskIds);
    }

    //2.设置任务候选用户、候选组；根据候选用户、候选组查询任务
    @Test
    public void test2() {
        String taskId = null;
        String userId = null;
        String groupId = null;

        Task task = taskService.newTask(taskId);

        taskService.addCandidateUser(taskId, userId);//ACT_RU_IDENTITYLINK,TYPE_字段类型为：IdentityLinkType.CANDIDATE
        //等同于=》taskService.addUserIdentityLink(taskId,userId,IdentityLinkType.CANDIDATE);

        taskService.addCandidateGroup(taskId, groupId);//ACT_RU_IDENTITYLINK,TYPE_字段类型为：IdentityLinkType.CANDIDATE
        //等同于=》taskService.addGroupIdentityLink(taskId,groupId,IdentityLinkType.CANDIDATE);

        List<Task> list = taskService.createTaskQuery().taskCandidateUser(userId).list();

        List<Task> list1 = taskService.createTaskQuery().taskCandidateGroup(groupId).list();

        List<String> groupIds = null;
        List<Task> list2 = taskService.createTaskQuery().taskCandidateGroupIn(groupIds).list();

        //查询ACT_RU_IDENTITYLINK表，taskId
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(taskId);
        for (IdentityLink identityLink : identityLinksForTask) {
            System.out.println(identityLink.getUserId());
            System.out.println(identityLink.getGroupId());
            System.out.println(identityLink.getType());
            System.out.println(identityLink.getTaskId());
            System.out.println(identityLink.getProcessDefinitionId());
            System.out.println(identityLink.getProcessInstanceId());
        }

        System.out.println(IdentityLinkType.OWNER);
        System.out.println(IdentityLinkType.CANDIDATE);
        System.out.println(IdentityLinkType.ASSIGNEE);
        System.out.println(IdentityLinkType.PARTICIPANT);
        System.out.println(IdentityLinkType.STARTER);
    }


    //3.设置任务持有人;根据任务持有人查询任务
    @Test
    public void test3(){
        String taskId = null;
        String userId = null;
        Task task = taskService.newTask(taskId);

        taskService.setOwner(taskId,userId);//ACT_RU_TASK表的OWNER_字段
        //等同于=》taskService.addUserIdentityLink(taskId,userId,IdentityLinkType.OWNER);

        List<Task> list = taskService.createTaskQuery().taskOwner(userId).list();
    }

    //4.设置任务代理人;根据任务持有人查询任务
    @Test
    public void test4(){
        String taskId = null;
        String userId = null;
        Task task = taskService.newTask(taskId);

        taskService.setAssignee(taskId,userId);//ACT_RU_TASK表的ASSIGNEE_字段
        //等同于=》taskService.addUserIdentityLink(taskId,userId,IdentityLinkType.ASSIGNEE);

        List<Task> list = taskService.createTaskQuery().taskAssignee(userId).list();
    }
}
