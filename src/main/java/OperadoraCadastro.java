public class OperadoraCadastro {

    private final String cnpj;
    private final String regAns;
    private final String modalidade;
    private final String uf;

    public OperadoraCadastro(String cnpj, String regAns, String modalidade, String uf) {
        this.cnpj = cnpj;
        this.regAns = regAns;
        this.modalidade = modalidade;
        this.uf = uf;
    }

    public String getCnpj() {
        return cnpj;
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
}
