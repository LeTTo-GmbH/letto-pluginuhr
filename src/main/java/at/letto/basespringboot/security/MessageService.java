package at.letto.basespringboot.security;

import at.letto.basespringboot.config.BaseMicroServiceConfiguration;
import at.letto.databaseclient.service.BaseLettoRedisDBService;
import at.letto.login.dto.message.MessageDto;
import at.letto.tools.Datum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Einfache Messages von Service zu Service
 */
@Service
public class MessageService {

    @Autowired
    private BaseLettoRedisDBService baseLettoRedisDBService;

    private BaseMicroServiceConfiguration microServiceConfiguration;

    public void init(BaseMicroServiceConfiguration mc) {
        microServiceConfiguration = mc;
    }

    /** erzeugt eine neue Message-ID */
    private String newMessageID() {
        String messageID;
        int i=0;
        do {
            messageID = UUID.randomUUID().toString();
            if (i++>10000) {
                // Wenn die Schleife zu lange dauert dann wird eine Exception geworfen
                throw new RuntimeException("Cannot create new messageID");
            }
        } while (baseLettoRedisDBService.hasKey(BaseLettoRedisDBService.REDIS_DATABASE_LOGIN, "message:"+messageID));
        return messageID;
    }

    /**
     * Generiert eine Nachricht an ein Service welche in der REDIS-Datenbank gespeichert wird<br>
     * @param sender     Kennung des Senders
     * @param receiver   Kennung des Empfängers
     * @param topic      Thema der Nachricht
     * @param message    Nachricht als Objekt welches als JSON gespeichert wird!!
     * @param lifetimeSeconds   Lebensdauer der Nachricht in Sekunden bis sie gelöscht wird
     * @param single     true wenn die Nachricht nur einmal abgeholt werden kann und dann sofort gelöscht wird
     * @return           Kennung der Nachricht als String welcher auch als get-Parameter verwendet werden kann
     */
    public String createMessage(String sender, String receiver, String topic, Object message, long lifetimeSeconds, boolean single) {
        String messageID = newMessageID();
        MessageDto messageDto = new MessageDto(
                messageID,
                Datum.nowDateInteger(),
                sender,
                receiver,
                topic,
                message.getClass().getName(),
                message,
                single
        );
        if (baseLettoRedisDBService.putSeconds(BaseLettoRedisDBService.REDIS_DATABASE_LOGIN, "message:"+messageID, messageDto, lifetimeSeconds))
            return messageID;
        else
            return null;
    }

    /** Lädt eine Nachricht aus der REDIS-Datenbank und löscht falls sie single ist sofort <br>
     * @param messageID   Kennung der Nachricht
     * @return            Nachricht als Object oder null wenn die Nachricht nicht existiert
     * */
    public Object loadMessage(String messageID) {
        MessageDto messageDto = loadMessageDto(messageID);
        if (messageDto!=null) return messageDto.getMessage();
        return null;
    }

    /** Lädt eine Nachricht aus der REDIS-Datenbank und löscht falls sie single ist sofort <br>
     * @param messageID   Kennung der Nachricht
     * @return            Nachricht als MessageDto oder null wenn die Nachricht nicht existiert
     * */
    public MessageDto loadMessageDto(String messageID) {
        MessageDto messageDto = baseLettoRedisDBService.get(BaseLettoRedisDBService.REDIS_DATABASE_LOGIN, "message:"+messageID, MessageDto.class);
        if (messageDto!=null) {
            if (messageDto.isSingle()) {
                // Lösche die Nachricht sofort
                baseLettoRedisDBService.deleteKey(BaseLettoRedisDBService.REDIS_DATABASE_LOGIN, "message:"+messageID);
            }
            return messageDto;
        }
        return null;
    }

    /**
     * Lädt alle Nachrichten aus der REDIS-Datenbank
     * @return          Liste von Nachrichten als MessageDto
     */
    public Map<String,MessageDto> loadMessages() {
        try {
            Map<String, MessageDto> messages = baseLettoRedisDBService.loadKeysWithPrefix(MessageDto.class, "message:");
            return messages;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lädt alle Nachrichten für einen Empfänger aus der REDIS-Datenbank
     * @param receiver  Kennung des Empfängers
     * @return          Liste von Nachrichten als MessageDto
     */
    public List<MessageDto> loadMessagesReceiver(String receiver) {
        Map<String, MessageDto> messagesMap = loadMessages();
        List<MessageDto> messages = new ArrayList<>();
        for (String messageID: messagesMap.keySet()) {
            MessageDto messageDto = messagesMap.get(messageID);
            if (messageDto.getReceiver().equals(receiver)) {
                messages.add(messageDto);
            }
        }
        return messages;
    }

    /**
     * Lädt alle Nachrichten eines Senders aus der REDIS-Datenbank
     * @param sender    Kennung des Senders
     * @return          Liste von Nachrichten als MessageDto
     */
    public List<MessageDto> loadMessagesSender(String sender) {
        Map<String, MessageDto> messagesMap = loadMessages();
        List<MessageDto> messages = new ArrayList<>();
        for (String messageID: messagesMap.keySet()) {
            MessageDto messageDto = messagesMap.get(messageID);
            if (messageDto.getSender().equals(sender)) {
                messages.add(messageDto);
            }
        }
        return messages;
    }

    /**
     * Lädt alle Nachrichten eines bestimmten topic aus der REDIS-Datenbank
     * @param topic     Thema der Nachricht
     * @return          Liste von Nachrichten als MessageDto
     */
    public List<MessageDto> loadMessagesTopic(String topic) {
        Map<String, MessageDto> messagesMap = loadMessages();
        List<MessageDto> messages = new ArrayList<>();
        for (String messageID: messagesMap.keySet()) {
            MessageDto messageDto = messagesMap.get(messageID);
            if (messageDto.getTopic().equals(topic)) {
                messages.add(messageDto);
            }
        }
        return messages;
    }

    /**
     * Lädt alle aktiven Nachrichten in eine Liste
     * @return          Liste von Nachrichten als MessageDto
     */
    public List<MessageDto> loadMessagesList() {
        Map<String, MessageDto> messagesMap = loadMessages();
        List<MessageDto> messages = new ArrayList<>();
        for (String messageID: messagesMap.keySet()) {
            MessageDto messageDto = messagesMap.get(messageID);
            messages.add(messageDto);
        }
        return messages;
    }

    /**
     * Löscht eine Nachricht aus der REDIS-Datenbank
     * @param messageID   Kennung der Nachricht
     * @return            true wenn die Nachricht gelöscht wurde
     */
    public boolean deleteMessage(String messageID) {
        return baseLettoRedisDBService.deleteKey(BaseLettoRedisDBService.REDIS_DATABASE_LOGIN, "message:" + messageID);
    }

}
