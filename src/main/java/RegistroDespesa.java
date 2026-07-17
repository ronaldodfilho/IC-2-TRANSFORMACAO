public class RegistroDespesa {

    private final String cnpj;
    private final String razaoSocial;
    private final String trimestre;
    private final String ano;
    private final double valorDespesas;

    public RegistroDespesa(String cnpj, String razaoSocial, String trimestre, String ano, double valorDespesas) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.trimestre = trimestre;
        this.ano = ano;
        this.valorDespesas = valorDespesas;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getTrimestre() {
        return trimestre;
    }

    public String getAno() {
        return ano;
    }

    public double getValorDespesas() {
        return valorDespesas;
    }

    @Override
    public String toString() {
        return "RegistroDespesa{" +
                "cnpj='" + cnpj + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                ", trimestre='" + trimestre + '\'' +
                ", ano='" + ano + '\'' +
                ", valorDespesas=" + valorDespesas +
                '}';
    }
}
