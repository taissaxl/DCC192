package edu.dcc192.ex03;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service; 

@Service
public class Dados {
    HashMap <String,String> m;
    Object obj = null;
    FileReader reader = null;
    JSONParser jsonParser;
    String f = "./src/main/resources/config.json";
    JSONArray JsonConfig;

    public Dados(){
        m = new HashMap<>();
        
        try {
            reader = new FileReader(f);
            jsonParser = new JSONParser();
            obj = jsonParser.parse(reader);
            JsonConfig = (JSONArray) obj;

            for(Object i : JsonConfig){
                JSONObject tmp1 = (JSONObject) i;
                String p1 = (String)tmp1.get("codigo");
                String p2 = (String)tmp1.get("tipo");
                m.put(p1,p2);
            }
        } catch (IOException | ParseException ex) {
            System.out.println(ex.toString());
        }     
    }
     
    public HashMap<String,String> pegaDados(){
        return m;
    }
}
