import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Path caminhoConsolidado = Path.of("recursos","consolidado.csv");
        List<RegistroDespesa> registros = ValidadorService.carregarCsv(caminhoConsolidado);

        Path caminhoCadop = Path.of("recursos","Relatorio_cadop.csv");
        Map<String,OperadoraCadastro> operadoras = ValidadorService.carregarOperadoras(caminhoCadop);

        List<RegistroDespesaEnriquecido> dadosEnriquecidos = ValidadorService.enriquecerDados(registros,operadoras);

        for (RegistroDespesaEnriquecido dado : dadosEnriquecidos) {
            System.out.println(dado);
        }
    }
}
