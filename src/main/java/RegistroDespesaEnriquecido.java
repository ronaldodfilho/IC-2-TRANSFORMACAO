public class RegistroDespesaEnriquecido {

    private final RegistroDespesa registroDespesa;
    private final String regAns;
    private final String modalidade;
    private final String uf;

    public RegistroDespesaEnriquecido(RegistroDespesa registroDespesa, String regAns, String modalidade, String uf) {
        this.registroDespesa = registroDespesa;
        this.regAns = regAns;
        this.modalidade = modalidade;
        this.uf = uf;
    }

    public RegistroDespesa getRegistroDespesa() {
        return registroDespesa;
    }

    public String getRegAns() {
        return regAns;
    }

    public String getModalidade() {
        return modalidade;
    }

    public String getUf() {
        return uf;
    }

    @Override
    public String toString() {
        return "RegistroDespesaEnriquecido{" +
                "registroDespesa=" + registroDespesa +
                ", regAns='" + regAns + '\'' +
                ", modalidade='" + modalidade + '\'' +
                ", uf='" + uf + '\'' +
                '}';
    }
}
