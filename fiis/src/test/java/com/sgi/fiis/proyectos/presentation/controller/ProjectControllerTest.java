package com.sgi.fiis.proyectos.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.in.CreateProjectUseCase;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest {

    private MockMvc mockMvc;
    private CreateProjectUseCase createProjectUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        createProjectUseCase = mock(CreateProjectUseCase.class);
        ProjectController projectController = new ProjectController(createProjectUseCase, mock(org.springframework.jdbc.core.JdbcTemplate.class));
        MessageSource messageSource = mock(MessageSource.class);
        lenient().when(messageSource.getMessage(anyString(), any(), anyString(), any())).thenAnswer(inv -> inv.getArgument(2));
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
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
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_DOCENTE_INVESTIGADOR"));
                        when(userDetails.getRole()).thenReturn("DOCENTE_INVESTIGADOR");
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

        verify(createProjectUseCase, times(1)).execute(argThat(req ->
                req.getTitle().equals("Controller Test") &&
                req.getResponsibleId() == 3
        ));
    }

    static java.util.stream.Stream<Arguments> projectListEndpoints() {
        return java.util.stream.Stream.of(
                Arguments.of("/api/v1/projects", "Docente defaults to own projects"),
                Arguments.of("/api/v1/projects?responsibleId=3", "Filter by responsibleId"),
                Arguments.of("/api/v1/projects?groupId=2", "Filter by groupId")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("projectListEndpoints")
    void testGetProjectsListEndpoints(String url, String testName) throws Exception {
        when(createProjectUseCase.getProjectsByResponsible(3L)).thenReturn(
                Collections.singletonList(ProjectResponse.builder().id(1).build()));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(createProjectUseCase, times(1)).getProjectsByResponsible(3L);
    }

    @Test
    void testGetProjectById() throws Exception {
        when(createProjectUseCase.getProjectById(1)).thenReturn(ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(3L).build());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("PRJ-1"));

        verify(createProjectUseCase, times(1)).getProjectById(1);
    }

    @Test
    void testGetProjectById_NotFound() throws Exception {
        when(createProjectUseCase.getProjectById(99))
                .thenThrow(new BusinessRuleValidationException("Project not found with ID: 99"));

        mockMvc.perform(get("/api/v1/projects/99"))
                .andExpect(status().isBadRequest());
    }

    // === NUEVOS TESTS PARA COBERTURA COMPLETA ===

    @Test
    void testCreateProject_WrongRole() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_COORDINADOR_GRUPO"));
                        when(userDetails.getRole()).thenReturn("COORDINADOR_GRUPO");
                        return userDetails;
                    }
                }).build();

        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Test");
        request.setSummary("Summary");
        request.setGeneralObjective("Obj");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("100"));
        request.setStartDate(LocalDate.of(2026, Month.JUNE, 17));
        request.setEndDate(LocalDate.of(2026, Month.JUNE, 27));
        request.setExecutionPlace("Place");
        request.setResearchGroupId(2);

        customMockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProjects_CoordinadorGrupo() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForList(anyString(), eq(Integer.class), any())).thenReturn(List.of(5));

        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, jdbcTemplate))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_COORDINADOR_GRUPO"));
                        when(userDetails.getRole()).thenReturn("COORDINADOR_GRUPO");
                        return userDetails;
                    }
                }).build();

        when(createProjectUseCase.getProjectsByGroup(5)).thenReturn(List.of(ProjectResponse.builder().id(1).build()));

        customMockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void testGetProjects_CoordinadorGrupo_NoGroup() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForList(anyString(), eq(Integer.class), any())).thenReturn(List.of());

        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, jdbcTemplate))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_COORDINADOR_GRUPO"));
                        when(userDetails.getRole()).thenReturn("COORDINADOR_GRUPO");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testGetProjects_Estudiante() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_ESTUDIANTE"));
                        when(userDetails.getRole()).thenReturn("ESTUDIANTE");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testGetProjects_ByGroupId() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_DIRECTOR_INVESTIGACION"));
                        when(userDetails.getRole()).thenReturn("DIRECTOR_INVESTIGACION");
                        return userDetails;
                    }
                }).build();

        when(createProjectUseCase.getProjectsByGroup(2)).thenReturn(List.of(ProjectResponse.builder().id(1).build()));

        customMockMvc.perform(get("/api/v1/projects?groupId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(createProjectUseCase).getProjectsByGroup(2);
    }

    @Test
    void testGetProjects_ByResponsibleId() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_DIRECTOR_INVESTIGACION"));
                        when(userDetails.getRole()).thenReturn("DIRECTOR_INVESTIGACION");
                        return userDetails;
                    }
                }).build();

        when(createProjectUseCase.getProjectsByResponsible(5L)).thenReturn(List.of(ProjectResponse.builder().id(2).build()));

        customMockMvc.perform(get("/api/v1/projects?responsibleId=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2));

        verify(createProjectUseCase).getProjectsByResponsible(5L);
    }

    @Test
    void testGetProjects_NoRoleFilters() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_DIRECTOR_INVESTIGACION"));
                        when(userDetails.getRole()).thenReturn("DIRECTOR_INVESTIGACION");
                        return userDetails;
                    }
                }).build();

        when(createProjectUseCase.getAllProjects()).thenReturn(List.of(
                ProjectResponse.builder().id(1).build(),
                ProjectResponse.builder().id(2).build()));

        customMockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(createProjectUseCase).getAllProjects();
    }

    @Test
    void testGetMyDrafts_Docente() throws Exception {
        when(createProjectUseCase.getDraftsByResponsible(3L)).thenReturn(List.of(
                ProjectResponse.builder().id(1).status("BORRADOR").build()));

        mockMvc.perform(get("/api/v1/projects/drafts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("BORRADOR"));

        verify(createProjectUseCase).getDraftsByResponsible(3L);
    }

    @Test
    void testGetMyDrafts_NonDocente() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_ESTUDIANTE"));
                        when(userDetails.getRole()).thenReturn("ESTUDIANTE");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects/drafts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testUpdateProjectStatus() throws Exception {
        when(createProjectUseCase.updateStatus(1, "APROBADO")).thenReturn(
                ProjectResponse.builder().id(1).status("APROBADO").build());

        mockMvc.perform(patch("/api/v1/projects/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"APROBADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROBADO"));

        verify(createProjectUseCase).updateStatus(1, "APROBADO");
    }

    @Test
    void testDeleteDraft_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/1"))
                .andExpect(status().isOk());

        verify(createProjectUseCase).deleteDraft(1, 3L);
    }

    @Test
    void testDeleteDraft_WrongRole() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_ESTUDIANTE"));
                        when(userDetails.getRole()).thenReturn("ESTUDIANTE");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(delete("/api/v1/projects/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetProjectById_Forbidden() throws Exception {
        when(createProjectUseCase.getProjectById(1)).thenReturn(
                ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(99L).build());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isForbidden());

        verify(createProjectUseCase).getProjectById(1);
    }

    @Test
    void testGetProjects_AllFilters() throws Exception {
        when(createProjectUseCase.getProjectsByGroup(2)).thenReturn(List.of(ProjectResponse.builder().id(1).build()));

        mockMvc.perform(get("/api/v1/projects?groupId=2&responsibleId=3"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProject_EstudianteRole() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_ESTUDIANTE"));
                        when(userDetails.getRole()).thenReturn("ESTUDIANTE");
                        return userDetails;
                    }
                }).build();

        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Estudiante Project");
        request.setSummary("Summary");
        request.setGeneralObjective("Obj");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("100"));
        request.setStartDate(LocalDate.of(2026, Month.JUNE, 17));
        request.setEndDate(LocalDate.of(2026, Month.JUNE, 27));
        request.setExecutionPlace("Place");
        request.setResearchGroupId(2);
        request.setCallId(1);

        ProjectResponse response = ProjectResponse.builder()
                .id(1)
                .code("PRJ-123")
                .title("Estudiante Project")
                .build();

        when(createProjectUseCase.execute(any(CreateProjectRequest.class))).thenReturn(response);

        customMockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(createProjectUseCase, times(1)).execute(argThat(req ->
                req.getResponsibleId() == 3
        ));
    }

    @Test
    void testGetMyDrafts_NonDocenteNonEstudiante() throws Exception {
        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_COORDINADOR_GRUPO"));
                        when(userDetails.getRole()).thenReturn("COORDINADOR_GRUPO");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects/drafts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testGetProjectById_Estudiante_OwnProject() throws Exception {
        when(createProjectUseCase.getProjectById(1)).thenReturn(
                ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(3L).build());

        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_ESTUDIANTE"));
                        when(userDetails.getRole()).thenReturn("ESTUDIANTE");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(createProjectUseCase).getProjectById(1);
    }

    @Test
    void testGetProjectById_Estudiante_Forbidden() throws Exception {
        when(createProjectUseCase.getProjectById(1)).thenReturn(
                ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(99L).build());

        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_ESTUDIANTE"));
                        when(userDetails.getRole()).thenReturn("ESTUDIANTE");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isForbidden());

        verify(createProjectUseCase).getProjectById(1);
    }

    @Test
    void testGetProjectById_NonDocenteNonEstudiante_Ok() throws Exception {
        when(createProjectUseCase.getProjectById(1)).thenReturn(
                ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(99L).build());

        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_DIRECTOR_INVESTIGACION"));
                        when(userDetails.getRole()).thenReturn("DIRECTOR_INVESTIGACION");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(createProjectUseCase).getProjectById(1);
    }

    @Test
    void testGetProjectById_ResponsibleIdNull() throws Exception {
        when(createProjectUseCase.getProjectById(1)).thenReturn(
                ProjectResponse.builder().id(1).code("PRJ-1").responsibleId(null).build());

        MockMvc customMockMvc = MockMvcBuilders.standaloneSetup(
                new ProjectController(createProjectUseCase, mock(JdbcTemplate.class)))
                .setControllerAdvice(new GlobalExceptionHandler(mock(MessageSource.class)))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        CustomUserDetails userDetails = mock(CustomUserDetails.class);
                        when(userDetails.getId()).thenReturn(3L);
                        when(userDetails.getAuthorities()).thenReturn(List.of(() -> "ROLE_DOCENTE_INVESTIGADOR"));
                        when(userDetails.getRole()).thenReturn("DOCENTE_INVESTIGADOR");
                        return userDetails;
                    }
                }).build();

        customMockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isForbidden());

        verify(createProjectUseCase).getProjectById(1);
    }
}
