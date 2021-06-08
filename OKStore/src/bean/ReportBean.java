package bean;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

import dao.ReportDAO;

@Named
@ViewScoped
public class ReportBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public LineChartModel getBuyersLine() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		Map<String, Double> map = ReportDAO.getSumForBuyers();

		LineChartModel lineChartModel = new LineChartModel();
		LineChartSeries lineChartSeries = new LineChartSeries();
		lineChartSeries.setFill(true);
		lineChartSeries.setLabel(msgResourceBundle.getString("pageStatisticsBuyersSeriesLabel"));

		for (Map.Entry<String, Double> entry : map.entrySet())
			lineChartSeries.set(entry.getKey(), entry.getValue());

		lineChartModel.addSeries(lineChartSeries);
		lineChartModel.setTitle(msgResourceBundle.getString("pageStatisticsBuyersChartTitle"));
		lineChartModel.setLegendPosition("ne");
		lineChartModel.setStacked(true);
		lineChartModel.setShowPointLabels(true);
		Axis xAxis = new CategoryAxis(msgResourceBundle.getString("pageStatisticsBuyersChartX"));
		lineChartModel.getAxes().put(AxisType.X, xAxis);
		Axis yAxis = lineChartModel.getAxis(AxisType.Y);
		yAxis.setLabel(msgResourceBundle.getString("pageStatisticsBuyersChartY"));
		yAxis.setMin(0);
		//	yAxis.setMax(300);  

		return lineChartModel;
	}
	
	public PieChartModel getBuyersPie() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		Map<String, Double> map = ReportDAO.getSumForBuyers();
		PieChartModel pieChartModel = new PieChartModel(); 
		pieChartModel.setTitle(msgResourceBundle.getString("pageStatisticsBuyersChartTitlePie"));
		pieChartModel.setLegendPosition("c");
		for (Map.Entry<String, Double> entry : map.entrySet())
			pieChartModel.set(entry.getKey(), entry.getValue());
		
		return pieChartModel;
	}
	
	public LineChartModel getSellersLine() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		Map<String, Double> map = ReportDAO.getSumForSellers();

		LineChartModel lineChartModel = new LineChartModel();
		LineChartSeries lineChartSeries = new LineChartSeries();
		lineChartSeries.setFill(true);
		lineChartSeries.setLabel(msgResourceBundle.getString("pageStatisticsSellersSeriesLabel"));

		for (Map.Entry<String, Double> entry : map.entrySet())
			lineChartSeries.set(entry.getKey(), entry.getValue());

		lineChartModel.addSeries(lineChartSeries);
		lineChartModel.setTitle(msgResourceBundle.getString("pageStatisticsSellersChartTitle"));
		lineChartModel.setLegendPosition("ne");
		lineChartModel.setStacked(true);
		lineChartModel.setShowPointLabels(true);
		Axis xAxis = new CategoryAxis(msgResourceBundle.getString("pageStatisticsSellersChartX"));
		lineChartModel.getAxes().put(AxisType.X, xAxis);
		Axis yAxis = lineChartModel.getAxis(AxisType.Y);
		yAxis.setLabel(msgResourceBundle.getString("pageStatisticsSellersChartY"));
		yAxis.setMin(0);
		//	yAxis.setMax(300);  

		return lineChartModel;
	}

	public PieChartModel getSellersPie() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		Map<String, Double> map = ReportDAO.getSumForSellers();
		PieChartModel pieChartModel = new PieChartModel(); 
		pieChartModel.setTitle(msgResourceBundle.getString("pageStatisticsSellersChartTitlePie"));
		pieChartModel.setLegendPosition("c");
		for (Map.Entry<String, Double> entry : map.entrySet())
			pieChartModel.set(entry.getKey(), entry.getValue());
		
		return pieChartModel;
	}

}
