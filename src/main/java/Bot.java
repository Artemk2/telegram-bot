import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) throws FileNotFoundException {
        //Запускаю тор для обхода блокировки
        System.getProperties().put( "proxySet", "true" );
        System.getProperties().put( "socksProxyHost", "127.0.0.1" );
        System.getProperties().put( "socksProxyPort", "9150" );

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());

        } catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if(message !=null && message.hasText()) {
            switch ( (message.getText())) {
                case "/start":
                    sendMsg(message, "Добро пожаловать!");
                    break;
            }
            startChoice(message);
        }
    }


    public void startChoice(Message message) {
        Model model = new Model();
        //Привественное сообщение
        switch ( (message.getText())) {
            case "/help":
                sendMsg(message, "Чем могу помочь?");
                break;
            case "/settings":
                sendMsg(message, "Что будем настраивать?");
                break;
            default:
                try{
                    //Ответ на сообщение которое отправил клиент, и запускаем метод getWeather
                    String responseMsg = Weather.getWeather(message.getText(), model);
                    sendMsg(message, responseMsg);
                } catch (IOException e) {
                    //отправим сообщение
                    sendMsg(message,"Город не найден!");
                }
        }

    }

    public void setButtons(SendMessage sendMessage){
        //инициализируем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        //Надо связать сообщение с клавиатурой
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        //Параметр, который выводит клавиатуру всем или только определённым пользователям
        replyKeyboardMarkup.setSelective(true);
        //Подгонка клавиатуры под количество кнопок. Сделать больше или меньше
        replyKeyboardMarkup.setResizeKeyboard(true);
        //Скрывать клавиатуру после сообщения
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        //Создаём кнопки
        List<KeyboardRow> keyboardRowList = new ArrayList();
        //Инициализируем первую строчку клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        //keyboardFirstRow.add(new KeyboardButton("/help"));
        //keyboardFirstRow.add(new KeyboardButton("/setting"));
        keyboardFirstRow.add(new KeyboardButton("Москва"));
        keyboardFirstRow.add(new KeyboardButton("Стремилово"));
        keyboardFirstRow.add(new KeyboardButton("Чехов"));

        //Добавляем все строчки клавиатуры в список
        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

      private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        /**установка Id клиента**/
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try{
            setButtons(sendMessage);
            execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }


    public String getBotUsername() {return "name_bot";}

    public String getBotToken() {return "token";}
}
