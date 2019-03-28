package com.evbox.transaction.controller;

import com.evbox.transaction.TransactionApplication;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TransactionApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TransactionControllerTestIT {

    private static final String TRANSACTIONS = "/transactions";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldNotCreateWithEmptyBody() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldNotCreateWithEmptyObject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldNotCreateWithInvalidStationId() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content("{\"stationId\":0}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldCreate() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content("{\"stationId\":1}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is("PROGRESS")));
    }

    @Test
    public void shouldNotStopWithEmptyBody() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(TRANSACTIONS + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldNotStopWithInvalidTransaction() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(TRANSACTIONS + "/1").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content("{\"consumption\":20}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getAllInLastMinute() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.started", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stopped", CoreMatchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", CoreMatchers.is(1)));
    }

    @Test
    public void getStoppedInLastMinute() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS + "/stopped").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void getStartedInLastMinute() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS + "/started").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void deleteStopped() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(TRANSACTIONS))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

}