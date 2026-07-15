package com.sgi.fiis.grupos_investigacion.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.grupos_investigacion.application.dto.*;
import com.sgi.fiis.grupos_investigacion.application.usecase.*;
import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.presentation.mapper.ResearchGroupMapper;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListResearchLinesByGroupUseCase;
import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.ResearchLineMapper;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.shared.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResearchGroupController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ResearchGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private CreateGroupUseCase createGroupUseCase;
    @MockitoBean private ListGroupsUseCase listGroupsUseCase;
    @MockitoBean private GetGroupUseCase getGroupUseCase;
    @MockitoBean private AssignCoordinatorUseCase assignCoordinatorUseCase;
    @MockitoBean private AssignMemberUseCase assignMemberUseCase;
    @MockitoBean private RemoveMemberUseCase removeMemberUseCase;
    @MockitoBean private ListMembersUseCase listMembersUseCase;
    @MockitoBean private ListResearchLinesByGroupUseCase listResearchLinesByGroupUseCase;
    @MockitoBean private ResearchGroupMapper mapper;
    @MockitoBean private ResearchLineMapper lineMapper;
    @MockitoBean private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    @MockitoBean private com.sgi.fiis.auth.domain.port.TokenProviderPort tokenProviderPort;
    @MockitoBean private com.sgi.fiis.auth.infrastructure.security.CustomUserDetailsService customUserDetailsService;

    @Test
    void create_shouldReturn201_whenDataIsValid() throws Exception {
        ResearchGroupRequestDto request = new ResearchGroupRequestDto("GI-001", "Grupo de IA");
        ResearchGroup domain = ResearchGroup.builder()
                .id(1).groupCode("GI-001").groupName("Grupo de IA").active(true).build();
        ResearchGroupResponseDto response = ResearchGroupResponseDto.builder()
                .id(1).groupCode("GI-001").groupName("Grupo de IA").active(true).build();

        given(mapper.toDomain(any())).willReturn(domain);
        given(createGroupUseCase.execute(any())).willReturn(domain);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/research-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.groupCode").value("GI-001"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void create_shouldReturn400_whenCodeIsEmpty() throws Exception {
        ResearchGroupRequestDto request = new ResearchGroupRequestDto("", "Grupo de IA");

        mockMvc.perform(post("/api/v1/research-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_shouldReturn200_withGroupsList() throws Exception {
        List<ResearchGroup> groups = List.of(
                ResearchGroup.builder().id(1).groupCode("GI-001").groupName("Grupo A").build()
        );
        ResearchGroupResponseDto dto = ResearchGroupResponseDto.builder()
                .id(1).groupCode("GI-001").groupName("Grupo A").build();

        given(listGroupsUseCase.execute()).willReturn(groups);
        given(mapper.toResponseDto(any())).willReturn(dto);

        mockMvc.perform(get("/api/v1/research-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].groupCode").value("GI-001"));
    }

    @Test
    void get_shouldReturn404_whenGroupDoesNotExist() throws Exception {
        given(getGroupUseCase.execute(99))
                .willThrow(new ResourceNotFoundException("ResearchGroup", "id", 99));

        mockMvc.perform(get("/api/v1/research-groups/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignCoordinator_shouldReturn200_whenDataIsValid() throws Exception {
        AssignCoordinatorRequestDto request = new AssignCoordinatorRequestDto(3);
        ResearchGroup updated = ResearchGroup.builder()
                .id(1).currentCoordinatorId(3).active(true).build();
        ResearchGroupResponseDto response = ResearchGroupResponseDto.builder()
                .id(1).currentCoordinatorId(3).active(true).build();

        given(assignCoordinatorUseCase.execute(1, 3)).willReturn(updated);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(patch("/api/v1/research-groups/1/coordinator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentCoordinatorId").value(3));
    }

    @Test
    void assignMember_shouldReturn400_whenViolatesRF21() throws Exception {
        AssignMemberRequestDto request = new AssignMemberRequestDto(5);

        given(assignMemberUseCase.execute(1, 5))
                .willThrow(new BusinessException("The user with id 5 already belongs to an active research group (RF-21)"));

        mockMvc.perform(post("/api/v1/research-groups/1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignMember_shouldReturn201_whenDataIsValid() throws Exception {
        AssignMemberRequestDto request = new AssignMemberRequestDto(2);
        Membership membership = Membership.builder()
                .id(10).groupId(1).userId(2).active(true)
                .startDate(LocalDateTime.now()).build();
        MembershipResponseDto response = MembershipResponseDto.builder()
                .id(10).groupId(1).userId(2).active(true).build();

        given(assignMemberUseCase.execute(1, 2)).willReturn(membership);
        given(mapper.toMembershipResponseDto(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/research-groups/1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groupId").value(1))
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void removeMember_shouldReturn200_whenMemberIsActive() throws Exception {
        Membership removed = Membership.builder()
                .id(10).groupId(1).userId(5).active(false)
                .endDate(LocalDateTime.now()).build();
        MembershipResponseDto response = MembershipResponseDto.builder()
                .id(10).active(false).build();

        given(removeMemberUseCase.execute(1, 5)).willReturn(removed);
        given(mapper.toMembershipResponseDto(any())).willReturn(response);

        mockMvc.perform(delete("/api/v1/research-groups/1/members/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void listLines_shouldReturn200_withGroupLines() throws Exception {
        ResearchGroup group = ResearchGroup.builder().id(1).active(true).build();
        List<ResearchLine> lines = List.of(
                ResearchLine.builder().id(2).lineName("Robótica").build()
        );
        ResearchLineResponseDto lineDto = ResearchLineResponseDto.builder()
                .id(2).lineName("Robótica").active(true).build();

        given(getGroupUseCase.execute(1)).willReturn(group);
        given(listResearchLinesByGroupUseCase.execute(1)).willReturn(lines);
        given(lineMapper.toResponseDto(any())).willReturn(lineDto);

        mockMvc.perform(get("/api/v1/research-groups/1/lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].lineName").value("Robótica"));
    }

    @Test
    void availableUsers_shouldReturn200_withUsersList() throws Exception {
        given(jdbcTemplate.queryForList(anyString())).willReturn(List.of(
                Map.of("id", 1, "firstNames", "Juan", "lastNames", "Perez", "institutionalEmail", "juan@unas.edu.pe")
        ));

        mockMvc.perform(get("/api/v1/research-groups/available-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstNames").value("Juan"));
    }

    @Test
    void coordinatorCandidates_shouldReturn200_withCandidatesList() throws Exception {
        given(jdbcTemplate.queryForList(anyString())).willReturn(List.of(
                Map.of("id", 2, "firstNames", "Maria", "lastNames", "Lopez", "institutionalEmail", "maria@unas.edu.pe")
        ));

        mockMvc.perform(get("/api/v1/research-groups/coordinator-candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].firstNames").value("Maria"));
    }
}
