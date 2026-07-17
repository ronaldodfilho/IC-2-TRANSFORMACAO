import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ValidadorService {

    public static List<RegistroDespesa> carregarCsv (Path caminhoArquivo) {

        List<RegistroDespesa> dadosValidados = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo.toFile()))){

            String linha;
            reader.readLine();

            while ((linha = reader.readLine()) != null) {

               String[] colunas = linha.split(";");

                if (colunas.length < 5) continue;

                String cnpj = colunas[0].trim();
                String razaoSocial = colunas[1].trim();
                String trimestre = colunas[2].trim();
                String ano = colunas[3].trim();
                Double valorDespesas = converterParaDouble(colunas[4].trim());

                if(!ValidadorUtils.isCnpjValido(cnpj)) {
                    System.out.println("CNPJ invalido: " + cnpj);
                    continue;
                }

                if (!ValidadorUtils.isRazaoSocialValida(razaoSocial)) {
                    System.out.println("Razão social inválida: " + razaoSocial);
                    continue;
                }

                if (!ValidadorUtils.isValorPositivo(valorDespesas)) {
                    System.out.println("Valor invalido: " + valorDespesas);
                    continue;
                }

                dadosValidados.add( new RegistroDespesa(cnpj,razaoSocial,trimestre,ano,valorDespesas) );
            }
        }
        catch (IOException e){
            throw new RuntimeException("Erro ao ler o aquivo" + caminhoArquivo, e);
        }
        return dadosValidados;
    }

    public static Map<String,OperadoraCadastro> carregarOperadoras(Path caminhoArquivo) {

        Map<String,OperadoraCadastro> operadoras = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo.toFile()))) {

            String linha;
            reader.readLine();

            while((linha = reader.readLine()) != null) {

                String[] colunas = linha.split(";");

                if (colunas.length < 11) continue;

                String regAns = colunas[0].replace("\"", "").trim();
                String cnpj = colunas[1].replace("\"", "").trim();
                String modalidade = colunas[4].replace("\"", "").trim();
                String uf = colunas[10].replace("\"", "").trim();

                operadoras.putIfAbsent(cnpj,new OperadoraCadastro(cnpj,regAns,modalidade,uf));

            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o aquivo da ANS" + caminhoArquivo, e);
        }
        return operadoras;
    }

    public static List<RegistroDespesaEnriquecido> enriquecerDados (List<RegistroDespesa> registroDespesas, Map<String,OperadoraCadastro> operadoras) {

        List<RegistroDespesaEnriquecido> dadosEnriquecidos = new ArrayList<>();

        for (RegistroDespesa registro : registroDespesas) {

            String cnpjLimpo = registro.getCnpj().replaceAll("\\D","");

            OperadoraCadastro cadastro = operadoras.get(cnpjLimpo);

            if (cadastro != null) {
                dadosEnriquecidos.add(new RegistroDespesaEnriquecido(
                        registro,
                        cadastro.getRegAns(),
                        cadastro.getModalidade(),
                        cadastro.getUf()));
            }
            else {
                dadosEnriquecidos.add(new RegistroDespesaEnriquecido(
                        registro,
                        "N/A",
                        "IGNORADO",
                        "IGNORADO"
                ));
            }
        }
        return dadosEnriquecidos;
    }

    private static double converterParaDouble(String texto) {

        if (texto == null || texto.isEmpty()) return 0.0;
        String limpo = texto.trim().replace("\"", "");
        if (limpo.isEmpty() || limpo.equals("-")) return 0.0;

        limpo = limpo.replace(",",".");

        try {
            return Double.parseDouble(limpo);
        }
        catch (NumberFormatException e){
            return 0.0;
        }
    }
}
