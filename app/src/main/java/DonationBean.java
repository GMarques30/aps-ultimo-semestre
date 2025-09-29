import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.alves.currency_converter_lib.service.CurrencyConversionServiceImpl;
import org.primefaces.model.file.UploadedFile;

@ManagedBean
@RequestScoped
public class DonationBean implements Serializable {

    private UploadedFile file;
    private HttpClient client;
    
    public DonationBean() {
        client = new HttpClient();
    }
    
    private List<Donation> donations = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    CurrencyConversionServiceImpl converter = new CurrencyConversionServiceImpl();

    public void upload() {
        if (file == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Nenhum arquivo selecionado."));
            return;
        }

        if (file.getSize() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "O arquivo enviado está vazio."));
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String donor = parts[0].trim();
                    try {
                        BigDecimal amount = converter.convertToBRL(parts[1].trim());
                        Donation donation = new Donation(donor, amount.setScale(2, RoundingMode.HALF_EVEN));
                        
                        ResourceBundle bundle = ResourceBundle.getBundle("application");
                        String aggregatorUrl = bundle.getString("AGGREGATOR_URL");
                        
                        client.sendRequest("POST", aggregatorUrl, createJSON(donation));
                    } catch (NumberFormatException ex) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Erro", "Valor inválido para o doador " + donor + ": " + parts[1]));
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Linha inválida ignorada: " + line));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Falha ao processar arquivo: " + e.getMessage()));
        }
    }
    
    private String createJSON(Donation donation) {
        return String.format("{\"donor\":\"%s\",\"amount\":%s}", donation.getDonor(), donation.getAmount());
        
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public static class Donation {
        private String donor;
        private BigDecimal amount;

        public Donation(String donor, BigDecimal amount) {
            this.donor = donor;
            this.amount = amount;
        }

        public String getDonor() {
            return donor;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }
}
