package ru.monitor.Mining.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.monitor.Mining.domain.models.Worker;
import ru.monitor.Mining.domain.repository.WorkerRepo;
import ru.monitor.Mining.domain.service.WorkerService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Autowired
    private WorkerRepo workerRepo;

    @Autowired
    private WorkerService workerService;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        if (msg != null && msg.hasText()) {

            logger.info(msg.getChatId() + ": " + msg.getText());

            String[] commandWithParams = msg.getText().split(" ");

            switch (commandWithParams[0]) {
                case "/start":
                    execStart(msg);
                    break;
                case "/miners":
                    execMiners(msg, commandWithParams);
                    break;
                case "/stat":
                    execStat(msg);
                    break;
            }
        }
    }

    private void execStat(Message msg) {
        String text = workerService.printStatInfo();
        if (text.length() != 0)
            sendReply(msg, text);
        else
            sendReply(msg, "Статистика еще не собиралась");
    }

    private void execMiners(Message msg, String[] commandWithParams) {
        List<String> params = Arrays.asList(commandWithParams);
        String text = workerService.printMinerInfo(params.contains("params"));
        if (text.length() != 0)
            sendReply(msg, text);
        else
            sendReply(msg, "Майнеры не зарегистрированны");
    }

    private void execStart(Message msg) {
        Worker worker = registerWorker(msg.getChatId());
        sendReply(msg, "Зарегистрирован майнер: " + worker.getId());
    }

    private Worker registerWorker(Long chatId) {
        Worker worker = new Worker();
        worker.setCreatedTime(LocalDateTime.now());
        worker.setHostName("");
        worker.setLastUpdatedTime(worker.getCreatedTime());
        worker.setTelegramChatId(chatId);
        worker.setUserName("none");
        workerRepo.save(worker);
        return worker;
    }

    private void sendReply(Message source, String text) {
        SendMessage msg = new SendMessage();
        msg.enableMarkdown(true);
        msg.setChatId(source.getChatId());
        msg.setReplyToMessageId(source.getMessageId());
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void notifyAll(String text) {
        if (text == null || text.isEmpty())
            return;

        for (Worker worker: workerRepo.findAll()) {
            SendMessage msg = new SendMessage();
            msg.enableMarkdown(true);
            msg.setChatId(worker.getTelegramChatId());
            msg.setText(text);
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
