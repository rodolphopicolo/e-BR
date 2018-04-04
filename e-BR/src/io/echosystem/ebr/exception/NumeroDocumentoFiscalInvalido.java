package io.echosystem.ebr.exception;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class NumeroDocumentoFiscalInvalido extends Exception {
    public NumeroDocumentoFiscalInvalido(long numeroDocumentoFiscal){
        super("Número do documento fiscal inválido: " + numeroDocumentoFiscal);
    }
}
