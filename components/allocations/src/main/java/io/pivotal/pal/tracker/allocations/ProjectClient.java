package io.pivotal.pal.tracker.allocations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private Map<Long, ProjectInfo> inMemoryProjectInfoMap;
    private final RestOperations restOperations;
    private final String registrationServerEndpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {

        this.inMemoryProjectInfoMap = new ConcurrentHashMap<>();
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo =  restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);

        if(null != projectInfo)
            inMemoryProjectInfoMap.put(projectId, projectInfo);

        return projectInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        return inMemoryProjectInfoMap.get(projectId);
    }
}