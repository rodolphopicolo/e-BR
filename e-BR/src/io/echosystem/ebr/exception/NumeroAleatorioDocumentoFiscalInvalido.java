package io.echosystem.ebr.exception;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class NumeroAleatorioDocumentoFiscalInvalido extends Exception {
    public NumeroAleatorioDocumentoFiscalInvalido(long numeroAleatorio){
        super("Número aleatório da chave de acesso do documento fiscal inválido: " + numeroAleatorio);
    }
}
