package ru.monitor.Mining.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.monitor.Mining.bot.TelegramBot;
import ru.monitor.Mining.domain.models.InfoType;
import ru.monitor.Mining.domain.models.Miner;
import ru.monitor.Mining.domain.models.MonitoringInfo;
import ru.monitor.Mining.domain.models.Worker;
import ru.monitor.Mining.domain.repository.MiningRepo;
import ru.monitor.Mining.domain.repository.MonitoringInfoRepository;
import ru.monitor.Mining.domain.repository.WorkerRepo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WorkerService {

    @Autowired
    private WorkerRepo workerRepo;

    @Autowired
    private MonitoringInfoRepository monitoringInfoRepository;

    @Autowired
    private MiningRepo miningRepo;

    @Autowired
    private TelegramBot bot;

    @Transactional
    public String printMinerInfo(boolean printParams) {
        StringBuilder text = new StringBuilder();
        for (Worker worker: workerRepo.findAll()) {
            for (Miner miner : worker.getMiners()){
                text.append(miner.printSign()).append(" ").append(worker.getHostName()).append("\n");
                if (printParams)
                    text.append("--параметры запуска майнера: ").append(miner.getParameters()).append("\n");
            }
        }
        return text.toString();
    }

    @Transactional
    public String printStatInfo() {
        StringBuilder text = new StringBuilder();

        for (Worker worker: workerRepo.findAll()) {
            for (Miner miner: worker.getMiners()) {
                List<MonitoringInfo> info = monitoringInfoRepository.findLastStat(miner.getId(), InfoType.STATISTIC, PageRequest.of(0, 1));
                if (info == null || info.isEmpty())
                    continue;
                String[] speeds = info.get(0).getMessage().split(" ");
                double avg = 0.0;
                for (String speed : speeds) {
                    int p = speed.indexOf("MH/s");
                    double d = Double.parseDouble(speed.substring(0, p));
                    avg += d;
                }
                LocalDateTime statTime = info.get(0).getCreatedTime();
                String time = statTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm"));

                text.append(miner.printSign()).append(": ").append("{"+time+"} ").append(String.format("%.2f", avg / speeds.length)).append("MH/s").append("\n");
            }
        }

        return text.toString();
    }

    @Transactional
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkNotActiveWorkers(){
        Iterable<Miner> miners = miningRepo.findAllByActive(true);
        for (Miner miner: miners) {
            long minutes = Duration.between(miner.getLastSelfCheck(), LocalDateTime.now()).toMinutes();
            if (minutes > 5) {
                miner.setActive(false);
                miningRepo.save(miner);
                bot.notifyAll("⛔️" + miner.getWorker().getUserName() + ":" + miner.getMinerName() + " не отвечает уже 5 минут, сделай там ченить");
            }
        }
    }
}
