package com.sgi.fiis.proyectos.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.in.CreateProjectUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest {

    private MockMvc mockMvc;
    private CreateProjectUseCase createProjectUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        createProjectUseCase = Mockito.mock(CreateProjectUseCase.class);
        ProjectController projectController = new ProjectController(createProjectUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getUsername()).thenReturn("testuser");
                        return userDetails;
                    }
                })
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testCreateProject() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Controller Test");
        request.setSummary("Summary");
        request.setGeneralObjective("Obj");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("100"));
        request.setStartDate(LocalDate.of(2026, 6, 17));
        request.setEndDate(LocalDate.of(2026, 6, 27));
        request.setExecutionPlace("Place");
        request.setResearchGroupId(2);
        request.setCallId(1);
        request.setResponsibleId(3);

        ProjectResponse response = ProjectResponse.builder()
                .id(1)
                .code("PRJ-123")
                .title("Controller Test")
                .build();

        when(createProjectUseCase.execute(any(CreateProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("PRJ-123"));
    }

    @Test
    void testGetProjectsNoFilters() throws Exception {
        when(createProjectUseCase.getAllProjects()).thenReturn(Collections.singletonList(ProjectResponse.builder().id(1).build()));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
        
        verify(createProjectUseCase, times(1)).getAllProjects();
    }

    @Test
    void testGetProjectsByResponsible() throws Exception {
        when(createProjectUseCase.getProjectsByResponsible(3L)).thenReturn(Collections.singletonList(ProjectResponse.builder().id(1).build()));

        mockMvc.perform(get("/api/v1/projects?responsibleId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(createProjectUseCase, times(1)).getProjectsByResponsible(3L);
    }

    @Test
    void testGetProjectsByGroup() throws Exception {
        when(createProjectUseCase.getProjectsByGroup(2)).thenReturn(Collections.singletonList(ProjectResponse.builder().id(1).build()));

        mockMvc.perform(get("/api/v1/projects?groupId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(createProjectUseCase, times(1)).getProjectsByGroup(2);
    }

    @Test
    void testGetProjectById() throws Exception {
        when(createProjectUseCase.getProjectById(1)).thenReturn(ProjectResponse.builder().id(1).code("PRJ-1").build());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("PRJ-1"));
    }
}
