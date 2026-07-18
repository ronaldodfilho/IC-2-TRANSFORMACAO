import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) throws IOException {

        Path caminhoConsolidado = Path.of("recursos","consolidado.csv");
        List<RegistroDespesa> registros = ValidadorService.carregarCsv(caminhoConsolidado);

        Path caminhoCadop = Path.of("recursos","Relatorio_cadop.csv");
        Map<String,OperadoraCadastro> operadoras = ValidadorService.carregarOperadoras(caminhoCadop);

        List<RegistroDespesaEnriquecido> dadosEnriquecidos = ValidadorService.enriquecerDados(registros,operadoras);

        Path caminhoSaida = Path.of("downloads","despesas_agregadas.csv");
        ValidadorService.gerarDespesasAgregadas(dadosEnriquecidos, caminhoSaida);

        Path caminhoZipFinal = Path.of("Teste_Ronaldo_Dutra_Filho.zip");
        compactarParaZip(caminhoSaida,caminhoZipFinal);

    }
    private static void compactarParaZip(Path caminhoCsv, Path caminhoZip) throws IOException {
        try (ZipOutputStream zipOutput = new ZipOutputStream(Files.newOutputStream(caminhoZip))) {
            ZipEntry entradaZip = new ZipEntry(caminhoCsv.getFileName().toString());
            zipOutput.putNextEntry(entradaZip);
            Files.copy(caminhoCsv, zipOutput);
            zipOutput.closeEntry();
            System.out.println("Arquivo ZIP gerado em: " + caminhoZip.toAbsolutePath());
        }
    }
}
