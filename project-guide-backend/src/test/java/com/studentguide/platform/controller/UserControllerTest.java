// package com.studentguide.platform.controller;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.time.LocalDateTime;
// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.studentguide.platform.dto.UserResponse;
// import com.studentguide.platform.dto.UserUpdateRequest;
// import com.studentguide.platform.service.UserService;

// @WebMvcTest(UserController.class)
// @AutoConfigureMockMvc(addFilters = false)
// class UserControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @MockBean
//     private UserService userService;

//     @Test
//     void getAllUsers_returnsOkAndUserList() throws Exception {
//         UserResponse user = new UserResponse(1L, "Alice Smith", "alice@example.com", "STUDENT", LocalDateTime.now());
//         when(userService.getAllUsers()).thenReturn(List.of(user));

//         mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].id").value(1))
//                 .andExpect(jsonPath("$[0].fullName").value("Alice Smith"))
//                 .andExpect(jsonPath("$[0].email").value("alice@example.com"));

//         verify(userService).getAllUsers();
//     }

//     @Test
//     void getUserById_returnsOkAndUser() throws Exception {
//         UserResponse user = new UserResponse(1L, "Bob Lee", "bob@example.com", "STUDENT", LocalDateTime.now());
//         when(userService.getUserById(1L)).thenReturn(user);

//         mockMvc.perform(get("/api/users/{id}", 1).accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(1))
//                 .andExpect(jsonPath("$.fullName").value("Bob Lee"))
//                 .andExpect(jsonPath("$.email").value("bob@example.com"));

//         verify(userService).getUserById(1L);
//     }

//     @Test
//     void updateUser_returnsOkAndUpdatedUser() throws Exception {
//         UserUpdateRequest request = new UserUpdateRequest();
//         request.setFullName("Bob Lee Updated");

//         UserResponse updatedUser = new UserResponse(1L, "Bob Lee Updated", "bob@example.com", "STUDENT", LocalDateTime.now());
//         when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(updatedUser);

//         mockMvc.perform(put("/api/users/{id}", 1)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(1))
//                 .andExpect(jsonPath("$.fullName").value("Bob Lee Updated"));

//         verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
//     }

//     @Test
//     void deleteUser_returnsNoContent() throws Exception {
//         doNothing().when(userService).deleteUser(1L);

//         mockMvc.perform(delete("/api/users/{id}", 1))
//                 .andExpect(status().isNoContent());

//         verify(userService).deleteUser(1L);
//     }
// }
