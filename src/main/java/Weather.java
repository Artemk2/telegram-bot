import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Weather {

    //Класс, который обрабатывает погоду
    public static String getWeather(String message, Model model) throws IOException {
        String appid = System.getenv().get("OPENWEATHERMAP_APPID");

        //Делаем URL запрос
       URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=" + appid);

       //Надо прочитать содержимое ответа
        Scanner in = new Scanner((InputStream) url.getContent());
        //Переменная, в которую будем помещать результат
        String result = "";
        //Условие. Мы будем считывать тогда, и только тогда, когда есть чего считывать
        while(in.hasNext()){
            //Метод позволяет читать целую последовательность символов
            result += in.nextLine();
        }

        //Кастим JSON объект в строку
        JSONObject object = new JSONObject(result);
        //Присвоить значение названия города
        model.setName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        //Получаем температуру
        model.setTemp(main.getDouble("temp"));
        //Получаем влажность
        model.setHumidity(main.getDouble("humidity"));

        //Из джейсона создаём массив
        JSONArray getArray = object.getJSONArray("weather");
        for(int i=0;i< getArray.length(); i++){
            //В массив запушутся все элементы weather
            JSONObject objectFor = getArray.getJSONObject(i);
            //Получение иконки
            model.setIcon((String) objectFor.get("icon"));
            //Краткое описание
            model.setMain((String) objectFor.get("main"));
        }
        return "Город: " + model.getName() + "\n" +
                "Температура: " + model.getTemp() + "С" + "\n" +
                "Влажность: " + model.getHumidity() + ", %" + "\n" +
                "Описание: " + model.getMain() + "\n" +
                //Изображение
                "http://openweathermap.org/img/wn/" + model.getIcon() + ".png";
    }
}
