package ru.monitor.Mining.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.monitor.Mining.domain.models.InfoType;
import ru.monitor.Mining.domain.models.MonitoringInfo;

import java.util.List;

@Repository
public interface MonitoringInfoRepository extends PagingAndSortingRepository<MonitoringInfo, Long> {

    @Query("select mi from MonitoringInfo mi where mi.miner.id = :minerId and mi.type = :type order by mi.createdTime desc")
    List<MonitoringInfo> findLastStat(@Param("minerId") Long minerId, @Param("type") InfoType type, Pageable p);
}
//findDistinctFirstByMiner_IdAndTypeOrderByCreatedTimeDesc