public class DespesaAgregada {

    private final String razaoSocial;
    private final String registroAns;
    private final String modalidade;
    private final String uf;
    private final double totalDespesas;
    private final double mediaPorTrimestre;
    private final double desvioPadrao;

    public DespesaAgregada(
            String razaoSocial,
            String registroAns,
            String modalidade,
            String uf,
            double totalDespesas,
            double mediaPorTrimestre,
            double desvioPadrao
    ) {
        this.razaoSocial = razaoSocial;
        this.registroAns = registroAns;
        this.modalidade = modalidade;
        this.uf = uf;
        this.totalDespesas = totalDespesas;
        this.mediaPorTrimestre = mediaPorTrimestre;
        this.desvioPadrao = desvioPadrao;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getUf() {
        return uf;
    }

    public String getRegistroAns() {
        return registroAns;
    }

    public String getModalidade() {
        return modalidade;
    }

    public double getTotalDespesas() {
        return totalDespesas;
    }

    public double getMediaPorTrimestre() {
        return mediaPorTrimestre;
    }

    public double getDesvioPadrao() {
        return desvioPadrao;
    }
}
