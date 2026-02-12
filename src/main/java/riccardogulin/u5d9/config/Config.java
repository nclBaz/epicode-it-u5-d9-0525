package riccardogulin.u5d9.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {
	@Bean
	public Cloudinary getImageUploader(@Value("${cloudinary.name}") String cloudName,
	                                   @Value("${cloudinary.apikey}") String apiKey,
	                                   @Value("${cloudinary.secret}") String apiSecret) {
		// VERIFICATE SEMPRE CHE I 3 VALORI VENGANO LETTI CORRETTAMENTE TRAMITE VALUE
		// BASTA UN SYSTEM OUT
		Map<String, String> configuration = new HashMap<>();
		configuration.put("cloud_name", cloudName);
		configuration.put("api_key", apiKey);
		configuration.put("api_secret", apiSecret);
		return new Cloudinary(configuration);
	}
}
