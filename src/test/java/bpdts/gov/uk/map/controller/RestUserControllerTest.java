package bpdts.gov.uk.map.controller;

import bpdts.gov.uk.map.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test the User rest controller
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RestUserControllerTest {

    @SpyBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void invokeGetAllCharacterID_ResponseWithCharacterWithAllIds() throws Exception {
        mockMvc.perform(get("/api/v1/londoners")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
