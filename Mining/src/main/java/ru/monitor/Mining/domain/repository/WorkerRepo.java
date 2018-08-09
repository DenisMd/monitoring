package ru.monitor.Mining.domain.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.monitor.Mining.domain.models.Worker;

import java.util.List;

@Repository
public interface WorkerRepo extends PagingAndSortingRepository<Worker, Long> {}
