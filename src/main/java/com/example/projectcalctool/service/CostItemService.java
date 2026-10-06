package com.example.projectcalctool.service;

import com.example.projectcalctool.model.CostItem;
import com.example.projectcalctool.repository.CostItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostItemService {

    private final CostItemRepository costItemRepository;

    public CostItemService(CostItemRepository costItemRepository) {
        this.costItemRepository = costItemRepository;
    }

    public List<CostItem> getByProjectId(int projectId) {
        return costItemRepository.findByProjectId(projectId);
    }

    public void create(CostItem item) {
        costItemRepository.create(item);
    }

    public CostItem getById(int id) {
        return costItemRepository.findById(id);
    }

    public void update(CostItem item) {
        costItemRepository.update(item);
    }

    public void delete(int id) {
        costItemRepository.delete(id);
    }
}
