package ru.digitalhabbits.homework3.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.digitalhabbits.homework3.exceptions.DepartmentClosedException;
import ru.digitalhabbits.homework3.model.DepartmentRequest;
import ru.digitalhabbits.homework3.model.DepartmentResponse;
import ru.digitalhabbits.homework3.model.DepartmentShortResponse;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.service.DepartmentService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DepartmentController.class)
class DepartmentControllerTest {

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder().create();


    @Test
    void departments() throws Exception {
        List<DepartmentShortResponse> response = List.of(new DepartmentShortResponse().setName("Department").setId(1));
        when(departmentService.findAllDepartments()).thenReturn(response);
        mockMvc.perform(get("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(response.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(response.get(0).getName()));
    }

    @Test
    void department() throws Exception {
        DepartmentResponse response = new DepartmentResponse().setClosed(false)
                .setId(1).setName("Department").setPersons(List.of(new PersonInfo().setFullName("FULL NAME").setId(1)));
        when(departmentService.getDepartment(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.closed").value(response.isClosed()))
                .andExpect(jsonPath("$.persons[0].fullName").value(response.getPersons().get(0).getFullName()))
                .andExpect(jsonPath("$.persons[0].id").value(response.getPersons().get(0).getId()));
    }

    @Test
    void createDepartment() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest().setName("My dep");
        when(departmentService.createDepartment(departmentRequest)).thenReturn(1);
        mockMvc.perform(post("/api/v1/departments")
                .content(gson.toJson(departmentRequest))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/departments/1"));
    }

    @Test
    void updateDepartment() throws Exception {
        DepartmentResponse response = new DepartmentResponse().setClosed(false)
                .setId(1).setName("My dep").setPersons(List.of(new PersonInfo().setFullName("FULL NAME").setId(1)));
        DepartmentRequest departmentRequest = new DepartmentRequest().setName("My dep");
        when(departmentService.updateDepartment(1, departmentRequest)).thenReturn(response);
        mockMvc.perform(patch("/api/v1/departments/1")
                .content(gson.toJson(departmentRequest))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.closed").value(response.isClosed()))
                .andExpect(jsonPath("$.persons[0].fullName").value(response.getPersons().get(0).getFullName()))
                .andExpect(jsonPath("$.persons[0].id").value(response.getPersons().get(0).getId()));

    }

    @Test
    void deleteDepartment() throws Exception {
        mockMvc.perform(delete("/api/v1/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(departmentService, times(1)).deleteDepartment(1);
    }

    @Test
    void addPersonToDepartment() throws Exception {
        mockMvc.perform(post("/api/v1/departments/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(departmentService, times(1)).addPersonToDepartment(1,1);
    }

    @Test
    void removePersonToDepartment() throws Exception {
        mockMvc.perform(delete("/api/v1/departments/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(departmentService, times(1)).removePersonToDepartment(1,1);
    }

    @Test
    void closeDepartment() throws Exception {
        mockMvc.perform(post("/api/v1/departments/1/close")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(departmentService, times(1)).closeDepartment(1);
    }

    @Test
    void errorNotFound() throws Exception {
        when(departmentService.getDepartment(anyInt())).thenThrow(new EntityNotFoundException("not found department"));
        mockMvc.perform(get("/api/v1/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("not found department"));
    }

    @Test
    void errorDepartmentClosed() throws Exception {
        doThrow(new DepartmentClosedException("department was closed")).when(departmentService).addPersonToDepartment(anyInt(), anyInt());
        mockMvc.perform(post("/api/v1/departments/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("department was closed"));
    }

    @Test
    void errorRuntime() throws Exception {
        doThrow(new RuntimeException("Unknown exception")).when(departmentService).findAllDepartments();
        mockMvc.perform(get("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unknown exception"));
    }
}