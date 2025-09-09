import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.file.UploadedFile;

@ManagedBean
@RequestScoped
public class DonationBean implements Serializable {

    private UploadedFile file;
    private List<Donation> donations = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            donations.clear();
            total = BigDecimal.ZERO;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String donor = parts[0].trim();
                    try {
                        BigDecimal amount = new BigDecimal(parts[1].trim()).setScale(2);
                        Donation donation = new Donation(donor, amount);
                        donations.add(donation);
                        print(donation);
                    } catch (NumberFormatException ex) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Erro", "Valor inválido para o doador " + donor + ": " + parts[1]));
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Linha inválida ignorada: " + line));
                }
            }

            System.out.println("Total processado: " + total);

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Falha ao processar arquivo: " + e.getMessage()));
        }
    }

    private void print(Donation donation) {
        System.out.println("Processando doador: " + donation.getDonor() + " | Valor: R$ " + donation.getAmount());
        total = total.add(donation.getAmount());
    }

    public List<Donation> getDonations() {
        return donations;
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
