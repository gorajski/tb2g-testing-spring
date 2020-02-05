package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)  //Since Mock wired in via XML, we only need this to make the @Captor annotation work
@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class OwnerControllerTest {

    @Autowired
    OwnerController ownerController;

    @Autowired
    ClinicService clinicService;

    private MockMvc mockMvc;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(clinicService);
    }

    @Test
    void processFindForm_returnsFindOwnersView_whenNoOwnerFound() throws Exception {
        mockMvc.perform(get("/owners")
                .param("lastName", "DON'T FIND ME!")
         )  //Adds a parameter to the HTTP call
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void processFindForm_returnsRedirectToSingleOwnerView_whenOneOwnerFound() throws Exception {
        Owner owner = new Owner();
        owner.setId(33);
        given(clinicService.findOwnerByLastName(eq("Smith"))).willReturn(Collections.singletonList(owner));

        mockMvc.perform(get("/owners")
                .param("lastName", "Smith")
        )
                .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/owners/33"));

        then(clinicService).should().findOwnerByLastName(anyString());
    }

    @Test
    void processFindForm_returnsOwnersListView_whenMultipleOwnersFound() throws Exception {
        given(clinicService.findOwnerByLastName(eq(""))).willReturn(Arrays.asList(new Owner(), new Owner()));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("selections"))
                .andExpect(view().name("owners/ownersList"));
    }

    @Test
    void processFindForm_returnsOwnersListView_whenMultipleOwnersFound_hisImplementation() throws Exception {
        given(clinicService.findOwnerByLastName(eq(""))).willReturn(Arrays.asList(new Owner(), new Owner()));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("selections"))
                .andExpect(view().name("owners/ownersList"));

        then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("");
    }

    @Test
    void processFindForm_performsBroadSearch_whenLastNameIsNotProvided() throws Exception {
        processFindForm_returnsOwnersListView_whenMultipleOwnersFound();
    }

    @Test
    void initCreationFormTest() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
}