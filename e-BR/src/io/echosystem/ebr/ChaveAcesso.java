package io.echosystem.ebr;

import io.echosystem.ebr.constant.ModoEmissao;
import io.echosystem.ebr.constant.TipoDocumento;
import io.echosystem.ebr.constant.UF;
import io.echosystem.ebr.exception.ArgumentoNaoInformado;
import io.echosystem.ebr.exception.CNPJInvalido;
import io.echosystem.ebr.exception.ChaveAcessoInvalida;
import io.echosystem.ebr.exception.NumeroAleatorioDocumentoFiscalInvalido;
import io.echosystem.ebr.exception.NumeroDocumentoFiscalInvalido;
import io.echosystem.ebr.exception.SerieDocumentoFiscalInvalida;
import io.echosystem.ebr.pessoa.CNPJ;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class ChaveAcesso {
    
    public static void main(String[] args) throws ArgumentoNaoInformado{
        Scanner scanner = new Scanner(System.in);
        char operacao = ' ';
        while(true){
            if(operacao == ' '){
                System.out.println("[d] - Para gerar um digito verificador dos 43 primeiros dígitos de uma chave de acesso;\n[v] - Para validar uma chave de acesso com 44 dígitos;\n[ENTER] - Para encerrrar;");
                String line = scanner.nextLine();
                if(line.equalsIgnoreCase("d")){
                    operacao = 'd';
                } else if(line.equalsIgnoreCase("v")){
                    operacao = 'v';
                } else if(line.isEmpty() == false){
                    System.out.println("Operação inválida");
                } else {
                    System.exit(0);
                }
            } else {
                if(operacao == 'd'){
                    System.out.println("Digite os primeiros 43 dígitos da chave de acesso:");
                    String chaveAcesso43Digitos = scanner.nextLine();
                    if(chaveAcesso43Digitos.isEmpty()){
                        System.exit(0);
                    }
                    try {
                        int digitoVerificador = calcularDigitoVerificador(chaveAcesso43Digitos);
                        System.out.println("Dígito verificador [" + digitoVerificador + "]. Chave de acesso completa: " + chaveAcesso43Digitos + digitoVerificador);
                    } catch (ChaveAcessoInvalida ex) {
                        System.out.println("Chave de acesso inválida: " + ex.getMessage());
                    }
                    
                } else if(operacao == 'v') {
                    System.out.println("Digite a chave de acesso com 44 dígitos:");
                    String chaveAcessoCompleta = scanner.nextLine();
                    if(chaveAcessoCompleta.isEmpty()){
                        System.exit(0);
                    }
                    try {
                        ChaveAcesso.decompor(chaveAcessoCompleta);
                        System.out.println("Chave de acesso válida");
                    } catch (ChaveAcessoInvalida ex) {
                        System.out.println("Chave de acesso inválida: " + ex.getMessage());
                    }
                } else {
                    throw new UnsupportedOperationException("Operação inválida");
                }
                System.out.println("\n");
                operacao = ' ';
            }
        }
    }

    private static final int COMPRIMENTO_UF = 2;
    private static final int COMPRIMENTO_AAMM = 4;
    private static final int COMPRIMENTO_CNPJ = 14;
    private static final int COMPRIMENTO_MODELO = 2;
    private static final int COMPRIMENTO_SERIE = 3;
    private static final int COMPRIMENTO_NUMERO_DOCUMENTO = 9;
    private static final int COMPRIMENTO_MODO_EMISSAO = 1;
    private static final int COMPRIMENTO_CODIGO_NUMERICO = 8;
    private static final int COMPRIMENTO_DIGITO_VERIFICADOR = 1;

    private final String cUF;
    private final String aamm;
    private final String cnpj;
    private final String mod;
    private final String serie;
    private final String nDoc;
    private final String tpEmis;
    private final String cAleatorio;
    private final String cDV;

    private ChaveAcesso(String _cUF,
                        String _aamm,
                        String _cnpj,
                        String _mod,
                        String _serie,
                        String _nDoc,
                        String _tpEmis,
                        String _cAleatorio,
                        String _cDV) throws ChaveAcessoInvalida {
        this(_aamm + _cnpj + _mod + _serie + _nDoc + _tpEmis + _cAleatorio + _cDV);
    }
    
    private ChaveAcesso(String numeroChaveAcesso) throws ChaveAcessoInvalida{
        if(numeroChaveAcesso.matches("^\\d{44}$") == false){
            throw new ChaveAcessoInvalida("Chave de acesso não composta por 44 dígitos numéricos: " + numeroChaveAcesso);
        }

        int cursor = 0;
        String _cUF = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_UF);
        String _aamm = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_AAMM);
        String _cnpj = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_CNPJ);
        String _mod = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_MODELO);
        String _serie = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_SERIE);
        String _nDoc = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_NUMERO_DOCUMENTO);
        String _tpEmis = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_MODO_EMISSAO);
        String _cAleatorio = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_CODIGO_NUMERICO);
        String _cDV = numeroChaveAcesso.substring(cursor, cursor += COMPRIMENTO_DIGITO_VERIFICADOR);
        
        try {
            UF.identificarCodigo(Integer.parseInt(_cUF));
        } catch(IllegalArgumentException ex){
            throw new ChaveAcessoInvalida("Código da UF de emissão inválido: " + _cUF);
        }
        
        {
            Calendar d = Calendar.getInstance();
            d.setTime(new Date());
            int anoCorrente = d.get(Calendar.YEAR);
            int mesCorrente = d.get(Calendar.MONTH) + 1;
            int ano = Integer.parseInt(_aamm.substring(0, 2)) + 2000;
            int mes = Integer.parseInt(_aamm.substring(2, 4));
            if(ano > anoCorrente){
                throw new ChaveAcessoInvalida("Ano de emissão posterior ao ano atual");
            }
            if(ano == anoCorrente && mes > mesCorrente){
                throw new ChaveAcessoInvalida("Mês de emissão posterior ao mês atual");
            }
            
            if(mes > 12 || mes < 1){
                throw new ChaveAcessoInvalida("Mês de emissão inválido: " + mes);
            }            
        }
        
        try {
            new CNPJ(_cnpj);
        } catch (CNPJInvalido ex) {
            throw new ChaveAcessoInvalida("CNPJ do emitente inválido");
        }
        
        TipoDocumento tipoDocumento = TipoDocumento.identificarModeloDocumentoFiscal(Integer.parseInt(_mod));
        if(tipoDocumento == null){
            throw new ChaveAcessoInvalida("Modelo do documento fiscal inválido");
        }
        
        ModoEmissao modoEmissao = ModoEmissao.identificarCodigoModoEmissao(Integer.parseInt(_tpEmis));
        if(modoEmissao == null){
            throw new ChaveAcessoInvalida("Modo de emissão inválido");
        }
        
        String numeroChaveAcessoSemDigito = numeroChaveAcesso.substring(0, 43);
        int digitoVerificadorCalculado = calcularDigitoVerificador(numeroChaveAcessoSemDigito);
        int digitoVerificadorOriginal = Integer.parseInt(_cDV);
        
        if(digitoVerificadorCalculado != digitoVerificadorOriginal){
            throw new ChaveAcessoInvalida("Dígito verificador inválido");
        }
        
        this.cUF = _cUF;
        this.aamm = _aamm;
        this.cnpj = _cnpj;
        this.mod = _mod;
        this.serie = _serie;
        this.nDoc = _nDoc;
        this.tpEmis = _tpEmis;
        this.cAleatorio = _cAleatorio;
        this.cDV = _cDV;
    }
    
    public static int calcularDigitoVerificador(String numeroChaveAcessoSemDigito) throws ChaveAcessoInvalida{
        if(numeroChaveAcessoSemDigito.matches("\\d{43}") == false){
            throw new ChaveAcessoInvalida("O cálculo do digito verificador implica na chave de acesso com 43 posições");
        }
        int peso = 2;
        int soma = 0;
        for(int i = 42; i >= 0; i--){
            int d = Character.getNumericValue(numeroChaveAcessoSemDigito.charAt(i));
            soma += peso * d;
            
            peso++;
            if(peso > 9){
                peso = 2;
            }
        }
        int restoDivisao = soma % 11;
        int digitoVerificadorCalculado;
        if (restoDivisao == 0 || restoDivisao == 1) {
            digitoVerificadorCalculado = 0;
        } else {
            digitoVerificadorCalculado = 11 - restoDivisao;
        }
        return digitoVerificadorCalculado;
    }
    
    
    public static ChaveAcesso decompor(String numeroChaveAcesso) throws ChaveAcessoInvalida{
        ChaveAcesso chaveAcesso = new ChaveAcesso(numeroChaveAcesso);
        return chaveAcesso;
    }

    public static ChaveAcesso compor(UF ufEmissao, Date dataEmissao, CNPJ cnpjEmitente, TipoDocumento tipoDocumento, int serie, long numeroDocumento, ModoEmissao modoEmissao, long numeroAleatorio) throws ChaveAcessoInvalida {
        String numeroChaveAcesso = String.valueOf(ufEmissao.getCodigo());
        {
            Calendar c = Calendar.getInstance();
            c.setTime(dataEmissao);
            int ano = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH) + 1;
            String _aamm = String.valueOf(ano).substring(2) + (mes < 10 ? "0" : "") + mes;
            numeroChaveAcesso += _aamm;
        }

        numeroChaveAcesso += cnpjEmitente.getNumeroCNPJ();
        numeroChaveAcesso += tipoDocumento.getCodigoFiscalDocumento();
        {
            if (serie > 999) {
                try {
                    throw new SerieDocumentoFiscalInvalida(serie);
                } catch (SerieDocumentoFiscalInvalida ex) {
                    throw new ChaveAcessoInvalida(ex);
                }
            }
            String _serie = String.valueOf(serie);
            while (_serie.length() < 3) {
                _serie = "0" + _serie;
            }
            numeroChaveAcesso += _serie;
        }

        {
            if (numeroDocumento > 999999999) {
                try {
                    throw new NumeroDocumentoFiscalInvalido(numeroDocumento);
                } catch (NumeroDocumentoFiscalInvalido ex) {
                    throw new ChaveAcessoInvalida(ex);
                }
            }
            String _numeroDocumento = String.valueOf(numeroDocumento);
            while (_numeroDocumento.length() < 9) {
                _numeroDocumento = "0" + _numeroDocumento;
            }
            numeroChaveAcesso += _numeroDocumento;
        }

        numeroChaveAcesso += modoEmissao.getCodigo();

        {
            if (numeroAleatorio > 99999999) {
                try {
                    throw new NumeroAleatorioDocumentoFiscalInvalido(numeroDocumento);
                } catch (NumeroAleatorioDocumentoFiscalInvalido ex) {
                    throw new ChaveAcessoInvalida(ex);
                }
            }
            String _numeroAleatorio = String.valueOf(numeroAleatorio);
            while (_numeroAleatorio.length() < 9) {
                _numeroAleatorio = "0" + _numeroAleatorio;
            }
            numeroChaveAcesso += _numeroAleatorio;
        }

        ChaveAcesso chaveAcesso = new ChaveAcesso(numeroChaveAcesso);
        return chaveAcesso;
    }

}
