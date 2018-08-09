package ru.monitor.MiningClient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.monitor.MiningClient.model.InfoType;
import ru.monitor.MiningClient.model.Miner;
import ru.monitor.MiningClient.model.MonitoringInfo;
import ru.monitor.MiningClient.model.Worker;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    public static Process p;

    @Value("${minging.server.url}")
    private String serverUrl;

    @Value("${mining.workerId}")
    private Long workerId;

    @Value("${mining.minerName}")
    private String minerName;

    @Value("${mining.parameters}")
    private String[] parameters;

    @Value("${mining.userName}")
    private String userName;

    @Value("${mining.path}")
    private String path;

    @PostConstruct
    public void init() {
        Miner miner = registerWorker();
        executeMiner(miner);
        accumulate(InfoType.APPLICATION_CLOSE, String.join(" ", "Майнер закрыт"), miner, true);
        System.exit(-1);
    }

    private void executeMiner(Miner miner) {
        BufferedReader input = null;

        try {
            String[] minerCommand = new String[parameters.length+1];
            minerCommand[0] = path;
            System.arraycopy(parameters, 0, minerCommand, 1, parameters.length);

            ProcessBuilder processBuilder = new ProcessBuilder(minerCommand);
            processBuilder.redirectErrorStream(true);
            p = processBuilder.start();

            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            List<String> speed = new ArrayList<>();
            accumulate(InfoType.APPLICATION_START, String.join(" ", "Майнер стартовал"), miner, false);
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                if (speed.size() > 30) {
                    accumulate(InfoType.STATISTIC, String.join(" ", speed), miner, false);
                    speed.clear();
                }
                if (line.contains("MH/s")) {
                    int i = line.indexOf("MH/s");
                    speed.add(line.substring(i-5, i+4).trim());
                }
                if (line.toLowerCase().contains("error")) {
                    accumulate(InfoType.INTERNAL_ERROR, line, miner, false);
                }
            }
        } catch (Exception err) {
            accumulate(InfoType.INTERNAL_ERROR, String.join(" ", err.getMessage()), miner, true);
            err.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void accumulate(InfoType type, String msg, Miner miner, boolean wait) {
        if (miner == null)
            return;
        Thread thread = new Thread(() -> {
            MonitoringInfo monitoringInfo = new MonitoringInfo();
            monitoringInfo.setMiner(miner);
            monitoringInfo.setType(type);
            monitoringInfo.setMessage(msg);

            RestTemplate restTemplate = new RestTemplate();
            String registerURl = serverUrl + "/maining/accumulate";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MonitoringInfo> request = new HttpEntity<>(monitoringInfo, headers);

            try {
                ResponseEntity<MonitoringInfo> response
                        = restTemplate.postForEntity(registerURl, request, MonitoringInfo.class);

                if (!response.getStatusCode().is2xxSuccessful())
                    System.out.println("Не смогли зарегистрировать событие: response code: " + response.getStatusCodeValue());
                else {
                    Miner in = response.getBody().getMiner();
                    miner.setLastSelfCheck(in.getLastSelfCheck());
                    miner.setActive(in.getActive());
                }
            } catch (RestClientException e) {
                System.out.println("Сервер: " + serverUrl + " не отвечает!");
            }
        });
        thread.start();
        if (wait) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Miner registerWorker() {
        RestTemplate restTemplate = new RestTemplate();
        String registerURl = serverUrl + "/maining/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Worker worker = new Worker();
        worker.setId(workerId);

        Miner miner = new Miner();
        miner.setMinerName(minerName);
        miner.setParameters(String.join(" ", parameters));

        worker.getMiners().add(miner);
        worker.setUserName(userName);

        HttpEntity<Worker> request = new HttpEntity<>(worker, headers);

        try {
            ResponseEntity<Worker> response
                    = restTemplate.postForEntity(registerURl, request, Worker.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                for (Miner miner1 : response.getBody().getMiners()) {
                    if (miner1.getMinerName().equals(minerName)) {
                        return miner1;
                    }
                }
            }
            if (response.getStatusCode().is4xxClientError())
                System.out.println("Ид. бота(" + workerId + ") не найдено на сервере");
            else
                System.out.println("Response code: " + response.getStatusCodeValue());
        } catch (RestClientException e) {
            System.out.println("Сервер: " + serverUrl + " не отвечает!");
        }

        return null;
    }

}
