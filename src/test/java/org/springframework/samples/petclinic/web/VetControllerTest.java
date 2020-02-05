package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class VetControllerTest {

    @Mock
    ClinicService service;

    @Mock
    Map<String, Object> model;

    @InjectMocks
    VetController controller;

    @Test
    void showVetList() {
        //given
        List<Vet> vetList = Arrays.asList(new Vet(), new Vet());
        given(service.findVets()).willReturn(vetList);

        //when
        String view = controller.showVetList(model);

        //then
        then(service).should().findVets();
        then(model).should().put(anyString(), any());
        assertThat(view).isEqualToIgnoringCase("vets/vetList");
    }

    @Test
    void showResourcesVetList() {
        //given
        List<Vet> vetList = Arrays.asList(new Vet(), new Vet());
        given(service.findVets()).willReturn(vetList);

        //when
        Vets vets = controller.showResourcesVetList();

        //then
        then(service).should().findVets();
        assertThat(vets).isNotNull();
        assertThat(vets.getVetList()).hasSize(2);
    }
}