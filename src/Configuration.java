import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Configuration {
	
	JSONParser parser = new JSONParser();
	JSONObject jsonObject = null;

	public Configuration(String path) throws IOException, ParseException
	{    
		Object obj = parser.parse(new FileReader(path));
		jsonObject = (JSONObject) obj;	
	}
		
	String getAttribute(String name)
	{
		return (String)jsonObject.get(name);
	}
	
	
	

}
