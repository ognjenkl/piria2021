package bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dao.AdvertiseDAO;
import model.Advertise;

@Named
@ViewScoped
public class AdvertisingBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject 
	@ManagedProperty (value = "#{userBean}")
	UserBean userBean;
	
	Integer frequency;
	Date targetDateFrom;
	Date targetDateTo;

	Properties prop;
	
	@PostConstruct
	public void init() {
		prop = new Properties();
		try {
			prop.load(FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/WEB-INF/config/config.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String createAdvertisement() {
		BigDecimal advertisePrice = new BigDecimal(prop.getProperty("advertise.price"));
		System.out.println("advert price: " + advertisePrice);
		
		Advertise advertise = new Advertise();
		advertise.setUserAdvertiserId(userBean.getUser().getId());
		advertise.setFrequency(frequency);
		advertise.setPrice(advertisePrice);
		advertise.setStartDate(new Timestamp(new java.util.Date().getTime()));
		advertise.setTargetDateFrom(targetDateFrom);
		advertise.setTargetDateTo(targetDateTo);
		
		return AdvertiseDAO.insert(advertise) > 0 ? "sellingArticles?faces-redirect=true" : null;
		
	}
	
	
	
	
	
	public Integer getFrequency() {
		return frequency;
	}
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	public Date getTargetDateFrom() {
		return targetDateFrom;
	}
	public void setTargetDateFrom(Date targetDateFrom) {
		this.targetDateFrom = targetDateFrom;
	}
	public Date getTargetDateTo() {
		return targetDateTo;
	}
	public void setTargetDateTo(Date targetDateTo) {
		this.targetDateTo = targetDateTo;
	}
	
	
}
