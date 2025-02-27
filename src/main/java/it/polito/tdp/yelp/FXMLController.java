/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.time.Year;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Year> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	Business partenza = this.cmbLocale.getValue();
    	Business arrivo = this.model.getLocaleMigliore();
    	double soglia = Double.parseDouble(txtX.getText());
    	//aggiungere controlli
    	
    	List<Business> percorso = this.model.percorsoMigliore(partenza, arrivo, soglia);
    	
    	this.txtResult.appendText("\n\nPercorso migliore: \n" + percorso.toString());
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	String citta = this.cmbCitta.getValue();
    	Year anno = this.cmbAnno.getValue();
    	
    	if(citta == null || anno == null) {
    		this.txtResult.appendText("Parametri obbligatori");
    		return;
    	}
    	
    	String msg = this.model.creaGrafo(citta, anno);
    	this.txtResult.appendText(msg);
    	
    	this.cmbLocale.getItems().clear();
    	this.cmbLocale.getItems().addAll(this.model.getVertici());
    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {
    	Business best = this.model.getLocaleMigliore();
    
    	this.txtResult.appendText("\nLocale migliore: " + best.getBusinessName());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	this.cmbCitta.getItems().addAll(this.model.getAllCities());
    	
    	for(int anno=2005; anno<=2013; anno++) {
    		this.cmbAnno.getItems().add(Year.of(anno));
    	}
    }
}
