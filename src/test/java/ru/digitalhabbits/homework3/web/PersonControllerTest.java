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
import ru.digitalhabbits.homework3.model.*;
import ru.digitalhabbits.homework3.service.PersonService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers =  PersonController.class)
class PersonControllerTest {

    @MockBean
    private PersonService personService;

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder().create();

    @Test
    void persons() throws Exception {
        List<PersonResponse> response = List.of(new PersonResponse().setAge(30).setId(1).setFullName("Full Name")
        .setDepartment(new DepartmentInfo().setId(1).setName("My dep")));
        when(personService.findAllPersons()).thenReturn(response);
        mockMvc.perform(get("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(response.get(0).getId()))
                .andExpect(jsonPath("$[0].age").value(response.get(0).getAge()))
                .andExpect(jsonPath("$[0].fullName").value(response.get(0).getFullName()))
                .andExpect(jsonPath("$[0].department.name").value(response.get(0).getDepartment().getName()))
                .andExpect(jsonPath("$[0].department.id").value(response.get(0).getDepartment().getId()));
    }

    @Test
    void person() throws Exception {
        PersonResponse response = new PersonResponse().setAge(30).setId(1).setFullName("Full Name")
                .setDepartment(new DepartmentInfo().setId(1).setName("My dep"));
        when(personService.getPerson(1)).thenReturn(response);
        mockMvc.perform(get("/api/v1/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andExpect(jsonPath("$.fullName").value(response.getFullName()))
                .andExpect(jsonPath("$.department.name").value(response.getDepartment().getName()))
                .andExpect(jsonPath("$.department.id").value(response.getDepartment().getId()));
    }

    @Test
    void createPerson() throws Exception {
        PersonRequest request = new PersonRequest().setAge(10).setFirstName("First").setLastName("Last").setMiddleName("Middle");
        when(personService.createPerson(request)).thenReturn(1);
        mockMvc.perform(post("/api/v1/persons")
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/persons/1"));
    }

    @Test
    void updatePerson() throws Exception {
        PersonResponse response = new PersonResponse().setAge(30).setId(1).setFullName("Full Name")
                .setDepartment(new DepartmentInfo().setId(1).setName("My dep"));
        PersonRequest request = new PersonRequest().setAge(10).setFirstName("First").setLastName("Last").setMiddleName("Middle");
        when(personService.updatePerson(1, request)).thenReturn(response);
        mockMvc.perform(patch("/api/v1/persons/1")
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andExpect(jsonPath("$.fullName").value(response.getFullName()))
                .andExpect(jsonPath("$.department.name").value(response.getDepartment().getName()))
                .andExpect(jsonPath("$.department.id").value(response.getDepartment().getId()));
    }

    @Test
    void deletePerson() throws Exception {
        mockMvc.perform(delete("/api/v1/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(personService, times(1)).deletePerson(1);
    }


}