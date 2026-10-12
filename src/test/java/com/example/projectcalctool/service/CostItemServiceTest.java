package com.example.projectcalctool.service;

import com.example.projectcalctool.model.CostItem;
import com.example.projectcalctool.repository.CostItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CostItemServiceTest {

    @Mock
    private CostItemRepository costItemRepository;

    @InjectMocks
    private CostItemService costItemService;

    @Test
    @DisplayName("getByProjectId delegates to repository")
    void getByProjectId_delegates() {
        // Given
        List<CostItem> items = List.of(new CostItem(), new CostItem());
        when(costItemRepository.findByProjectId(5)).thenReturn(items);

        // When / Then
        assertEquals(2, costItemService.getByProjectId(5).size());
    }

    @Test
    @DisplayName("getById delegates to repository")
    void getById_delegates() {
        // Given
        CostItem item = new CostItem();
        when(costItemRepository.findById(7)).thenReturn(item);

        // When / Then
        assertSame(item, costItemService.getById(7));
    }

    @Test
    @DisplayName("create / update / delete delegate to repository")
    void writes_delegate() {
        // Given
        CostItem item = new CostItem();

        // When
        costItemService.create(item);
        costItemService.update(item);
        costItemService.delete(3);

        // Then
        verify(costItemRepository).create(item);
        verify(costItemRepository).update(item);
        verify(costItemRepository).delete(3);
    }
}
