package com.campus.admissions.service;

import com.campus.admissions.model.AverageCompetition;
import com.campus.admissions.repository.AverageCompetitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AverageCompetitionServiceTest {

    @Mock
    private AverageCompetitionRepository averageCompetitionRepository;

    @InjectMocks
    private AverageCompetitionService averageCompetitionService;

    @Test
    void getByUserId_shouldReturnAverageCompetition() {
        // Arrange
        Long userId = 100L;

        AverageCompetition averageCompetition = AverageCompetition.builder()
                .idAverage(1)
                .averageBac(9.75f)
                .markDif1(9.50f)
                .markDif2(9.80f)
                .markDif3(10.0f)
                .userId(userId)
                .enabled(1)
                .build();

        when(averageCompetitionRepository.getAverageCompetitionByUserId(userId))
                .thenReturn(averageCompetition);

        // Act
        Optional<AverageCompetition> result =
                averageCompetitionService.getByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdAverage());
        assertEquals(userId, result.get().getUserId());
        assertEquals(9.75f, result.get().getAverageBac());

        verify(averageCompetitionRepository)
                .getAverageCompetitionByUserId(userId);
    }

    @Test
    void getByUserId_shouldThrowExceptionWhenRepositoryReturnsNull() {
        // Arrange
        Long userId = 200L;

        when(averageCompetitionRepository.getAverageCompetitionByUserId(userId))
                .thenReturn(null);

        // Act + Assert
        assertThrows(NullPointerException.class, () ->
                averageCompetitionService.getByUserId(userId));

        verify(averageCompetitionRepository)
                .getAverageCompetitionByUserId(userId);
    }

    @Test
    void findAll_shouldReturnAllAverageCompetitions() {
        // Arrange
        AverageCompetition avg1 = AverageCompetition.builder()
                .idAverage(1)
                .averageBac(9.10f)
                .markDif1(9.0f)
                .markDif2(9.0f)
                .markDif3(9.0f)
                .build();

        AverageCompetition avg2 = AverageCompetition.builder()
                .idAverage(2)
                .averageBac(8.80f)
                .markDif1(8.0f)
                .markDif2(8.0f)
                .markDif3(8.0f)
                .build();

        List<AverageCompetition> expected = List.of(avg1, avg2);

        when(averageCompetitionRepository.findAll())
                .thenReturn(expected);

        // Act
        List<AverageCompetition> result =
                averageCompetitionService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(expected, result);

        verify(averageCompetitionRepository).findAll();
    }

    @Test
    void findOne_shouldReturnAverageCompetitionWhenExists() {
        // Arrange
        Integer id = 1;

        AverageCompetition avg = AverageCompetition.builder()
                .idAverage(id)
                .averageBac(9.40f)
                .markDif1(9.0f)
                .markDif2(9.0f)
                .markDif3(9.0f)
                .build();

        when(averageCompetitionRepository.findById(id))
                .thenReturn(Optional.of(avg));

        // Act
        AverageCompetition result =
                averageCompetitionService.findOne(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getIdAverage());

        verify(averageCompetitionRepository).findById(id);
    }

    @Test
    void findOne_shouldReturnNullWhenNotFound() {
        // Arrange
        Integer id = 99;

        when(averageCompetitionRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act
        AverageCompetition result =
                averageCompetitionService.findOne(id);

        // Assert
        assertNull(result);

        verify(averageCompetitionRepository).findById(id);
    }

    @Test
    void save_shouldReturnSavedAverageCompetition() {
        // Arrange
        AverageCompetition avg = AverageCompetition.builder()
                .averageBac(9.50f)
                .markDif1(9.0f)
                .markDif2(9.0f)
                .markDif3(9.0f)
                .userId(100L)
                .enabled(1)
                .build();

        AverageCompetition savedAvg = AverageCompetition.builder()
                .idAverage(1)
                .averageBac(9.50f)
                .markDif1(9.0f)
                .markDif2(9.0f)
                .markDif3(9.0f)
                .userId(100L)
                .enabled(1)
                .build();

        when(averageCompetitionRepository.save(avg))
                .thenReturn(savedAvg);

        // Act
        AverageCompetition result =
                averageCompetitionService.save(avg);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getIdAverage());
        assertEquals(9.50f, result.getAverageBac());

        verify(averageCompetitionRepository).save(avg);
    }
}
