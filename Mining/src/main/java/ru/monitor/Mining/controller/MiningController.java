package ru.monitor.Mining.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(value = "/maining")
public class MiningController {

    @Autowired
    private WorkerRepo workerRepo;

    @Autowired
    private MonitoringInfoRepository monitoringInfoRepository;

    @Autowired
    private MiningRepo miningRepo;

    @Autowired
    private TelegramBot bot;

    @PostMapping(value = "/register")
    public ResponseEntity<Worker> registerWorker(@RequestBody Worker worker, HttpServletRequest request) {

        Optional<Worker> tmp = workerRepo.findById(worker.getId());
        if (!tmp.isPresent() || worker.getMiners() == null || worker.getMiners().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Worker original = tmp.get();
        original.setLastUpdatedTime(LocalDateTime.now());
        original.setUserName(worker.getUserName());
        original.setHostName(request.getRemoteAddr());

        Miner minerFromClient = worker.getMiners().get(0);

        boolean isUpdate = false;
        for (Miner miner: original.getMiners()) {
            if (miner.getMinerName().equals(minerFromClient.getMinerName())) {
                miner.setLastUpdatedTime(LocalDateTime.now());
                miner.setParameters(minerFromClient.getParameters());
                isUpdate = true;
            }
        }

        if (!isUpdate) {
            Miner miner = new Miner();
            miner.setCreatedTime(LocalDateTime.now());
            miner.setLastUpdatedTime(miner.getCreatedTime());
            miner.setMinerName(minerFromClient.getMinerName());
            miner.setWorker(original);
            miner.setParameters(minerFromClient.getParameters());
            original.getMiners().add(miner);
            miningRepo.save(miner);
        }

        workerRepo.save(original);
        return ResponseEntity.ok(original);
    }


    @PostMapping(value = "/accumulate")
    public ResponseEntity<MonitoringInfo> accumulate(@RequestBody MonitoringInfo info) {
        MonitoringInfo newInfo = new MonitoringInfo();
        newInfo.setMiner(info.getMiner());
        newInfo.setMessage(info.getMessage());
        newInfo.setCreatedTime(LocalDateTime.now());
        newInfo.setType(info.getType());
        monitoringInfoRepository.save(newInfo);

        Optional<Miner> tmp = miningRepo.findById(newInfo.getMiner().getId());
        if (!tmp.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Miner miner = tmp.get();
        Worker worker = miner.getWorker();


        if (newInfo.getType() == InfoType.APPLICATION_START) {
            miner.setActive(true);
        } else if (newInfo.getType() == InfoType.APPLICATION_CLOSE) {
            miner.setActive(false);
        } else if (!miner.getActive() && newInfo.getType() == InfoType.STATISTIC) {
            miner.setActive(true);
            bot.notifyAll("✅ " + worker.getUserName() + ":" + newInfo.getMiner().getMinerName() + " воскрес и продолжает копать");
        }

        miner.setLastSelfCheck(LocalDateTime.now());
        miningRepo.save(miner);

        String text = null;
        switch (newInfo.getType()) {
            case APPLICATION_START:
            case APPLICATION_CLOSE:
                text = "✅ " + worker.getUserName() + ":" + newInfo.getMiner().getMinerName() + " " + newInfo.getMessage();
                break;
            case INTERNAL_ERROR:
            case NOT_RESPONSE:
                text = "⛔️" + worker.getUserName() + ":" + newInfo.getMiner().getMinerName() + " " + newInfo.getMessage();
                break;
        }

        bot.notifyAll(text);

        newInfo.setMiner(miner);
        return ResponseEntity.ok(newInfo);
    }

}
