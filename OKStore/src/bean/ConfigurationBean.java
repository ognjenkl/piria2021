package bean;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;

@Startup
@Singleton
@ApplicationScoped
@FacesConfig(
	// Activates CDI build-in beans
	version = JSF_2_3 
)
public class ConfigurationBean {

}
