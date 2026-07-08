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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Map;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest {

    private MockMvc mockMvc;
    private CreateProjectUseCase createProjectUseCase;
    private ObjectMapper objectMapper;
    private String currentRole = "USER";

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
                        doReturn(List.of(new SimpleGrantedAuthority("ROLE_" + currentRole))).when(userDetails).getAuthorities();
                        return userDetails;
                    }
                })
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        currentRole = "USER";
    }

    @Test
    void testCreateProject() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Controller Test");
        request.setSummary("Summary");
        request.setGeneralObjective("Obj");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("100"));
        request.setStartDate(LocalDate.of(2026, Month.JUNE, 17));
        request.setEndDate(LocalDate.of(2026, Month.JUNE, 27));
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
    void testCreateProject_InvalidRequest_Returns400() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        // Missing required fields
        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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
    void testGetProjectsAsDocenteInvestigador() throws Exception {
        currentRole = "DOCENTE_INVESTIGADOR";
        when(createProjectUseCase.getProjectsByResponsible(3L)).thenReturn(Collections.singletonList(ProjectResponse.builder().id(1).build()));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
        
        verify(createProjectUseCase, times(1)).getProjectsByResponsible(3L);
        verify(createProjectUseCase, never()).getAllProjects();
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
        when(createProjectUseCase.getProjectById(1)).thenReturn(ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(3L).build());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("PRJ-1"));
    }
    
    @Test
    void testGetProjectById_ForbiddenForDocente() throws Exception {
        currentRole = "DOCENTE_INVESTIGADOR";
        when(createProjectUseCase.getProjectById(1)).thenReturn(ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(99L).build());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testGetProjectById_AllowedForDocenteIfOwner() throws Exception {
        currentRole = "DOCENTE_INVESTIGADOR";
        when(createProjectUseCase.getProjectById(1)).thenReturn(ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(3L).build());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateProjectStatus() throws Exception {
        ProjectResponse response = ProjectResponse.builder().id(1).status("APROBADO").build();
        when(createProjectUseCase.updateStatus(1, "APROBADO")).thenReturn(response);
        
        Map<String, String> body = Map.of("status", "APROBADO");
        
        mockMvc.perform(patch("/api/v1/projects/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROBADO"));
    }
}
