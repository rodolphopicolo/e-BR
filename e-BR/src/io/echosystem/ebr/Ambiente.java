package io.echosystem.ebr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
@XmlType
@XmlEnum(String.class)
public enum Ambiente {
    @XmlEnumValue("Produção")PRODUCAO
    , @XmlEnumValue("Homologação")HOMOLOGACAO
}
