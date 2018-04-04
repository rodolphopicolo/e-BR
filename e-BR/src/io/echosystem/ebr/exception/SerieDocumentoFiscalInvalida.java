package io.echosystem.ebr.exception;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class SerieDocumentoFiscalInvalida extends Exception {
    public SerieDocumentoFiscalInvalida(int serie){
        super("Série do documento fiscal inválida: " + serie);
    }
}
