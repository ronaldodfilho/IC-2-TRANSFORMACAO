import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Path caminho = Path.of("recursos","consolidado.csv");
        List<RegistroDespesa> registros = ValidadorService.carregarCsv(caminho);

        for (RegistroDespesa registro : registros) {
            System.out.println(registro);
        }
    }
}
