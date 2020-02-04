package org.springframework.samples.petclinic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ClinicServiceImplTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private VetRepository vetRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private ClinicServiceImpl serviceImpl;

    @Test
    void findPetTypes() {
        //given
        given(petRepository.findPetTypes()).willReturn(Collections.singletonList(new PetType()));

        //when
        Collection<PetType> petTypes = serviceImpl.findPetTypes();

        //then
        then(petRepository).should().findPetTypes();
        assertThat(petTypes).isNotNull();
        assertThat(petTypes.size()).isEqualTo(1);
    }

    @Test
    void findPetTypes_throwsDataAccessException() {
        //given
        given(petRepository.findPetTypes()).willThrow(new CannotGetJdbcConnectionException("boom", null));

        //when
        Executable serviceCall = () ->
                serviceImpl.findPetTypes();

        //then
        assertThrows(DataAccessException.class, serviceCall);
        then(petRepository).should().findPetTypes();

    }
}