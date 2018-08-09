package ru.monitor.Mining.domain.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.monitor.Mining.domain.models.Miner;

import java.util.List;

@Repository
public interface MiningRepo extends PagingAndSortingRepository<Miner, Long> {

    List<Miner> findAllByActive(boolean active);
}
