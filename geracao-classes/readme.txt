A partir deste diretório são criadas as classes JAVA que representam as estruturas XML dos documentos eletrônicos definidos através de seus respectivos schemas XSD.

	Conteúdo
		doc
			Documentação das versões dos documentos eletrônicos.

		generated-classes.sh
			Bash script que gera as classes JAVA via JAXB com base nos schemas fornecidos.

		generated-classes
			Diretório utilizado com destino para as classes criadas através do script generated-classes.sh.

		schemas
			Esquemas XSD fornecidos como documentação.

Geração de classes através de schemas XSD

	Toda a troca de informações com secretarias da fazenda é feita através de web services SOAP XML. As secretarias da fazenda disponibilizam todos os esquemas que descrevem os XML utilizados nos web services, consequentemente, podemos utilizá-los para a geração de classes que os representam, assim, facilitando qualquer implementação do ponto de vista de geração dos próprios XML.
